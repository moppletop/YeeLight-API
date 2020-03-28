package com.moppletop.yeelight.api;

import com.moppletop.yeelight.api.manager.YeeLightConnection;
import com.moppletop.yeelight.api.manager.YeeManager;
import com.moppletop.yeelight.api.model.*;
import com.moppletop.yeelight.api.model.YeeColourFlow.YeeColourFlowState;
import com.moppletop.yeelight.api.music.BuiltInMusicServer;
import com.moppletop.yeelight.api.music.MusicServer;
import com.moppletop.yeelight.api.music.RemoteMusicServer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public class YeeApiImpl implements YeeApi {

    private final YeeManager manager;

    @Override
    public void discoverLights(int millisToWait) throws YeeException {
        manager.discoverLights(millisToWait);
    }

    @Override
    public YeeLight getLightBy(int id) {
        YeeLightConnection connection = manager.getLights().get(id);
        return connection == null ? null : connection.getLight().clone();
    }

    @Override
    public Collection<YeeLight> getLights() {
        return manager.getLights().values().stream()
                .map(connection -> connection.getLight().clone())
                .collect(Collectors.toList());
    }

    @Override
    public MusicServer createRemoteMusicServer(String host, int port) {
        return new RemoteMusicServer(host, port);
    }

    @Override
    public MusicServer createBuiltInMusicServer(int port) throws YeeException {
        try {
            return new BuiltInMusicServer(manager, port);
        } catch (IOException e) {
            throw new YeeException(e);
        }
    }

    @Override
    public void setTemperature(int id, int temperature, YeeDuration duration) {
        manager.sendCommand(id, new YeeCommand(
                YeeCommandType.SET_TEMPERATURE,
                temperature,
                duration.getEffect(),
                duration.getMillis()
        ));
    }

    @Override
    public void setRgb(int id, int rgb, YeeDuration duration) {
        manager.sendCommand(id, new YeeCommand(
                YeeCommandType.SET_RGB,
                rgb,
                duration.getEffect(),
                duration.getMillis()
        ));
    }

    @Override
    public void setHsv(int id, int hue, int saturation, YeeDuration duration) {
        manager.sendCommand(id, new YeeCommand(
                YeeCommandType.SET_HSV,
                hue,
                saturation,
                duration.getEffect(),
                duration.getMillis()
        ));
    }

    @Override
    public void setBrightness(int id, int brightness, YeeDuration duration) {
        manager.sendCommand(id, new YeeCommand(
                YeeCommandType.SET_BRIGHTNESS,
                brightness,
                duration.getEffect(),
                duration.getMillis()
        ));
    }

    @Override
    public void setPower(int id, boolean on, YeeDuration duration, YeePowerMode powerMode) {
        manager.sendCommand(id, new YeeCommand(
                YeeCommandType.SET_POWER,
                on ? "on" : "off",
                duration.getEffect(),
                duration.getMillis(),
                powerMode == null ? 0 : powerMode.ordinal()
        ));
    }

    @Override
    public void togglePower(int id) {
        manager.sendCommand(id, new YeeCommand(YeeCommandType.TOGGLE));
    }

    @Override
    public void saveCurrentState(int id) {
        manager.sendCommand(id, new YeeCommand(YeeCommandType.SET_DEFAULT));
    }

    @Override
    public void startColourFlow(int id, YeeColourFlow flow) {
        manager.sendCommand(id, new YeeCommand(
                YeeCommandType.START_COLOUR_FLOW,
                flow.getTotalStateChanges(),
                flow.getEndAction(),
                flow.getStates().stream()
                        .map(YeeColourFlowState::toString)
                        .collect(Collectors.joining(", "))
        ));
    }

    @Override
    public void stopColourFlow(int id) {
        manager.sendCommand(id, new YeeCommand(YeeCommandType.STOP_COLOUR_FLOW));
    }

    @Override
    public void setSleepTimer(int id, int minutesUntilSleep) {
        manager.sendCommand(id, new YeeCommand(
                YeeCommandType.ADD_CRON,
                0,
                minutesUntilSleep
        ));
    }

    @Override
    public void removeSleepTimer(int id) {
        manager.sendCommand(id, new YeeCommand(
                YeeCommandType.DELETE_CRON,
                0
        ));
    }

    @Override
    public void enableMusicMode(int id, MusicServer musicServer) {
        musicServer.handleLight(id);
        manager.sendCommand(id, new YeeCommand(
                YeeCommandType.SET_MUSIC,
                1,
                musicServer.getHost(),
                musicServer.getPort()
        ));
    }

    @Override
    public void disableMusicMode(int id) {
        manager.sendCommand(id, new YeeCommand(
                YeeCommandType.SET_MUSIC,
                0
        ));
    }

    @Override
    public void setName(int id, String name) {
        manager.sendCommand(id, new YeeCommand(
                YeeCommandType.SET_NAME,
                name.length() > 64 ? name.substring(0, 64) : name
        ));
    }
}

package com.moppletop.yeelight.api;

import com.moppletop.yeelight.api.model.YeeColourFlow;
import com.moppletop.yeelight.api.model.YeeDuration;
import com.moppletop.yeelight.api.model.YeeLight;
import com.moppletop.yeelight.api.model.YeePowerMode;
import com.moppletop.yeelight.api.music.MusicServer;
import com.moppletop.yeelight.api.util.ColourUtil;

import java.io.IOException;
import java.util.Collection;

public interface YeeApi {

    /*
        Manager operations
     */

    void discoverLights() throws IOException;

    YeeLight getLightBy(int id);

    Collection<YeeLight> getLights();

    MusicServer createRemoteMusicServer(String host, int port);

    MusicServer createBuiltInMusicServer(int port) throws IOException;

    /*
        Light operations
     */

    void setTemperature(int id, int temperature, YeeDuration duration);

    default void setRgb(int id, int red, int green, int blue, YeeDuration duration) {
        setRgb(id, ColourUtil.toRGB(red, green, blue), duration);
    }

    void setRgb(int id, int rgb, YeeDuration duration);

    void setHsv(int id, int hue, int saturation, YeeDuration duration);

    void setBrightness(int id, int brightness, YeeDuration duration);

    default void setPower(int id, boolean on, YeeDuration duration) {
        setPower(id, on, duration, null);
    }

    void setPower(int id, boolean on, YeeDuration duration, YeePowerMode powerMode);

    void togglePower(int id);

    void saveCurrentState(int id);

    void startColourFlow(int id, YeeColourFlow flow);

    void stopColourFlow(int id);

    // TODO maybe add support for set_scene, not too sure at the moment if it's worth the hassle

    void setSleepTimer(int id, int minutesUntilSleep);

    // TODO maybe add the get? gonna involve fucking around with the async tcp stuff

    void removeSleepTimer(int id);

    // TODO is adjust worth adding since we always have maintain state locally?

    void enableMusicMode(int id, MusicServer musicServer);

    void disableMusicMode(int id);

    void setName(int id, String name);

}

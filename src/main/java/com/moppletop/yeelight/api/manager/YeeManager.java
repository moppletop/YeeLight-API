package com.moppletop.yeelight.api.manager;

import com.moppletop.yeelight.api.YeeConfiguration;
import com.moppletop.yeelight.api.json.JSONProvider;
import com.moppletop.yeelight.api.discovery.DiscoveryUDPListener;
import com.moppletop.yeelight.api.model.YeeCommand;
import com.moppletop.yeelight.api.model.YeeLight;
import com.moppletop.yeelight.api.model.YeeResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.IOException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
public class YeeManager {

    @Getter
    private final YeeConfiguration configuration;
    @Getter
    private final JSONProvider jsonProvider;

    @Getter
    private final Map<Integer, YeeLightConnection> lights = new LinkedHashMap<>();
    private final Map<Integer, YeeCommand> inFlightCommands = new HashMap<>();

    private DiscoveryUDPListener discoveryUDPListener;

    public void start() {
        try {
            discoveryUDPListener = new DiscoveryUDPListener(this);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        Thread thread = new Thread(discoveryUDPListener);
        thread.setName("YeeLight - UDP Listener");
        thread.setDaemon(true);
//        thread.start();
    }

    public boolean registerLight(YeeLight light) throws IOException {
        YeeLightConnection newConnection = new YeeLightConnection(this, light);
        YeeLightConnection oldConnection = lights.put(light.getId(), newConnection);

        if (oldConnection != null) {
            oldConnection.close();
            return false;
        }

        return true;
    }

    public void discoverLights() throws IOException {
        discoveryUDPListener.discoverLights();
    }

    @SneakyThrows
    public void sendCommand(int id, YeeCommand command) {
        YeeLightConnection connection = lights.get(id);

        if (connection == null) {
            throw new IllegalArgumentException("There is no light registered with id " + id);
        } else {
            connection.send(command);
            inFlightCommands.put(command.getId(), command);
        }
    }

    public void readCommand(YeeLight light, YeeResponse response) {
        if (response.isError()) {
            System.err.println(response); // TODO error handling?
        } else if (response.isNotification()) {
            System.out.println("Notification " + response);
            response.getParams().forEach(light::setByParameter);
        } else if (response.isResult()) {
            System.out.println("Result " + response);
            YeeCommand command = inFlightCommands.remove(response.getId());

            // TODO do we need to update our state here? or can we just rely on the Notification?
            if (command == null) {
                System.err.println("Got a response for a command we didn't send " + response);
            }
        } else {
            System.err.println("Invalid response " + response);
        }
    }
}

package com.moppletop.yeelight.api.manager;

import com.moppletop.yeelight.api.YeeConfiguration;
import com.moppletop.yeelight.api.discovery.DiscoveryUDPListener;
import com.moppletop.yeelight.api.json.JSONProvider;
import com.moppletop.yeelight.api.model.YeeCommand;
import com.moppletop.yeelight.api.model.YeeLight;
import com.moppletop.yeelight.api.model.YeeResponse;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The central manager class for all lights
 */
@Slf4j
public class YeeManager {

    @Getter
    private final YeeConfiguration configuration;
    @Getter
    private final JSONProvider jsonProvider;

    private final DiscoveryUDPListener discoveryUDPListener;

    @Getter
    private final Map<Integer, YeeLightConnection> lights = new LinkedHashMap<>();

    public YeeManager(YeeConfiguration configuration, JSONProvider jsonProvider) {
        this.configuration = configuration;
        this.jsonProvider = jsonProvider;
        this.discoveryUDPListener = new DiscoveryUDPListener(this);
    }

    /**
     * Adds a light to the managed list of lights
     *
     * @param light the light to manage
     * @return true if the light was not managed
     */
    public boolean registerLight(YeeLight light) {
        if (lights.containsKey(light.getId())) {
            return false;
        }

        lights.put(light.getId(), new YeeLightConnection(this, light));
        return true;
    }

    public void discoverLights(int millisToWait) throws IOException {
        discoveryUDPListener.discoverLights(millisToWait);
    }

    @SneakyThrows
    public void sendCommand(int id, YeeCommand command) {
        YeeLightConnection connection = lights.get(id);

        if (connection == null) {
            throw new IllegalArgumentException("There is no light registered with id " + id);
        } else {
            connection.send(command);
        }
    }

    public void readCommand(YeeLight light, YeeResponse response) {
        if (response.isError()) {
            log.error("Result [ERROR] {}", response);
        } else if (response.isNotification()) {
            log.debug("Notification " + response);

            // If we've received a Notification (state change), update our local state
            response.getParams().forEach(light::setByParameter);
        } else if (response.isResult()) {
            log.debug("Result " + response);
        } else {
            log.error("Invalid response " + response);
        }
    }
}

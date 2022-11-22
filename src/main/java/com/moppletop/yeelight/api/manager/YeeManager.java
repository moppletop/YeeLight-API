package com.moppletop.yeelight.api.manager;

import com.moppletop.yeelight.api.YeeConfiguration;
import com.moppletop.yeelight.api.YeeException;
import com.moppletop.yeelight.api.discovery.DiscoveryUDPListener;
import com.moppletop.yeelight.api.json.JSONSerializer;
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
    private final JSONSerializer jsonSerializer;

    private final DiscoveryUDPListener discoveryUDPListener;

    @Getter
    private final Map<Integer, YeeLightConnection> lights = new LinkedHashMap<>();

    public YeeManager(YeeConfiguration configuration, JSONSerializer jsonSerializer) {
        this.configuration = configuration;
        this.jsonSerializer = jsonSerializer;
        this.discoveryUDPListener = new DiscoveryUDPListener(this);
    }

    /**
     * Adds a light to the managed list of lights
     *
     * @param light the light to manage
     * @return true if the light was not already managed
     */
    public boolean registerLight(YeeLight light) {
        if (lights.containsKey(light.getId())) {
            return false;
        }

        lights.put(light.getId(), new YeeLightConnection(this, light));
        return true;
    }

    /**
     * @see com.moppletop.yeelight.api.YeeApi#discoverLights(int)
     */
    public void discoverLights(int millisToWait) {
        try {
            discoveryUDPListener.discoverLights(millisToWait);
        } catch (IOException e) {
            throw new YeeException(e);
        }
    }

    /**
     * Sends a command to a light
     *
     * @param id      the id of the light
     * @param command the command to send
     */
    @SneakyThrows
    public void sendCommand(int id, YeeCommand command) {
        YeeLightConnection connection = lights.get(id);

        if (connection == null) {
            throw new IllegalArgumentException("There is no light registered with id " + id);
        } else {
            connection.send(command);
        }
    }

    /**
     * Interprets a response from a light and updates it's internal state if needed
     *
     * @param light    the light the response is for
     * @param response the response
     */
    public void readResponse(YeeLight light, YeeResponse response) {
        if (response.isError()) {
            log.error("Result {}", response);
        } else if (response.isNotification()) {
            log.debug("Notification {}", response);

            // If we've received a Notification (state change), update our local state
            response.getParams().forEach(light::setByParameter);
        } else if (response.isResult()) {
            log.debug("Result {}", response);
        } else {
            log.error("Invalid response {}", response);
        }
    }

    public void shutdown() throws YeeException {
        discoveryUDPListener.close();

        for (YeeLightConnection connection : lights.values()) {
            try {
                connection.close();
            } catch (IOException e) {
                throw new YeeException(e);
            }
        }
    }
}

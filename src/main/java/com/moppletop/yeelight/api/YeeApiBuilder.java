package com.moppletop.yeelight.api;

import com.moppletop.yeelight.api.json.JSONSerialiser;
import com.moppletop.yeelight.api.manager.YeeManager;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Setter
public class YeeApiBuilder {

    private YeeConfiguration configuration;
    private JSONSerialiser jsonSerialiser;
    private boolean autoDiscovery = true;

    @SneakyThrows
    public YeeApi build() {
        if (configuration == null) {
            configuration = YeeConfiguration.emptyConfig();
        }

        if (jsonSerialiser == null) {
            throw new IllegalArgumentException("An implementation of JSONSerialiser must be set in the builder through .jsonSerialiser(...)");
        }

        YeeManager manager = new YeeManager(configuration, jsonSerialiser);

        if (autoDiscovery) {
            manager.discoverLights(1000);
        }

        return new YeeApiImpl(manager);
    }
}

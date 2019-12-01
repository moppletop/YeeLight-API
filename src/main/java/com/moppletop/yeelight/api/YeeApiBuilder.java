package com.moppletop.yeelight.api;

import com.moppletop.yeelight.api.json.JSONProvider;
import com.moppletop.yeelight.api.manager.YeeManager;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Setter
public class YeeApiBuilder {

    private YeeConfiguration configuration;
    private JSONProvider jsonProvider;
    private boolean autoDiscovery = true;

    @SneakyThrows
    public YeeApi build() {
        if (configuration == null) {
            configuration = YeeConfiguration.emptyConfig();
        }

        if (jsonProvider == null) {
            throw new IllegalArgumentException("An implementation of JSONProvider must be set in the builder through .jsonProvider(...)");
        }

        YeeManager manager = new YeeManager(configuration, jsonProvider);
        manager.start();

        if (autoDiscovery) {
            manager.discoverLights();
        }

        return new YeeApiImpl(manager);
    }
}

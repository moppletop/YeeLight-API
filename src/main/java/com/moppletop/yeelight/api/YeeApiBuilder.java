package com.moppletop.yeelight.api;

import com.moppletop.yeelight.api.json.JSONSerializer;
import com.moppletop.yeelight.api.manager.YeeManager;
import com.moppletop.yeelight.api.util.JacksonJSONSerializer;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Setter
public class YeeApiBuilder {

    private YeeConfiguration configuration;
    private JSONSerializer jsonSerializer;
    private boolean autoDiscovery = true;

    @SneakyThrows
    public YeeApi build() {
        if (configuration == null) {
            configuration = YeeConfiguration.builder().build();
        }

        if (jsonSerializer == null) {
            jsonSerializer = JacksonJSONSerializer.INSTANCE;
        }

        YeeManager manager = new YeeManager(configuration, jsonSerializer);

        if (autoDiscovery) {
            manager.discoverLights(1000);
        }

        return new YeeApiImpl(manager);
    }
}

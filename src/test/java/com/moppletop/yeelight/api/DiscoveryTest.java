package com.moppletop.yeelight.api;

import com.moppletop.yeelight.api.util.JacksonJSONProvider;
import com.moppletop.yeelight.api.util.TestUDPServer;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DiscoveryTest {

    private final YeeConfiguration configuration = YeeConfiguration.builder()
            .searchUdpAddress("localhost")
            .searchUdpPort(1982)
            .searchUdpResponsePort(1983)
            .build();
    private final YeeApiImpl api = (YeeApiImpl) new YeeApiBuilder()
            .configuration(configuration)
            .jsonProvider(JacksonJSONProvider.INSTANCE)
            .autoDiscovery(false)
            .build();
    private TestUDPServer server;

    @BeforeEach
    void before() {
        server = new TestUDPServer(configuration.getSearchUdpPort(), configuration.getSearchUdpResponsePort());
    }

    @AfterEach
    void after() {
        server.close();
        api.getManager().shutdown();
    }

    @Test
    @SneakyThrows
    void testDiscovery() {
        api.discoverLights();

        assertThat(api.getLights().isEmpty()).isFalse();
    }
}

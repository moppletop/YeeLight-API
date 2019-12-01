package com.moppletop.yeelight.api;

import com.moppletop.yeelight.api.util.JacksonJSONProvider;
import com.moppletop.yeelight.api.util.TestUDPServer;
import lombok.SneakyThrows;
import org.awaitility.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.awaitility.Awaitility.await;

class DiscoveryTesting {

    private final YeeConfiguration configuration = YeeConfiguration.builder()
            .searchUdpAddress("localhost")
            .searchUdpPort(1982)
            .searchUdpResponsePort(1983)
            .build();
    private final YeeApi api = new YeeApiBuilder()
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
    }

    @Test
    @SneakyThrows
    void testDiscovery() {
        api.discoverLights();

        await().atMost(Duration.FIVE_SECONDS)
                .until(() -> !api.getLights().isEmpty());
    }
}

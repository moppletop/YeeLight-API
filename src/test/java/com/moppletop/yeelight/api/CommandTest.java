package com.moppletop.yeelight.api;

import com.moppletop.yeelight.api.model.YeeDuration;
import com.moppletop.yeelight.api.model.YeeLight;
import com.moppletop.yeelight.api.util.JacksonJSONSerialiser;
import com.moppletop.yeelight.api.util.TestTCPServer;
import lombok.SneakyThrows;
import org.awaitility.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.awaitility.Awaitility.await;

public class CommandTest {

    private final YeeApiImpl api = (YeeApiImpl) new YeeApiBuilder()
            .jsonSerialiser(JacksonJSONSerialiser.INSTANCE)
            .autoDiscovery(false)
            .build();
    private final int port = 25569;
    private final YeeLight light = YeeLight.builder()
            .id(8888)
            .location("yeelight://localhost:" + port)
            .build();

    private TestTCPServer server;

    @BeforeEach
    void before() {
        server = new TestTCPServer(port, api.getManager().getJsonSerialiser());
    }

    @AfterEach
    @SneakyThrows
    void after() {
        server.close();
        api.getManager().shutdown();
    }

    @Test
    @SneakyThrows
    void testSetTemperature() {
        api.getManager().registerLight(light);
        api.setTemperature(light.getId(), 765, YeeDuration.instant());

        await().atMost(Duration.FIVE_SECONDS)
                .until(() -> light.getTemperature() == 765);
    }

}

package com.moppletop.yeelight.api;

import com.moppletop.yeelight.api.util.JacksonJSONSerialiser;
import com.moppletop.yeelight.api.util.TestUDPServer;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;

import static org.assertj.core.api.Assertions.assertThat;

public class DiscoveryTest {

    private YeeApiImpl api;
    private TestUDPServer server;

    @BeforeEach
    void before() throws Exception {
        YeeConfiguration configuration = YeeConfiguration.builder()
                .searchUdpAddress(InetAddress.getLocalHost().getHostAddress())
                .searchUdpPort(1982)
                .searchUdpResponsePort(1983)
                .build();

        api = (YeeApiImpl) new YeeApiBuilder()
                .configuration(configuration)
                .jsonSerialiser(JacksonJSONSerialiser.INSTANCE)
                .autoDiscovery(false)
                .build();
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

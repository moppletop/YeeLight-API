package com.moppletop.yeelight.api;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Getter
@RequiredArgsConstructor
@Builder
public class YeeConfiguration {

    @SneakyThrows(UnknownHostException.class)
    private static InetAddress localHost() {
        return InetAddress.getLocalHost();
    }

    @Builder.Default
    private final String searchUdpAddress = "239.255.255.250";
    @Builder.Default
    private final int searchUdpPort = 1982;
    @Builder.Default
    private final int searchUdpResponsePort = 1983;
    @Builder.Default
    private final InetAddress discoveryBindAddress = localHost();

}

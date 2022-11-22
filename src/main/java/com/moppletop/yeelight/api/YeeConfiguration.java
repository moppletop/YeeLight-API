package com.moppletop.yeelight.api;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class YeeConfiguration {

    @Builder.Default
    private final String searchUdpAddress = "239.255.255.250";
    @Builder.Default
    private final int searchUdpPort = 1982;
    @Builder.Default
    private final int searchUdpResponsePort = 1983;

}

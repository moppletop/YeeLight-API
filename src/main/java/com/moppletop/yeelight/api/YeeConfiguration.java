package com.moppletop.yeelight.api;

import lombok.Builder;
import lombok.Getter;

@Getter
public class YeeConfiguration {

    @SuppressWarnings("unchecked")
    private static <T> T get(T override, String key, T defaultVal) {
        if (override != null) {
            return override;
        }

        String env = System.getenv(key);
        return env == null ? defaultVal : (T) env;
    }

    public static YeeConfiguration emptyConfig() {
        return new YeeConfiguration(null, null, null);
    }

    private final String searchUdpAddress;
    private final int searchUdpPort;
    private final int searchUdpResponsePort;

    @Builder
    YeeConfiguration(String searchUdpAddress, Integer searchUdpPort, Integer searchUdpResponsePort) {
        this.searchUdpAddress = get(searchUdpAddress, "YEE_SEARCH_UDP_ADDRESS", "239.255.255.250");
        // We separate these into two variables so that for the tests we can have two udp servers and clients running on the same machine
        // but when the library is used outside of that context there's never a need, hence we have this check
        if (searchUdpPort == null && searchUdpResponsePort == null) {
            this.searchUdpPort = get(searchUdpPort, "YEE_SEARCH_UDP_PORT", 1982);
            this.searchUdpResponsePort = get(searchUdpResponsePort, "YEE_SEARCH_UDP_RESPONSE_PORT", 25567);
        } else {
            this.searchUdpPort = searchUdpPort;
            this.searchUdpResponsePort = searchUdpResponsePort;
        }
    }
}

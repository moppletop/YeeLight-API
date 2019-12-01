package com.moppletop.yeelight.api.model;

import lombok.Getter;

@Getter
public class YeeDuration {

    private static final YeeDuration INSTANT = new YeeDuration("sudden", 0);

    public static YeeDuration instant() {
        return INSTANT;
    }

    public static YeeDuration seconds(int seconds) {
        return millis(seconds * 1000);
    }

    public static YeeDuration millis(int millis) {
        return new YeeDuration("smooth", millis);
    }

    private final String effect;
    private final int millis;

    private YeeDuration(String effect, int millis) {
        this.effect = effect;
        this.millis = Math.max(millis, 30);
    }
}

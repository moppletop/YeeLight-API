package com.moppletop.yeelight.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum YeeColourMode {

    COLOUR_MODE(1),
    COLOUR_TEMPERATURE(2),
    COLOUR_HSV(3);

    public static YeeColourMode fromId(int id) {
        for (YeeColourMode mode : values()) {
            if (mode.id == id) {
                return mode;
            }
        }

        throw new IllegalArgumentException("Invalid Colour Mode Id: " + id);
    }

    private final int id;

}

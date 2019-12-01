package com.moppletop.yeelight.api.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ColourUtil {

    public int toRGB(int red, int green, int blue) {
        return ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | (blue & 0xFF);
    }

}

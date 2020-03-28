package com.moppletop.yeelight.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum YeeCommandType {

    GET_PROPERTY("get_prop"),
    SET_TEMPERATURE("set_ct_abx"),
    SET_RGB("set_rgb"),
    SET_HSV("set_hsv"),
    SET_BRIGHTNESS("set_bright"),
    SET_POWER("set_power"),
    TOGGLE("toggle"),
    SET_DEFAULT("set_default"),
    START_COLOUR_FLOW("start_cf"),
    STOP_COLOUR_FLOW("stop_cf"),
    ADD_CRON("cron_add"),
    DELETE_CRON("cron_del"),
    SET_MUSIC("set_music"),
    SET_NAME("set_name");

    public static YeeCommandType[] of(String[] methodNames) {
        List<YeeCommandType> commandTypes = new ArrayList<>();

        for (String name : methodNames) {
            for (YeeCommandType commandType : values()) {
                if (commandType.methodName.equals(name)) {
                    commandTypes.add(commandType);
                    break;
                }
            }
        }

        return commandTypes.toArray(new YeeCommandType[0]);
    }

    private final String methodName;

}

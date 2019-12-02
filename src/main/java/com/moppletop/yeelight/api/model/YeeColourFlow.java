package com.moppletop.yeelight.api.model;

import com.moppletop.yeelight.api.util.ColourUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
public class YeeColourFlow {

    public enum YeeColourFlowEndAction {
        RESTORE_STATE, REMAIN_AT_STATE, TURN_OFF
    }

    @Getter
    @RequiredArgsConstructor
    public static class YeeColourFlowState {

        final int duration, stateMode, value, brightness;

        @Override
        public String toString() {
            return duration + ", " + stateMode + ", " + value + ", " + brightness;
        }
    }

    public static YeeColourFlowState colourState(int duration, int red, int green, int blue) {
        return new YeeColourFlowState(duration, 1, ColourUtil.toRGB(red, green, blue), -1);
    }

    public static YeeColourFlowState colourState(int duration, int red, int green, int blue, int brightness) {
        return new YeeColourFlowState(duration, 1, ColourUtil.toRGB(red, green, blue), brightness);
    }

    public static YeeColourFlowState colourState(int duration, int rgb) {
        return new YeeColourFlowState(duration, 1, rgb, -1);
    }

    public static YeeColourFlowState colourState(int duration, int rgb, int brightness) {
        return new YeeColourFlowState(duration, 1, rgb, brightness);
    }

    public static YeeColourFlowState temperatureState(int duration, int rgb) {
        return new YeeColourFlowState(duration, 2, rgb, -1);
    }

    public static YeeColourFlowState temperatureState(int duration, int rgb, int brightness) {
        return new YeeColourFlowState(duration, 2, rgb, brightness);
    }

    public static YeeColourFlowState sleepState(int duration) {
        return new YeeColourFlowState(duration, 7, 0, 0);
    }

    private final int totalStateChanges;
    private final YeeColourFlowEndAction endAction;
    private final List<YeeColourFlowState> states;

    public YeeColourFlow(int totalStateChanges, YeeColourFlowEndAction endAction) {
        this.totalStateChanges = Math.max(totalStateChanges, 0);
        this.endAction = endAction;
        this.states = new ArrayList<>();
    }

    public YeeColourFlow addState(YeeColourFlowState state) {
        states.add(state);
        return this;
    }

}

package com.moppletop.yeelight.api.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

import static java.lang.Integer.parseInt;

@Builder
@Getter
@EqualsAndHashCode
@ToString
public class YeeLight implements Cloneable {

    public static YeeLight of(Map<String, String> packet) {
        return builder()
                .id(parseInt(packet.get("id").substring(2), 16))
                .cacheControl(packet.get("Cache-Control"))
                .location(packet.get("Location"))
                .server(packet.get("Server"))
                .model(packet.get("model"))
                .firmwareVersion(parseInt(packet.get("fw_ver")))
                .supportedCommandTypes(YeeCommandType.of(packet.get("support").split("\\s")))
                .powered(isPowered(packet.get("power")))
                .brightness(parseInt(packet.get("bright")))
                .colourMode(YeeColourMode.fromId(parseInt(packet.get("color_mode"))))
                .temperature(parseInt(packet.getOrDefault("ct", "0")))
                .rgb(parseInt(packet.get("rgb")))
                .hue(parseInt(packet.getOrDefault("hue", "0")))
                .saturation(parseInt(packet.getOrDefault("sat", "0")))
                .name(packet.get("name"))
                .build();
    }

    public static boolean isPowered(String powered) {
        return "on".equals(powered);
    }

    @EqualsAndHashCode.Include
    private int id;

    // Initial state
    private String cacheControl;
    private String location;
    private String server;
    private String model;
    private int firmwareVersion;
    private YeeCommandType[] supportedCommandTypes;
    private boolean powered;
    private int brightness;
    private YeeColourMode colourMode;
    private int temperature;
    private int rgb;
    private int hue;
    private int saturation;
    private String name;

    // Exclusively active state
    private boolean flowing;
    private String flowParameters;
    private int sleepTimer;
    private boolean musicMode;

    public String getHost() {
        return location.substring(location.lastIndexOf('/') + 1, location.lastIndexOf(':'));
    }

    public int getPort() {
        return parseInt(location.substring(location.lastIndexOf(':') + 1));
    }

    public void setByParameter(String key, String value) {
        switch (key) {
            case "power":
                powered = isPowered(value);
                break;
            case "bright":
                brightness = parseInt(value);
                break;
            case "ct":
                temperature = parseInt(value);
                break;
            case "rgb":
                rgb = parseInt(value);
                break;
            case "hue":
                hue = parseInt(value);
                break;
            case "sat":
                saturation = parseInt(value);
                break;
            case "color_mode":
                colourMode = YeeColourMode.fromId(parseInt(value));
                break;
            case "flowing":
                flowing = parseInt(value) == 1;
                break;
            case "flow_params":
                flowParameters = value;
                break;
            case "delayoff":
                sleepTimer = parseInt(value);
                break;
            case "music_mode":
                musicMode = parseInt(value) == 1;
                break;
            case "name":
                name = value;
                break;
        }
    }

    @Override
    public YeeLight clone() {
        try {
            return (YeeLight) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}

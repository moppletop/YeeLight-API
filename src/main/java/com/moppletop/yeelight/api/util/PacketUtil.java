package com.moppletop.yeelight.api.util;

import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@UtilityClass
public class PacketUtil {

    public String getNewLine() {
        return "\r\n";
    }
    private final String NEW_LINE_PATTERN = Pattern.quote(getNewLine());

    public byte[] serialisePacket(String type, String... keyValuePairs) {
        StringBuilder builder = new StringBuilder((keyValuePairs.length + 1) * 16);

        builder.append(type).append(" * HTTP/1.1");

        for (String keyValuePair : keyValuePairs) {
            builder.append(getNewLine()).append(keyValuePair);
        }

        builder.append(getNewLine());

        System.out.println(builder.toString());
        return builder.toString().getBytes();
    }

    public Map<String, String> deserialisePacket(byte[] packetData) {
        Map<String, String> packet = new HashMap<>(32);

        String asString = new String(packetData).trim();
        String[] split = asString.split(NEW_LINE_PATTERN);
        int colonIndex;

        for (String line : split) {
            colonIndex = line.indexOf(':');

            if (colonIndex == -1) {
                continue;
            }

            String key = line.substring(0, colonIndex);

            if (colonIndex + 2 >= line.length()) {
                continue;
            }

            String value = line.substring(colonIndex + 2); // +2 to skip the colon and the extra space

            packet.put(key, value);
        }

        return packet;
    }

    public boolean isEndOfPacket(CharSequence sequence) {
        return sequence.length() > 4 && sequence.subSequence(sequence.length() - 4, sequence.length()).equals(getNewLine());
    }
}

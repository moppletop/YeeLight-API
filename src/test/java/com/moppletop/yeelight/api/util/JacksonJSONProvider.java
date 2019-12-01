package com.moppletop.yeelight.api.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moppletop.yeelight.api.json.JSONProvider;
import lombok.SneakyThrows;

public class JacksonJSONProvider implements JSONProvider {

    public static final JacksonJSONProvider INSTANCE = new JacksonJSONProvider();

    private final ObjectMapper objectMapper;

    private JacksonJSONProvider() {
        this.objectMapper = new ObjectMapper();

        objectMapper.setVisibility(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withIsGetterVisibility(Visibility.NONE)
        );
        objectMapper.setSerializationInclusion(Include.NON_NULL);
    }

    @Override
    @SneakyThrows
    public String serialise(Object obj) {
        return objectMapper.writeValueAsString(obj);
    }

    @Override
    @SneakyThrows
    public <T> T deserialise(String json, Class<T> classOfT) {
        return objectMapper.readValue(json, classOfT);
    }

}

package com.moppletop.yeelight.api.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moppletop.yeelight.api.json.JSONSerialiser;

public class JacksonJSONSerialiser implements JSONSerialiser {

    public static final JacksonJSONSerialiser INSTANCE = new JacksonJSONSerialiser();

    private final ObjectMapper objectMapper;

    private JacksonJSONSerialiser() {
        this.objectMapper = new ObjectMapper();

        objectMapper.setVisibility(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withCreatorVisibility(Visibility.ANY)
                .withSetterVisibility(Visibility.NONE)
                .withFieldVisibility(Visibility.ANY)
                .withGetterVisibility(Visibility.NONE)
                .withIsGetterVisibility(Visibility.NONE)
        );
        objectMapper.setSerializationInclusion(Include.NON_NULL);
    }

    @Override
    public String serialise(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    @Override
    public <T> T deserialise(String json, Class<T> classOfT) throws Exception {
        return objectMapper.readValue(json, classOfT);
    }

}

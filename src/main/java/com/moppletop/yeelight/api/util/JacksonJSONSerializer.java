package com.moppletop.yeelight.api.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moppletop.yeelight.api.json.JSONSerializer;

public class JacksonJSONSerializer implements JSONSerializer {

    public static final JacksonJSONSerializer INSTANCE = new JacksonJSONSerializer();

    private final ObjectMapper objectMapper;

    private JacksonJSONSerializer() {
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
    public byte[] serialise(Object obj) throws Exception {
        return objectMapper.writeValueAsBytes(obj);
    }

    @Override
    public <T> T deserialize(byte[] json, Class<T> classOfT) throws Exception {
        return objectMapper.readValue(json, classOfT);
    }

}

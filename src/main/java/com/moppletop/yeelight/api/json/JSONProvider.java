package com.moppletop.yeelight.api.json;

public interface JSONProvider {

    String serialise(Object obj);

    <T> T deserialise(String json, Class<T> classOfT);

}

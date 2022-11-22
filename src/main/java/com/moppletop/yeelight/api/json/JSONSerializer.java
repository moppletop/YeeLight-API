package com.moppletop.yeelight.api.json;

public interface JSONSerializer {

    byte[] serialise(Object obj) throws Exception;

    <T> T deserialize(byte[] json, Class<T> classOfT) throws Exception;

}

package com.moppletop.yeelight.api.json;

/**
 * An interface defining the contract for JSON serialisation and deserialisation. An implementation of this class must be
 * set when building an instance of {@link com.moppletop.yeelight.api.YeeApi}.
 * <p>
 * This flexibility means that you can choose what implementation of JSON you want to use in your application.
 */
public interface JSONSerialiser {

    String serialise(Object obj) throws Exception;

    <T> T deserialise(String json, Class<T> classOfT) throws Exception;

}

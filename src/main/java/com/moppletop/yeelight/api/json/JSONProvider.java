package com.moppletop.yeelight.api.json;

/**
 * An interface defining the contract for JSON serialisation and deserialisation. An implementation of this class must be
 * set when building an instance of {@link com.moppletop.yeelight.api.YeeApi}.
 *
 * This flexibility means that you can choose what implementation of JSON you want to use in your application.
 */
public interface JSONProvider {

    String serialise(Object obj);

    <T> T deserialise(String json, Class<T> classOfT);

}

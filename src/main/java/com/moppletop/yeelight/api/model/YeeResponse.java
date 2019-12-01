package com.moppletop.yeelight.api.model;

import lombok.Value;

import java.util.Map;

@Value
public class YeeResponse {

    Integer id;
    String method;
    Object[] result;
    Map<String, String> params;
    YeeError error;

    public boolean isError() {
        return error != null;
    }

    public boolean isNotification() {
        return params != null && "props".equals(method);
    }

    public boolean isResult() {
        return result != null;
    }
}

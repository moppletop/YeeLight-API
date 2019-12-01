package com.moppletop.yeelight.api.model;

import lombok.Value;

@Value
public class YeeCommand {

    // TODO is doing this statically really the most sensible idea?
    private static int nextId;

    int id;
    String method;
    Object[] params;

    public YeeCommand(YeeCommandType methodName, Object... params) {
        this.id = nextId++;
        this.method = methodName.getMethodName();
        this.params = params;
    }
}

package com.moppletop.yeelight.api.model;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.concurrent.atomic.AtomicInteger;

@Value
@AllArgsConstructor
public class YeeCommand {

    private static final AtomicInteger NEXT_ID = new AtomicInteger();

    int id;
    String method;
    Object[] params;

    public YeeCommand(YeeCommandType methodName, Object... params) {
        this.id = NEXT_ID.getAndIncrement();
        this.method = methodName.getMethodName();
        this.params = params;
    }
}

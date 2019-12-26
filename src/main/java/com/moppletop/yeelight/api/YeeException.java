package com.moppletop.yeelight.api;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class YeeException extends RuntimeException {

    public YeeException(String message) {
        super(message);
    }

    public YeeException(String message, Throwable cause) {
        super(message, cause);
    }

    public YeeException(Throwable cause) {
        super(cause);
    }

}

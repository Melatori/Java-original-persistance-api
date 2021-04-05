package com.things.jopa.persistance.exceptions;

public class JopaException extends RuntimeException {
    public JopaException() {
    }

    public JopaException(String message) {
        super(message);
    }

    public JopaException(String message, Throwable cause) {
        super(message, cause);
    }

    public JopaException(Throwable cause) {
        super(cause);
    }

    public JopaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

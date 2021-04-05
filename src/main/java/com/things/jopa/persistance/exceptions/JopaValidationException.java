package com.things.jopa.persistance.exceptions;

public class JopaValidationException extends JopaException {
    public JopaValidationException() {
    }

    public JopaValidationException(String message) {
        super(message);
    }

    public JopaValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public JopaValidationException(Throwable cause) {
        super(cause);
    }

    public JopaValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

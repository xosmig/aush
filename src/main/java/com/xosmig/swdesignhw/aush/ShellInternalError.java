package com.xosmig.swdesignhw.aush;

public class ShellInternalError extends RuntimeException {

    public ShellInternalError() {
    }

    public ShellInternalError(String message) {
        super(message);
    }

    public ShellInternalError(String message, Throwable cause) {
        super(message, cause);
    }

    public ShellInternalError(Throwable cause) {
        super(cause);
    }

    public ShellInternalError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

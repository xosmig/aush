package com.xosmig.swdesignhw.aush;

/**
 * Thrown to indicate some unexpected internal error has occurred in the shell.
 */
public class ShellInternalException extends RuntimeException {

    public ShellInternalException() {
    }

    public ShellInternalException(String message) {
        super(message);
    }

    public ShellInternalException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShellInternalException(Throwable cause) {
        super(cause);
    }

    public ShellInternalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

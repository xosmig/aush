package com.xosmig.swdesignhw.aush.parser;

public final class ParseErrorException extends Exception {

    public ParseErrorException(String message) {
        super(message);
    }

    public ParseErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}

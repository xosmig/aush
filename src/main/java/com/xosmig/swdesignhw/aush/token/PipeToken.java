package com.xosmig.swdesignhw.aush.token;

public final class PipeToken implements Token {
    private PipeToken() {
    }

    private static final PipeToken INSTANCE = new PipeToken();

    public static PipeToken get() {
        return INSTANCE;
    }

    @Override
    public <T> T accept(TokenVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String backToString() {
        return "|";
    }

    @Override
    public String toString() {
        return "PipeToken";
    }
}

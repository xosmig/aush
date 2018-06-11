package com.xosmig.swdesignhw.aush.token;

public final class SemicolonToken implements Token {
    private SemicolonToken() {
    }

    private static final SemicolonToken INSTANCE = new SemicolonToken();

    public static SemicolonToken get() {
        return INSTANCE;
    }

    @Override
    public <T> T accept(TokenVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "SemicolonToken";
    }
}

package com.xosmig.swdesignhw.aush.token;

public final class SemicolonToken implements Token {
    private SemicolonToken() {
    }

    private static final SemicolonToken INSTANCE = new SemicolonToken();

    public static SemicolonToken get() {
        return INSTANCE;
    }

    @Override
    public void accept(TokenVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String backToString() {
        return ";";
    }

    @Override
    public String toString() {
        return "SemicolonToken";
    }
}

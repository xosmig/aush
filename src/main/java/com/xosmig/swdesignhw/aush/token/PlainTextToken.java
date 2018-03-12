package com.xosmig.swdesignhw.aush.token;

public final class PlainTextToken implements Token {
    private final String content;

    public PlainTextToken(String content) {
        this.content = content;
    }

    @Override
    public <T> T accept(TokenVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public String getContent() {
        return content;
    }
}

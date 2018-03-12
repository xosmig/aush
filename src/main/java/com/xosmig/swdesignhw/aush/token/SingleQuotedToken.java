package com.xosmig.swdesignhw.aush.token;

public final class SingleQuotedToken implements Token {
    private final String content;

    public SingleQuotedToken(String content) {
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

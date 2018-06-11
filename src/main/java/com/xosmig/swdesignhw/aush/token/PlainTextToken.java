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

    @Override
    public String toString() {
        return "PlainTextToken{" +
                "content='" + content + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PlainTextToken)) return false;

        final PlainTextToken token = (PlainTextToken) obj;

        return content.equals(token.content);
    }

    @Override
    public int hashCode() {
        return content.hashCode();
    }
}

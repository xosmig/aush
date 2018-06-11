package com.xosmig.swdesignhw.aush.token;

import java.util.Objects;

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

    @Override
    public String toString() {
        return "SingleQuotedToken{" +
                "content='" + content + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof SingleQuotedToken)) return false;
        final SingleQuotedToken token = (SingleQuotedToken) obj;
        return Objects.equals(content, token.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }
}

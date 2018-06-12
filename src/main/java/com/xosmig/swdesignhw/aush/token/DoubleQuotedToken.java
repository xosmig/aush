package com.xosmig.swdesignhw.aush.token;

import java.util.Objects;

public final class DoubleQuotedToken implements Token {
    private final CmdString content;

    public DoubleQuotedToken(CmdString content) {
        this.content = content;
    }

    @Override
    public void accept(TokenVisitor visitor) {
        visitor.visit(this);
    }

    public CmdString getContent() {
        return content;
    }

    @Override
    public String backToString() {
        return "\"" + content + "\"";
    }

    @Override
    public String toString() {
        return "DoubleQuotedToken{" +
                "content='" + content + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof DoubleQuotedToken)) return false;
        final DoubleQuotedToken token = (DoubleQuotedToken) obj;
        return Objects.equals(content, token.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }
}

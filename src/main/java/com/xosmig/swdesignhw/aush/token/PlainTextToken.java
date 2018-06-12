package com.xosmig.swdesignhw.aush.token;

public final class PlainTextToken implements Token {
    private final CmdString content;

    public PlainTextToken(CmdString content) {
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
        return content.toString();
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

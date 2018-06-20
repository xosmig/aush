package com.xosmig.swdesignhw.aush.token;

/**
 * Represent a substring of a command line without non-escaped whitespaces.
 */
public final class PlainTextToken implements ConcatenatableToken {
    private final CmdString content;

    /**
     * Constructs a new {@code PlainTextToken} with the given content.
     *
     * @param content content of the new token.
     * @throws IllegalArgumentException if the content contains non-escaped whitespaces.
     */
    public PlainTextToken(CmdString content) {
        for (int i = 0; i < content.length(); i++) {
            if (CmdChar.isNonEscapedWhitespace(content.charAt(i))) {
                throw new IllegalArgumentException("PlainTextToken cannot contain non-escaped whitespaces");
            }
        }
        this.content = content;
    }

    @Override
    public void accept(TokenVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Returns the content of the token. Cannot contain non-escaped whitespaces.
     *
     * @return the content of the token.
     */
    public CmdString getContent() {
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

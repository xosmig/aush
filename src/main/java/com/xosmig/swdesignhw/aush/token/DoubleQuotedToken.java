package com.xosmig.swdesignhw.aush.token;

import java.util.Objects;

/**
 * Represent a substring of a command line, enclosed with double quotes.
 * The content of the token can contain non-escaped whitespaces
 * as opposite to {@code PlainTextToken}
 */
public final class DoubleQuotedToken implements ConcatenatableToken {

    private final CmdString content;

    /**
     * Constructs a new {@code DoubleQuotedToken} with the given content.
     *
     * @param content content of the new token.
     */
    public DoubleQuotedToken(CmdString content) {
        this.content = content;
    }

    @Override
    public void accept(TokenVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Returns the content of the token. The content might contain non-escaped whitespaces.
     *
     * @return the content of the token.
     */
    public CmdString getContent() {
        return content;
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

package com.xosmig.swdesignhw.aush.token;

import java.util.Objects;

/**
 * Represent a substring of a command line, enclosed with single quotes.
 * The content of the token can contain non-escaped whitespaces
 * as opposite to {@code PlainTextToken}
 */
public class SingleQuotedToken implements ConcatenatableToken {

    private final CmdString content;

    public SingleQuotedToken(CmdString content) {
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
        return "SingleQuotedToken{" +
                "content=" + content +
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

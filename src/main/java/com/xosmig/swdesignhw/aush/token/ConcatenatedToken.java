package com.xosmig.swdesignhw.aush.token;

import java.util.Objects;

/**
 * Represents a sequence of tokens concatenated to a single token.
 *
 * Note that it's a common technique to use another instance of <code>ConcatenatedToken</code>
 * as one (preferably, left) or both sub-tokens.
 */
public final class ConcatenatedToken implements ConcatenatableToken {

    private final ConcatenatableToken left;
    private final ConcatenatableToken right;

    private ConcatenatedToken(ConcatenatableToken left, ConcatenatableToken right) {
        this.left = left;
        this.right = right;
    }

    /**
     * Returns a token, which is semantically equal to the two given
     * tokens concatenated with each other.
     * I.e. a token which will be always expanded by the {@code Environment}
     * class to the same string as the two given tokens concatenated to each other.
     *
     * @param left the left part of the resulting token.
     * @param right the right part of the resulting token.
     * @return the result of concatenating.
     * @see com.xosmig.swdesignhw.aush.environment.Environment
     */
    public static ConcatenatableToken concat(ConcatenatableToken left, ConcatenatableToken right) {
        // it's possible avoid concatenating empty tokens here or to concatenate plain text tokens.
        return new ConcatenatedToken(left, right);
    }

    @Override
    public void accept(TokenVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Returns the left part of the token, which can be another {@code ConcatenatedToken}.
     *
     * @return the left part of the token.
     */
    public ConcatenatableToken getLeft() {
        return left;
    }

    /**
     * Returns the right part of the token, which can be another {@code ConcatenatedToken}.
     *
     * @return the right part of the token.
     */
    public ConcatenatableToken getRight() {
        return right;
    }

    @Override
    public String toString() {
        return "{" + left + " @@ " + right + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ConcatenatedToken)) return false;
        final ConcatenatedToken token = (ConcatenatedToken) obj;
        return Objects.equals(left, token.left) &&
                Objects.equals(right, token.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }
}

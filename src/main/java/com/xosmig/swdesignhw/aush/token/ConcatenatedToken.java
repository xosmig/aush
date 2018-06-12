package com.xosmig.swdesignhw.aush.token;

import java.util.Objects;

public final class ConcatenatedToken implements Token {
    private final Token left;
    private final Token right;

    public ConcatenatedToken(Token left, Token right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public void accept(TokenVisitor visitor) {
        visitor.visit(this);
    }

    public Token getLeft() {
        return left;
    }

    public Token getRight() {
        return right;
    }

    @Override
    public String backToString() {
        return getLeft().backToString() + getRight().backToString();
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

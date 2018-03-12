package com.xosmig.swdesignhw.aush.token;

public final class ConcatenatedToken implements Token {
    private final Token left;
    private final Token right;

    public ConcatenatedToken(Token left, Token right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public <T> T accept(TokenVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public Token getLeft() {
        return left;
    }

    public Token getRight() {
        return right;
    }
}

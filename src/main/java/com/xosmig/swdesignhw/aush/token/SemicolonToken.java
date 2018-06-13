package com.xosmig.swdesignhw.aush.token;

/**
 * Special non-concatenatable token for the semicolon (';') symbol.
 */
public final class SemicolonToken implements Token {
    private SemicolonToken() {
    }

    private static final SemicolonToken INSTANCE = new SemicolonToken();

    /**
     * Returns an instance of this class.
     *
     * @return an instance of this class.
     */
    public static SemicolonToken get() {
        return INSTANCE;
    }

    @Override
    public void accept(TokenVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "SemicolonToken";
    }
}

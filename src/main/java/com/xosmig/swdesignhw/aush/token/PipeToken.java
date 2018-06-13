package com.xosmig.swdesignhw.aush.token;

/**
 * Special non-concatenatable token for the pipe ('|') symbol.
 */
public final class PipeToken implements Token {
    private PipeToken() {
    }

    private static final PipeToken INSTANCE = new PipeToken();

    /**
     * Returns an instance of this class.
     *
     * @return an instance of this class.
     */
    public static PipeToken get() {
        return INSTANCE;
    }

    @Override
    public void accept(TokenVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "PipeToken";
    }
}

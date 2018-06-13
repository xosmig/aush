package com.xosmig.swdesignhw.aush.token;

/**
 * The visitor interface for the {@code Token} class.
 * Since this interface is used in many different contexts, it doesn't make much
 * sense to return anything from the {@code visit} method, nor to accept any
 * additional parameters.
 * Use internal state instead.
 */
public interface TokenVisitor {

    void visit(DoubleQuotedToken token);

    void visit(PlainTextToken token);

    void visit(ConcatenatedToken token);

    void visit(SemicolonToken token);

    void visit(PipeToken token);
}

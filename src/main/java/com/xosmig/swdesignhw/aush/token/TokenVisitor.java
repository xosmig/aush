package com.xosmig.swdesignhw.aush.token;

public interface TokenVisitor {
    void visit(DoubleQuotedToken token);

    void visit(PlainTextToken token);

    void visit(ConcatenatedToken token);

    void visit(SemicolonToken token);

    void visit(PipeToken token);
}

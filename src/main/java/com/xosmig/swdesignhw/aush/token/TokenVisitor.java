package com.xosmig.swdesignhw.aush.token;

public interface TokenVisitor<T> {
    T visit(DoubleQuotedToken token);

    T visit(SingleQuotedToken token);

    T visit(PlainTextToken token);

    T visit(ConcatenatedToken token);
}

package com.xosmig.swdesignhw.aush.token;

/**
 * A single token of a command line, i.e. a piece of the line treated as a single
 * command or argument.
 * A command line consist of a sequence of tokens separated by whitespaces
 * or special tokens (such as {@code SemicolonToken}).
 *
 * @see TokenVisitor
 */
public interface Token {

    /**
     * Visitor pattern is used.
     * This method's implementations call {@code visitor.visit(this)}.
     *
     * @param visitor the visitor.
     */
    void accept(TokenVisitor visitor);
}

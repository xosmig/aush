package com.xosmig.swdesignhw.aush.environment;

/**
 * Should be created only as a result of token expansion.
 * Content of a word is always treated as a whole (e.g. as a single argument to a command,
 * or a single command name) regardless any separators it contains.
 */
public final class Word {
    private final String content;

    public Word(String content) {
        this.content = content;
    }

    public static Word concat(Word left, Word right) {
        return new Word(left.getContent() + right.getContent());
    }

    public String getContent() {
        return content;
    }
}

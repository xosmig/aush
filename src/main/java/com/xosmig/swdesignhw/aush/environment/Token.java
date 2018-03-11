package com.xosmig.swdesignhw.aush.environment;

public final class Token {
    private boolean singleWord;
    private boolean expandVariables;
    private String content;

    private Token(boolean singleWord, boolean expandVariables, String content) {
        this.singleWord = singleWord;
        this.expandVariables = expandVariables;
        this.content = content;
    }

    public boolean isSingleWord() {
        return singleWord;
    }

    public boolean expandVariables() {
        return !expandVariables;
    }

    public String getContent() {
        return content;
    }

    public static Token notQuoted(String content) {
        return new Token(false, true, content);
    }

    public static Token doubleQuoted(String content) {
        return new Token(true, true, content);
    }

    public static Token singleQuoted(String content) {
        return new Token(true, false, content);
    }
}

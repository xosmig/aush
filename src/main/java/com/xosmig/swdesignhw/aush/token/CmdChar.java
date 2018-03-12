package com.xosmig.swdesignhw.aush.token;

public final class CmdChar {
    final char ch;
    final boolean escaped;

    public CmdChar(char ch, boolean escaped) {
        this.ch = ch;
        this.escaped = escaped;
    }

    public char getChar() {
        return ch;
    }

    public boolean isEscaped() {
        return escaped;
    }
}

package com.xosmig.swdesignhw.aush.token;

import java.util.Objects;

public final class CmdChar {
    private final char ch;
    private final boolean escaped;

    private CmdChar(char ch, boolean escaped) {
        this.ch = ch;
        this.escaped = escaped;
    }

    // possible to add caching in future
    public static CmdChar get(char ch, boolean escaped) {
        return new CmdChar(ch, escaped);
    }

    public char getCh() {
        return ch;
    }

    public boolean isEscaped() {
        return escaped;
    }

    @Override
    public String toString() {
        return (isEscaped() ? "\\" : "") + getCh();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CmdChar)) return false;
        final CmdChar aChar = (CmdChar) obj;
        return ch == aChar.ch && escaped == aChar.escaped;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ch, escaped);
    }

    public static boolean isWhitespace(CmdChar ch) {
        return !ch.isEscaped() && Character.isWhitespace(ch.getCh());
    }
}

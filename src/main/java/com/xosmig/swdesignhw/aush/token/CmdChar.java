package com.xosmig.swdesignhw.aush.token;

import java.util.Objects;

/**
 * Represents a pair of a character and a boolean flag indicating whether
 * this character is escaped (e.g. with a backslash) or not.
 *
 * Use <code>CmdChar.get(char, bool)</code> to create new instances.
 *
 * This class is immutable, thus it's safe to reuse the objects. Even across multiple threads.
 *
 * @see CmdString
 * @see CmdStringBuilder
 */
public final class CmdChar {

    private final char ch;
    private final boolean escaped;

    private CmdChar(char ch, boolean escaped) {
        this.ch = ch;
        this.escaped = escaped;
    }

    /**
     * Returns a <code>CmdChar</code> with the given character and the flag.
     * Not guaranteed to neither return new instances on each call nor to reuse objects.
     *
     * @param ch the character.
     * @param escaped the escaping flag.
     * @return an instance with the given character and the flag.
     */
    public static CmdChar get(char ch, boolean escaped) {
        return new CmdChar(ch, escaped);
    }

    /**
     * Returns the underlying character.
     *
     * @return the character.
     */
    public char getCh() {
        return ch;
    }

    /**
     * Returns the escaping flag.
     *
     * @return the escaping flag.
     */
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

    /**
     * Returns <code>true</code> if the character is a whitespace and the escaping flag is not set,
     * <code>false</code> otherwise.
     *
     * @param ch the object.
     * @return <code>true</code> if the character is a whitespace and the escaping flag is not set,
     * <code>false</code> otherwise.
     */
    public static boolean isNonEscapedWhitespace(CmdChar ch) {
        return !ch.isEscaped() && Character.isWhitespace(ch.getCh());
    }
}

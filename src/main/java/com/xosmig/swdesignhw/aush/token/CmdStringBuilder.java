package com.xosmig.swdesignhw.aush.token;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to construct a {@code CmdString}.
 *
 * @see CmdString
 * @see CmdChar
 * @see StringBuilder
 */
public final class CmdStringBuilder {

    private final List<CmdChar> data = new ArrayList<>();

    /**
     * Append the {@code CmdChar} to the sequence.
     *
     * @param ch the {@code CmdChar} to be appended.
     */
    public void append(CmdChar ch) {
        data.add(ch);
    }

    /**
     * Constructs a {@code CmdChar} and appends it to the sequence.
     * See the documentation for {@code CmdChar.get(char, escaped)}.
     *
     * @param ch the character.
     * @param escaped the escaping flag.
     */
    public void append(char ch, boolean escaped) {
        append(CmdChar.get(ch, escaped));
    }

    /**
     * Finishes the construction and returns the resulting {@code CmdString}.
     * The sequence is NOT cleaned nor changed and you can proceed using this
     * {@code CmdStringBuilder} after the call to {@code finish()}.
     * Use {@code clear()} method if you want to start constructing a new {@code CmdString}
     * instead of proceeding appending to the same sequence.
     *
     * @return the resulting {@code CmdString}.
     */
    public CmdString finish() {
        return new CmdString(data);
    }

    /**
     * Cleans the buffer. Use this method if you want to start
     * constructing a new {@code CmdString}.
     */
    public void clear() {
        data.clear();
    }

    /**
     * Returns <tt>true</tt> if the buffer is currently empty.
     *
     * @return <tt>true</tt> if the buffer is empty.
     */
    public boolean isEmpty() {
        return data.isEmpty();
    }
}

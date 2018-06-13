package com.xosmig.swdesignhw.aush.token;

import java.util.List;

/**
 * Represents a string with an escaping flag for each character.
 * The api of this class is intended to be similar to the api
 * of the default {@code String} class with the exception
 * that {@code CmdChar} class is used instead of {@code char}.
 *
 * This class is immutable.
 *
 * @see CmdChar
 * @see CmdStringBuilder
 */
public final class CmdString {

    private final CmdChar[] data;
    private final int begin;
    private final int end;

    /**
     * Creates a new <code>CmdString</code> with the given sequence of characters.
     *
     * @param data the characters to form a new <code>CmdString</code>.
     */
    public CmdString(List<CmdChar> data) {
        this(data.toArray(new CmdChar[]{}), 0, data.size());
    }

    private CmdString(CmdChar[] data, int begin, int end) {
        this.data = data;
        this.begin = begin;
        this.end = end;
    }

    /**
     * Converts <code>text</code> to a <code>CmdString</code>.
     * A character in the resulting <code>CmdString</code> is considered escaped
     * if and only if there is a non-escaped backslash right before this character.
     *
     * Unfortunately, I don't know how to write readable examples with javadoc,
     * so I won't bother trying.
     *
     * @param text text to be parsed.
     * @return the resulting <code>CmdString</code>.
     * @throws IllegalArgumentException if <code>text</code> ends with a backslash.
     */
    public static CmdString parse(String text) {
        final CmdStringBuilder builder = new CmdStringBuilder();
        for (int i = 0; i < text.length(); ) {
            if (text.charAt(i) == '\\') {
                if (i + 1 == text.length()) {
                    throw new IllegalArgumentException("Unexpected end of line after backslash");
                }
                builder.append(text.charAt(i + 1), true);
                i += 2;
            } else {
                builder.append(text.charAt(i), false);
                i += 1;
            }
        }
        return builder.finish();
    }

    /**
     * Returns the length of this string.
     * The length is equal to the number of characters in the string.
     *
     * @return  the length of the sequence of characters represented by this object.
     */
    public int length() {
        return end - begin;
    }

    /**
     * Returns the {@code CmdChar} value at the
     * specified index. An index ranges from {@code 0} to
     * {@code length() - 1}. The first {@code CmdChar} value of the sequence
     * is at index {@code 0}, the next at index {@code 1},
     * and so on, as for array indexing.
     *
     * @param      index   the index of the {@code CmdChar} value.
     * @return     the {@code CmdChar} value at the specified index of this string.
     *             The first {@code CmdChar} value is at index {@code 0}.
     * @exception  IndexOutOfBoundsException  if the {@code index}
     *             argument is negative or not less than the length of this
     *             string.
     */
    public CmdChar charAt(int index) {
        return data[begin + index];
    }

    /**
     * Returns a string that is a substring of this string. The
     * substring begins with the character at the specified index and
     * extends to the end of this string.
     *
     * @param      beginIndex   the beginning index, inclusive.
     * @return     the specified substring.
     * @exception  IndexOutOfBoundsException  if
     *             {@code beginIndex} is negative or larger than the
     *             length of this {@code CmdString} object.
     */
    public CmdString substring(int beginIndex) {
        return substring(beginIndex, length());
    }

    /**
     * Returns a {@code CmdString} that is a substring of this string. The
     * substring begins at the specified {@code beginIndex} and
     * extends to the character at index {@code endIndex - 1}.
     * Thus the length of the substring is {@code endIndex-beginIndex}.
     *
     * @param      beginIndex   the beginning index, inclusive.
     * @param      endIndex     the ending index, exclusive.
     * @return     the specified substring.
     * @exception  IndexOutOfBoundsException  if the
     *             {@code beginIndex} is negative, or
     *             {@code endIndex} is larger than the length of
     *             this {@code CmdString} object, or
     *             {@code beginIndex} is larger than
     *             {@code endIndex}.
     */
    public CmdString substring(int beginIndex, int endIndex) {
        if (endIndex > length()) {
            throw new IndexOutOfBoundsException("Invalid endIndex " + endIndex);
        }
        if (beginIndex < 0 || beginIndex > endIndex) {
            throw new IndexOutOfBoundsException("Invalid beginIndex " + beginIndex);
        }

        return new CmdString(this.data, begin + beginIndex, begin + endIndex);
    }

    /**
     * Returns the index within this {@code CmdString} of the first occurrence of
     * the specified {@code CmdChar}. If no such {@code CmdChar} occurs in this
     * string, then {@code -1} is returned.
     * When read the documentation for the standard library: https://www.youtube.com/watch?v=xOrgLj9lOwk
     *
     * @param   ch   a {@code CmdChar}.
     * @return  the index of the first occurrence of the {@code CmdChar} in this {@code CmdString},
     *          or {@code -1} if an equal {@code CmdChar} does not occur.
     */
    public int indexOf(CmdChar ch) {
        for (int i = 0; i < length(); i++) {
            if (charAt(i).equals(ch)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the index within this {@code CmdString} of the last occurrence of
     * the specified {@code CmdChar}. If no such {@code CmdChar} occurs in this
     * string, then {@code -1} is returned.
     *
     * @param   ch   a {@code CmdChar}.
     * @return  the index of the last occurrence of the {@code CmdChar} in this {@code CmdString},
     *          or {@code -1} if an equal {@code CmdChar} does not occur.
     */
    public int lastIndexOf(CmdChar ch) {
        for (int i = length() - 1; i >= 0; i--) {
            if (charAt(i).equals(ch)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length(); i++) {
            sb.append(charAt(i));
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CmdString)) return false;
        final CmdString str = (CmdString) obj;
        if (length() != str.length()) return false;
        for (int i = 0; i < length(); i++) {
            if (!charAt(i).equals(str.charAt(i))) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 123;
        for (int i = 0; i < length(); i++) {
            result = 31 * result + charAt(i).hashCode();
        }
        return result;
    }

    /**
     * Returns {@code true} if there are at least one escaped symbol in this {@code CmdString},
     * {@code false} otherwise.
     *
     * @param str the {@code CmdString} to be checked.
     * @return {@code true} if there are at least one escaped symbol in this {@code CmdString},
     *          {@code false} otherwise.
     */
    public static boolean containsEscapedSymbols(CmdString str) {
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i).isEscaped()) {
                return true;
            }
        }
        return false;
    }
}

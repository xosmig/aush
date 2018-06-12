package com.xosmig.swdesignhw.aush.token;


public final class CmdString {
    private final CmdChar[] data;
    private final int begin;
    private final int end;

    public CmdString(CmdChar[] data) {
        this(data, 0, data.length);
    }

    private CmdString(CmdChar[] data, int begin, int end) {
        this.data = data;
        this.begin = begin;
        this.end = end;
    }

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

    public int length() {
        return end - begin;
    }

    public CmdChar charAt(int idx) {
        return data[begin + idx];
    }

    public CmdString substring(int beginIndex) {
        return substring(beginIndex, length());
    }

    public CmdString substring(int beginIndex, int endIndex) {
        if (endIndex > length()) {
            throw new IllegalArgumentException("Invalid endIndex");
        }
        if (beginIndex < 0 || beginIndex > endIndex) {
            throw new IllegalArgumentException("Invalid beginIndex " + beginIndex);
        }

        return new CmdString(this.data, begin + beginIndex, begin + endIndex);
    }

    public int indexOf(CmdChar ch) {
        for (int i = 0; i < length(); i++) {
            if (charAt(i).equals(ch)) {
                return i;
            }
        }
        return -1;
    }

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

    public static boolean containsEscapedSymbols(CmdString str) {
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i).isEscaped()) {
                return true;
            }
        }
        return false;
    }
}

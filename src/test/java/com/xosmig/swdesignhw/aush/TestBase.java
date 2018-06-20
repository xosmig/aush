package com.xosmig.swdesignhw.aush;

import com.xosmig.swdesignhw.aush.token.*;

public class TestBase {
    protected static PlainTextToken plainText(String text) {
        return new PlainTextToken(CmdString.parse(text));
    }

    protected static DoubleQuotedToken doubleQuoted(String text) {
        return new DoubleQuotedToken(CmdString.parse(text));
    }

    protected static ConcatenatableToken concat(ConcatenatableToken left, ConcatenatableToken right) {
        return ConcatenatedToken.concat(left, right);
    }

    protected static String fromUnixStr(String str) {
        return str.replace("\n", System.lineSeparator());
    }
}

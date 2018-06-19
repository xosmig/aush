package com.xosmig.swdesignhw.aush;

import com.xosmig.swdesignhw.aush.token.CmdString;
import com.xosmig.swdesignhw.aush.token.DoubleQuotedToken;
import com.xosmig.swdesignhw.aush.token.PlainTextToken;

public class TestBase {
    protected static PlainTextToken plainText(String text) {
        return new PlainTextToken(CmdString.parse(text));
    }

    protected static DoubleQuotedToken doubleQuoted(String text) {
        return new DoubleQuotedToken(CmdString.parse(text));
    }

    protected static String unixStr(String str) {
        return str.replace("\n", System.lineSeparator());
    }
}

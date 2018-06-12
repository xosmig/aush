package com.xosmig.swdesignhw.aush;

import com.xosmig.swdesignhw.aush.token.CmdString;
import com.xosmig.swdesignhw.aush.token.DoubleQuotedToken;
import com.xosmig.swdesignhw.aush.token.PlainTextToken;

public class TestBase {
    protected PlainTextToken plainText(String text) {
        return new PlainTextToken(CmdString.parse(text));
    }

    protected DoubleQuotedToken doubleQuoted(String text) {
        return new DoubleQuotedToken(CmdString.parse(text));
    }
}

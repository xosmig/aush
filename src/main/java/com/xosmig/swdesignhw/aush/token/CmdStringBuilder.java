package com.xosmig.swdesignhw.aush.token;

import java.util.ArrayList;
import java.util.List;

public final class CmdStringBuilder {
    private final List<CmdChar> data = new ArrayList<>();

    public void append(CmdChar ch) {
        data.add(ch);
    }

    public void append(char ch, boolean escaped) {
        append(CmdChar.get(ch, escaped));
    }

    public CmdString finish() {
        return new CmdString(data.toArray(new CmdChar[]{}));
    }

    public void clear() {
        data.clear();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }
}

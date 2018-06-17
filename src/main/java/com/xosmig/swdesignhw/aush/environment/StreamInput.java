package com.xosmig.swdesignhw.aush.environment;

import com.xosmig.swdesignhw.aush.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamInput implements Input {
    private final InputStream ins;

    private StreamInput(InputStream ins) {
        this.ins = ins;
    }

    public static Input get(InputStream ins) {
        if (ins == System.in) {
            return Inherit.input();
        }
        return new StreamInput(ins);
    }

    @Override
    public InputStream inputStream() {
        return ins;
    }

    @Override
    public ProcessBuilder.Redirect getRedirect() {
        return ProcessBuilder.Redirect.PIPE;
    }

    @Override
    public void doRedirection(OutputStream processInput) throws IOException {
        try {
            Utils.redirectStream(ins, processInput);
        } finally {
            ins.close();
            processInput.close();
        }
    }
}

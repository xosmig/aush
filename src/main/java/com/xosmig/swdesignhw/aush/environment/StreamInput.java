package com.xosmig.swdesignhw.aush.environment;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
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
    public ProcessBuilder.Redirect getRedirect() {
        return ProcessBuilder.Redirect.PIPE;
    }

    @Override
    public void doRedirection(OutputStream processInput) throws IOException {
        try {
            while (true) {
                if (Thread.interrupted()) {
                    throw new InterruptedIOException();
                }
                int b = ins.read();
                if (b == -1) {
                    break;
                }
                processInput.write(b);
            }
        } finally {
            ins.close();
            processInput.close();
        }
    }
}

package com.xosmig.swdesignhw.aush.environment;

import java.io.*;


public final class Pipe {
    private final CloseableInput input;
    private final CloseableOutput output;

    private Pipe(CloseableInput input, CloseableOutput output) {
        this.input = input;
        this.output = output;
    }

    public static Pipe get() {
        // this seems to be the only cross-platform approach, although it's not really efficient
        PipedInputStream ins = new PipedInputStream();
        PipedOutputStream outs = new PipedOutputStream();

        try {
            ins.connect(outs);
        } catch (IOException e) {
            // should be unreachable
            e.printStackTrace();
            System.exit(1);
        }

        return new Pipe(new StreamInput(ins), new StreamOutput(outs));
    }

    public CloseableInput getInput() {
        return input;
    }

    public CloseableOutput getOutput() {
        return output;
    }
}

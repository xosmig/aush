package com.xosmig.swdesignhw.aush.environment;

import java.io.*;


public final class Pipe {
    private final Input input;
    private final Output output;

    private Pipe(Input input, Output output) {
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

        return new Pipe(StreamInput.get(ins), StreamOutput.get(outs));
    }

    public Input getInput() {
        return input;
    }

    public Output getOutput() {
        return output;
    }
}

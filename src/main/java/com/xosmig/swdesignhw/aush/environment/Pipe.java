package com.xosmig.swdesignhw.aush.environment;

import java.io.*;

/**
 * Used to instruct the shell to pipe the output of one command to the input of another command.
 * All the data written to the {@code Output} object returned by {@code getOutput()} will be
 * forwarded to the {@code Input} object returned by {@code getInput()}.
 */
public final class Pipe {

    private final CloseableInput input;
    private final CloseableOutput output;

    private Pipe(CloseableInput input, CloseableOutput output) {
        this.input = input;
        this.output = output;
    }

    /**
     * Returns a {@code Pipe} object. See class-level documentation.
     */
    public static Pipe get() {
        // this seems to be the only cross-platform approach, although it's not really efficient
        // Do not try to reuse streams, since they might be closed.
        PipedInputStream ins = new PipedInputStream();
        PipedOutputStream outs = new PipedOutputStream();

        try {
            ins.connect(outs);
        } catch (IOException e) {
            // should be unreachable
            e.printStackTrace();
            System.exit(1);
        }

        return new Pipe(StreamInput.getCloseable(ins), StreamOutput.getCloseable(outs));
    }

    /**
     * Returns the object for the read end of the pipe.
     */
    public CloseableInput getInput() {
        return input;
    }

    /**
     * Returns the object for the write end of the pipe.
     */
    public CloseableOutput getOutput() {
        return output;
    }
}

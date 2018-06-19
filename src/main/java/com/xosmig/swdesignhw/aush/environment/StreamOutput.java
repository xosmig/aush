package com.xosmig.swdesignhw.aush.environment;

import com.xosmig.swdesignhw.aush.utils.Utils;

import java.io.*;

public class StreamOutput implements CloseableOutput {
    private final PrintStream outs;

    public StreamOutput(PrintStream outs) {
        this.outs = outs;
    }

    public StreamOutput(OutputStream outs) {
        this(outs instanceof PrintStream ? (PrintStream) outs : new PrintStream(outs));
    }

    public static Output get(OutputStream outs) {
        if (outs == System.out) {
            return Inherit.output();
        }
        return new StreamOutput(outs);
    }

    @Override
    public ProcessBuilder.Redirect getRedirect() {
        return ProcessBuilder.Redirect.PIPE;
    }

    @Override
    public void doRedirection(InputStream processOutput) throws IOException {
        try {
            Utils.redirectStream(processOutput, outs);
        } finally {
            processOutput.close();
        }
    }

    @Override
    public PrintStream printStream() {
        return outs;
    }

    @Override
    public void close() throws IOException {
        outs.close();
    }
}

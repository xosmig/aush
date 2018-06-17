package com.xosmig.swdesignhw.aush.environment;

import com.xosmig.swdesignhw.aush.utils.Utils;

import java.io.*;

public class StreamOutput implements Output {
    private final PrintStream outs;

    private StreamOutput(PrintStream outs) {
        this.outs = outs;
    }

    public static Output get(OutputStream outs) {
        if (outs == System.out) {
            return Inherit.output();
        }
        return new StreamOutput(outs instanceof PrintStream ? (PrintStream) outs : new PrintStream(outs));
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
            outs.close();
            processOutput.close();
        }
    }

    @Override
    public PrintStream printStream() {
        return outs;
    }
}

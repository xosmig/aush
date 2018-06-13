package com.xosmig.swdesignhw.aush.environment;

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
            while (true) {
                if (Thread.interrupted()) {
                    throw new InterruptedIOException();
                }
                int b = processOutput.read();
                if (b == -1) {
                    break;
                }
                outs.write(b);
            }
        } finally {
            outs.close();
            processOutput.close();
        }
    }

    @Override
    public void println(Object obj) {
        outs.println(obj);
    }
}

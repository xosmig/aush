package com.xosmig.swdesignhw.aush.environment;

import com.xosmig.swdesignhw.aush.utils.Utils;

import java.io.*;

/**
 * Used to instruct the shell to provide the commands with input from the given {@code OutputStream}.
 * Constructor methods ({@code get} and {@code getCloseable}) do not guarantee to return
 * an instance of {@code StreamOutput}. Other implementations might be chosen.
 */
public class StreamOutput implements CloseableOutput {

    private final PrintStream outs;

    private StreamOutput(PrintStream outs) {
        this.outs = outs;
    }

    private StreamOutput(OutputStream outs) {
        this(outs instanceof PrintStream ? (PrintStream) outs : new PrintStream(outs));
    }

    /**
     * Returns an instance of {@code CloseableOutput} interface with the semantic,
     * described in the class-level documentation.
     * Does not guarantee to return an instance of {@code StreamOutput}.
     * Another implementation might be chosen.
     *
     * @param outs {@code OutputStream} to which the output will be written.
     * @return     a {@code CloseableOutput} object.
     */
    public static CloseableOutput getCloseable(OutputStream outs) {
        return new StreamOutput(outs);
    }

    /**
     * Returns an instance of {@code Output} interface with the semantic,
     * described in the class-level documentation.
     * Does not guarantee to return an instance of {@code StreamOutput}.
     * Another implementation might be chosen.
     *
     * @param outs {@code OutputStream} to which the output will be written.
     * @return     an {@code Output} object.
     */
    public static Output get(OutputStream outs) {
        if (outs == System.out) {
            return Inherit.output();
        }
        return new StreamOutput(outs);
    }

    @Override
    public void prepare(ProcessBuilder processBuilder) {
        processBuilder.redirectOutput(ProcessBuilder.Redirect.PIPE);
    }

    @Override
    public void doRedirection(Process process) throws IOException {
        try {
            Utils.redirectStream(process.getInputStream(), outs);
        } finally {
            process.getInputStream().close();
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

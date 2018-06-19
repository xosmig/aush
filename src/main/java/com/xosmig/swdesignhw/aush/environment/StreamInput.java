package com.xosmig.swdesignhw.aush.environment;

import com.xosmig.swdesignhw.aush.utils.Utils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Used to instruct the shell to provide the commands with input from the given {@code InputStream}.
 * Constructor methods ({@code get} and {@code getCloseable}) do not guarantee to return
 * an instance of {@code StreamInput}. Other implementations might be chosen.
 */
public class StreamInput implements CloseableInput {

    private final InputStream ins;

    private StreamInput(InputStream ins) {
        this.ins = ins;
    }

    /**
     * Returns an instance of {@code CloseableInput} interface with the semantic,
     * described in the class-level documentation.
     * Does not guarantee to return an instance of {@code StreamInput}.
     * Another implementation might be chosen.
     *
     * @param ins {@code InputStream} from which the input will be read.
     * @return    a {@code CloseableInput} object.
     */
    public static CloseableInput getCloseable(InputStream ins) {
        return new StreamInput(ins);
    }

    /**
     * Returns an instance of {@code Input} interface with the semantic,
     * described in the class-level documentation.
     * Does not guarantee to return an instance of {@code StreamInput}.
     * Another implementation might be chosen.
     *
     * @param ins {@code InputStream} from which the input will be read.
     * @return    an {@code Input} object.
     */
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
    public void prepare(ProcessBuilder processBuilder) {
        processBuilder.redirectInput(ProcessBuilder.Redirect.PIPE);
    }

    @Override
    public void doRedirection(Process process) throws IOException {
        try {
            Utils.redirectStream(ins, process.getOutputStream());
        } finally {
            process.getOutputStream().close();
        }
    }

    /**
     * Closes the underlying {@code InputStream}.
     */
    @Override
    public void close() throws IOException {
        ins.close();
    }
}

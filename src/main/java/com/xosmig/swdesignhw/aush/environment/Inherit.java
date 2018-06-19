package com.xosmig.swdesignhw.aush.environment;

import java.io.InputStream;
import java.io.PrintStream;

/**
 * {@code Inherit.input()} and {@code Inherit.output} should be used when you want for the command
 * in the shell to inherit process's input or output.
 */
public final class Inherit {

    private Inherit() {
    }

    private static final Input INPUT = new InheritInput();
    private static final Output OUTPUT = new InheritOutput();

    /**
     * Returns a special {@code Input} object which reads the data from {@code System.in}.
     */
    public static Input input() {
        return INPUT;
    }

    /**
     * Returns a special {@code Output} object which writes the data to {@code System.out}.
     */
    public static Output output() {
        return OUTPUT;
    }

    private static class InheritInput implements Input {
        @Override
        public void prepare(ProcessBuilder processBuilder) {
            processBuilder.redirectInput(ProcessBuilder.Redirect.INHERIT);
        }

        @Override
        public void doRedirection(Process process) {
            // no actions required
        }

        @Override
        public InputStream inputStream() {
            return System.in;
        }
    }

    private static class InheritOutput implements Output {
        @Override
        public void prepare(ProcessBuilder processBuilder) {
            processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        }

        @Override
        public void doRedirection(Process process) {
            // no actions required
        }

        @Override
        public PrintStream printStream() {
            return System.out;
        }
    }
}

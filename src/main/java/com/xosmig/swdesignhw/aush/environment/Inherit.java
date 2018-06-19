package com.xosmig.swdesignhw.aush.environment;

import com.xosmig.swdesignhw.aush.ShellInternalException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public final class Inherit {
    private Inherit() {
    }

    private static final Input INPUT = new InheritInput();
    private static final Output OUTPUT = new InheritOutput();

    public static Input input() {
        return INPUT;
    }

    public static Output output() {
        return OUTPUT;
    }

    private static class InheritInput implements Input {
        @Override
        public ProcessBuilder.Redirect getRedirect() {
            return ProcessBuilder.Redirect.INHERIT;
        }

        @Override
        public void doRedirection(OutputStream processInput) {
        }

        @Override
        public InputStream inputStream() {
            return System.in;
        }
    }

    private static class InheritOutput implements Output {
        @Override
        public ProcessBuilder.Redirect getRedirect() {
            return ProcessBuilder.Redirect.INHERIT;
        }

        @Override
        public void doRedirection(InputStream processOutput) {
        }

        @Override
        public PrintStream printStream() {
            return System.out;
        }
    }
}

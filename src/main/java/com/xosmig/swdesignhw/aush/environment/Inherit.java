package com.xosmig.swdesignhw.aush.environment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
        public void println(Object obj) {
            System.out.println(obj);
        }
    }
}

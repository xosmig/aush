package com.xosmig.swdesignhw.aush.environment;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Basic abstraction for output destination in the shell implementation.
 * It might be used either to redirect output from a process, created by shell
 * (via methods {@code prepare()} and {@code doRedirection()}),
 * or just to write output inside the shell itself, or one of the builtin commands
 * (via method {@code printStream()}).
 */
public interface Output {

    /**
     * Does the necessary preparation to redirect the process output.
     */
    void prepare(ProcessBuilder processBuilder);

    /**
     * Does the active part of redirection if needed.
     * See the class-level documentation and the implementation of
     * {@code StandardCommandExecutor} for more.
     */
    void doRedirection(Process process) throws IOException;

    /**
     * Returns a {@code PrintStream} which can be used be the shell or a builtin command.
     */
    PrintStream printStream();
}

package com.xosmig.swdesignhw.aush.environment;

import java.io.IOException;
import java.io.InputStream;

/**
 * Basic abstraction for input source in the shell implementation.
 * It might be used either to provide input to a process, created by shell
 * (via methods {@code prepare()} and {@code doRedirection()}),
 * or just to read input inside the shell itself, or one of the builtin commands
 * (via method {@code inputStream()}).
 */
public interface Input {

    /**
     * Does the necessary preparation to provide the process with the desired input.
     */
    void prepare(ProcessBuilder processBuilder);

    /**
     * Does the active part of redirection if needed.
     * See the class-level documentation and the implementation of
     * {@code StandardCommandExecutor} for more.
     */
    void doRedirection(Process process) throws IOException;

    /**
     * Returns an {@code InputStream} which can be used be the shell or a builtin command.
     */
    InputStream inputStream();
}

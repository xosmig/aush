package com.xosmig.swdesignhw.aush.commands.executor.builtin;

import com.xosmig.swdesignhw.aush.environment.Environment;

import java.util.List;

/**
 * Represents a builtin function.
 * Call to a builtin function is similar to a call to an executable file,
 * but it doesn't create a separate process and is executed by the shell itself.
 */
@FunctionalInterface
public interface Builtin {

    Environment execute(Environment env, List<String> args) throws InterruptedException;
}

package com.xosmig.swdesignhw.aush.commands;

import com.xosmig.swdesignhw.aush.commands.executor.CommandExecutor;
import com.xosmig.swdesignhw.aush.environment.Environment;

import java.io.IOException;

/**
 * Represents a command, which can be executed.
 * Usually created as a result of some kind of parsing of user input.
 *
 * Exact semantic depends on the command executor.
 * See <coode>StandardCommandExecutor</coode> for information about the default semantic.
 */
public interface Command {

    /**
     * Executes the command in the given <code>CommandExecutor</code> with the given <code>Environment</code>.
     * Returns the modified environment.
     *
     * @param environment the environment for the execution.
     * @param executor executes the commands and defines the semantic.
     * @return the modified environment after the command execution.
     * @throws IOException if an io error happens.
     * @throws InterruptedException if the thread was interrupted.
     */
    Environment accept(Environment environment, CommandExecutor executor) throws IOException, InterruptedException;
}

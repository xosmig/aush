package com.xosmig.swdesignhw.aush.commands.executor;

import com.xosmig.swdesignhw.aush.commands.*;
import com.xosmig.swdesignhw.aush.environment.Environment;

import java.io.IOException;

/**
 * Visitor for the <code>Command</code> class.
 * Executes the given commands within the given environment.
 * Returns the modified environment. Not supposed to have any internal state
 * (the state of the shell is encapsulated inside the <code>Environment</code> class).
 */
public interface CommandExecutor {

    Environment execute(Environment env, AssignmentCommand cmd) throws IOException, InterruptedException;

    Environment execute(Environment env, MultipleCommands cmd) throws IOException, InterruptedException;

    Environment execute(Environment env, PipeCommand cmd) throws IOException, InterruptedException;

    Environment execute(Environment env, TokenSequenceCommand cmd) throws IOException, InterruptedException;
}

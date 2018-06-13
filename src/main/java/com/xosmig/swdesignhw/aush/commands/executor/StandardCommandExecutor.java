package com.xosmig.swdesignhw.aush.commands.executor;

import com.xosmig.swdesignhw.aush.commands.*;
import com.xosmig.swdesignhw.aush.environment.Environment;
import com.xosmig.swdesignhw.aush.environment.Pipe;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The default command executor.
 * "Standard" means that this executor represents the semantic, which was intended
 * for the language at the moment of its first design.
 * Hence, all the other parts of the language (Environment, Tokenizer, Parser, Commands)
 * were designed with this semantic in mind.
 *
 * The semantic is very similar to the semantic of bash shell.
 */
public class StandardCommandExecutor implements CommandExecutor {

    /**
     * Maps the names of builtin functions to their implementations.
     */
    private static final Map<String, Builtin> BUILTINS;

    static {
        BUILTINS = new HashMap<>();

        // `pwd` command prints the current working directory
        BUILTINS.put("pwd", (Environment env, List<String> args) -> {
            env.getOutput().println(env.getWorkingDir().toAbsolutePath());
            return env.update().setLastExitCode(0).finish();
        });

        // exit command just informs the user interface that the used wants to exit the shell.
        BUILTINS.put("exit", (Environment env, List<String> args) -> {
            return env.update().shouldExit(true).setLastExitCode(0).finish();
        });
    }

    /**
     * Assigns to the variable <code>cmd.getName()</code> the value obtained
     * as the result expansion of <code>cmd.getValueToken()</code>.
     *
     * No need to explicitly create a variable, it will be created at the moment of the first
     * assignment.
     * If a variable with this value already existed, it will be overwritten.
     *
     * @param env current executing environment.
     * @param cmd the command to be executed.
     * @return the new environment.
     */
    @Override
    public Environment execute(Environment env, AssignmentCommand cmd) {
        String value = env.expand(cmd.getValueToken());
        return env.update().assign(cmd.getName(), value).setLastExitCode(0).finish();
    }

    /**
     * Executes the sub-commands sequentially. Each command works in the environment returned
     * by the previous command. The environment, returned from the rightmost command will be returned
     * from this method.
     *
     * @param env current executing environment.
     * @param cmd the command to be executed.
     * @return the new environment.
     */
    @Override
    public Environment execute(Environment env, MultipleCommands cmd) throws IOException, InterruptedException {
        env = cmd.getLeft().accept(env, this);
        env = cmd.getRight().accept(env, this);
        return env;
    }

    /**
     * Executes two commands in parallel and pipes the output of the left (source) command
     * to the input of the right (destination) command.
     *
     * Since the commands are run in parallel, they cannot modify the environment
     * (i.e. the modified environments are discarded).
     * With the exception of the the exit-code of the rightmost command in a pipeline.
     * This exit-code is set to be the last exit code in the environment.
     * Other exit codes are discarded.
     *
     * @param env current executing environment.
     * @param cmd the command to be executed.
     * @return the new environment.
     */
    @Override
    public Environment execute(Environment env, PipeCommand cmd) throws IOException, InterruptedException {
        final Pipe pipe = Pipe.get();

        // TODO: run in separate threads
        cmd.getSource().accept(env.update().setOutput(pipe.getOutput()).finish(), this);
        final Environment envRight =
                cmd.getDestination().accept(env.update().setInput(pipe.getInput()).finish(), this);

        return env.update().setLastExitCode(envRight.getLastExitCode()).finish();
    }

    /**
     * The first token of the sequence is treated as the program name and the other tokens are
     * treated as its parameters.
     *
     * The program name is either the name of one of the built-in functions (e.g. `pwd`),
     * or name of a binary to be executed.
     *
     * @param env current executing environment.
     * @param cmd the command to be executed.
     * @return the new environment.
     */
    @Override
    public Environment execute(Environment env, TokenSequenceCommand cmd)
            throws IOException, InterruptedException {
        final List<String> words = cmd.getTokens().stream().map(env::expand).collect(Collectors.toList());
        return executeProgram(env, words);
    }

    private Environment executeProgram(Environment env, List<String> command)
            throws IOException, InterruptedException {
        if (command.isEmpty()) {
            return env;
        }
        final String name = command.get(0);
        if (BUILTINS.containsKey(name)) {
            return BUILTINS.get(name).execute(env, command.subList(1, command.size()));
        }

        final Process process = new ProcessBuilder(command)
                .directory(env.getWorkingDir().toFile())
                .redirectErrorStream(true) // the error stream is merged with the output stream
                .redirectInput(env.getInput().getRedirect())
                .redirectOutput(env.getOutput().getRedirect())
                .start();

        final Thread processOutputReader = new Thread(() -> {
            try {
                env.getOutput().doRedirection(process.getInputStream());
            } catch (Exception e) {
                // oops
            }
        });
        processOutputReader.start();

        final Thread processInputWriter = new Thread(() -> {
            try {
                env.getInput().doRedirection(process.getOutputStream());
            } catch (Exception e) {
                // oops
            }
        });
        processInputWriter.start();

        int exitCode = process.waitFor();
        processInputWriter.interrupt();
        processInputWriter.join();
        processOutputReader.join();

        return env.update().setLastExitCode(exitCode).finish();
    }

    @FunctionalInterface
    private interface Builtin {
        Environment execute(Environment env, List<String> args) throws IOException;
    }
}

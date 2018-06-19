package com.xosmig.swdesignhw.aush.commands.executor;

import com.xosmig.swdesignhw.aush.ShellInternalException;
import com.xosmig.swdesignhw.aush.commands.*;
import com.xosmig.swdesignhw.aush.commands.executor.builtin.*;
import com.xosmig.swdesignhw.aush.environment.Environment;
import com.xosmig.swdesignhw.aush.environment.Pipe;
import com.xosmig.swdesignhw.aush.utils.Utils;
import org.apache.commons.io.output.NullOutputStream;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * The default command executor.
 * "Standard" means that this executor represents the semantic, which was intended
 * for the language at the moment of its first design.
 * Hence, all the other parts of the language (Environment, Tokenizer, Parser, Commands)
 * were designed with this semantic in mind.
 * <p>
 * The semantic is very similar to the semantic of bash shell.
 */
public class StandardCommandExecutor implements CommandExecutor {

    /**
     * Maps the names of builtin functions to their implementations.
     */
    private static final Map<String, Builtin> BUILTINS;

    static {
        BUILTINS = new HashMap<>();
        BUILTINS.put("pwd", new PwdBuiltin());
        BUILTINS.put("exit", new ExitBuiltin());
        BUILTINS.put("echo", new EchoBuiltin());
        BUILTINS.put("wc", new WcBuiltin());
        BUILTINS.put("cat", new CatBuiltin());
    }

    private StandardCommandExecutor() {
    }

    public static StandardCommandExecutor get() {
        return new StandardCommandExecutor();
    }

    /**
     * Assigns to the variable <code>cmd.getName()</code> the value obtained
     * as the result expansion of <code>cmd.getValueToken()</code>.
     * <p>
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
     * <p>
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

        final ExecutorService leftService = Executors.newSingleThreadExecutor();
        final CompletableFuture<Void> leftFuture = CompletableFuture.runAsync(() -> {
            try {
                cmd.getSource().accept(env.update().setOutput(pipe.getOutput()).finish(), this);
                pipe.getOutput().close();
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, leftService);

        final ExecutorService rightService = Executors.newSingleThreadExecutor();
        final CompletableFuture<Environment> rightFuture = CompletableFuture.supplyAsync(() -> {
            try {
                final Environment result =
                        cmd.getDestination().accept(env.update().setInput(pipe.getInput()).finish(), this);
                // We have to read all the bytes from the input stream to avoid the writer hang
                // waiting for the capacity.
                // Closing the InputStream is a bad idea, since it would cause an unwanted exception
                // in the writer.
                Utils.redirectStream(pipe.getInput().inputStream(), new NullOutputStream());
                return result;
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, rightService);

        try {
            waitBothOrException(leftFuture, rightFuture);
            return env.update().setLastExitCode(rightFuture.get().getLastExitCode()).finish();
        } catch (ExecutionException e) {
            // We throw `ShellInternalException` here since all meaningful possible instances
            // of ExecutionException are unwrapped inside `waitBothOrException` method.
            throw new ShellInternalException(e.getCause());
        }
    }

    /**
     * The first token of the sequence is treated as the program name and the other tokens are
     * treated as its parameters.
     * <p>
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

        final Exception[] outputReaderException = new Exception[1];
        final Thread outputReader = new Thread(() -> {
            try {
                env.getOutput().doRedirection(process.getInputStream());
            } catch (InterruptedIOException e) {
                // ignored
            } catch (Exception e) {
                outputReaderException[0] = e;
            }
        });
        outputReader.start();

        final Exception[] inputWriterException = new Exception[1];
        final Thread inputWriter = new Thread(() -> {
            try {
                env.getInput().doRedirection(process.getOutputStream());
            } catch (InterruptedIOException e) {
                // ignored
            } catch (Exception e) {
                inputWriterException[0] = e;
            }
        });
        inputWriter.start();

        int exitCode = process.waitFor();
        inputWriter.interrupt();
        inputWriter.join();
        outputReader.join();

        if (outputReaderException[0] != null) {
            throw new ShellInternalException(outputReaderException[0]);
        }
        if (inputWriterException[0] != null) {
            throw new ShellInternalException(inputWriterException[0]);
        }

        return env.update().setLastExitCode(exitCode).finish();
    }

    // this method cannot be replaced with allOf, since we need to cancel the other future,
    // when an exception is thrown in one of them.
    private static void waitBothOrException(CompletableFuture leftFuture, CompletableFuture rightFuture)
        throws IOException, InterruptedException {
        Throwable firstThrown = null;
        try {
            CompletableFuture.anyOf(leftFuture, rightFuture).get();
        } catch (Throwable th) {
            firstThrown = th;
            leftFuture.cancel(true);
            rightFuture.cancel(true);
        }

        try {
            CompletableFuture.allOf(leftFuture, rightFuture).get();
        } catch (Throwable th) {
            if (firstThrown == null) {
                firstThrown = th;
            }
            wrapAndThrow(firstThrown);
        }
    }

    private static void wrapAndThrow(Throwable e) throws IOException, InterruptedException {
        if (e == null) {
            throw new NullPointerException();
        }

        if (e instanceof IOException) {
            throw (IOException) e;
        }
        if (e instanceof InterruptedException) {
            throw (InterruptedException) e;
        }

        throw new ShellInternalException(e);
    }
}

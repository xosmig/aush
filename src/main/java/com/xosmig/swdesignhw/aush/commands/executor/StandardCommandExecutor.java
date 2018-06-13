package com.xosmig.swdesignhw.aush.commands.executor;

import com.xosmig.swdesignhw.aush.commands.*;
import com.xosmig.swdesignhw.aush.environment.Environment;
import com.xosmig.swdesignhw.aush.environment.Pipe;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class StandardCommandExecutor implements CommandExecutor {
    static final Map<String, Builtin> BUILTINS;

    static {
        BUILTINS = new HashMap<>();
        BUILTINS.put("pwd", (Environment env, List<String> args) -> {
            env.getOutput().println(env.getWorkingDir().toAbsolutePath());
            return env.update().setLastExitCode(0).finish();
        });
        BUILTINS.put("exit", (Environment env, List<String> args) -> {
            return env.update().shouldExit(true).setLastExitCode(0).finish();
        });
    }

    @Override
    public Environment execute(AssignmentCommand cmd, Environment env) {
        String value = env.expand(cmd.getValue());
        return env.update().assign(cmd.getName(), value).setLastExitCode(0).finish();
    }

    @Override
    public Environment execute(MultipleCommands cmd, Environment env)
            throws IOException, InterruptedException {
        env = cmd.getLeft().accept(env, this);
        env = cmd.getRight().accept(env, this);
        return env;
    }

    @Override
    public Environment execute(PipeCommand cmd, Environment env) throws IOException, InterruptedException {
        final Pipe pipe = Pipe.get();

        // TODO: run in separate threads
        cmd.getLeft().accept(env.update().setOutput(pipe.getOutput()).finish(), this);
        final Environment envRight =
                cmd.getRight().accept(env.update().setInput(pipe.getInput()).finish(), this);

        return env.update().setLastExitCode(envRight.getLastExitCode()).finish();
    }

    @Override
    public Environment execute(TokenSequenceCommand cmd, Environment env)
            throws IOException, InterruptedException {
        final List<String> words = cmd.getTokens().stream().map(env::expand).collect(Collectors.toList());
        return executeProgram(env, words);
    }

    public Environment executeProgram(Environment env, List<String> command)
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
}

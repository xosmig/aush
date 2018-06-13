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
            return env;
        });
        BUILTINS.put("exit", (Environment env, List<String> args) -> {
            return env.shouldExit(true);
        });
    }

    @Override
    public Environment execute(AssignmentCommand cmd, Environment env) {
        return env.assign(cmd.getName(), cmd.getValue());
    }

    @Override
    public Environment execute(MultipleCommands cmd, Environment env)
            throws IOException, InterruptedException {
        env = env.updateVars(cmd.getLeft().accept(env, this));
        return env.updateVars(cmd.getRight().accept(env, this));
    }

    @Override
    public Environment execute(PipeCommand cmd, Environment env) throws IOException, InterruptedException {
        Pipe pipe = Pipe.get();
        // TODO: run in separate threads
        cmd.getLeft().accept(env.updateOutput(pipe.getOutput()), this);
        cmd.getRight().accept(env.updateInput(pipe.getInput()), this);
        return env;
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

        return env.setLastExitCode(exitCode);
    }
}

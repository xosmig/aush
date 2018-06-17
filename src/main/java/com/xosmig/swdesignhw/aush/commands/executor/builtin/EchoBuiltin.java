package com.xosmig.swdesignhw.aush.commands.executor.builtin;

import com.xosmig.swdesignhw.aush.environment.Environment;

import java.util.List;
import java.util.StringJoiner;

/**
 * {@code echo} command prints its arguments to the standard output.
 */
public class EchoBuiltin implements Builtin {

    @Override
    public Environment execute(Environment env, List<String> args) {
        StringJoiner joiner = new StringJoiner(" ");
        for (String arg : args) {
            joiner.add(arg);
        }
        env.printStream().println(joiner.toString());
        return env.update().setLastExitCode(0).finish();
    }
}

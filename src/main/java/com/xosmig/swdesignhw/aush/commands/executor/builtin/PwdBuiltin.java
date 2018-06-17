package com.xosmig.swdesignhw.aush.commands.executor.builtin;

import com.xosmig.swdesignhw.aush.environment.Environment;

import java.util.List;

/**
 * {@code pwd} command prints the current working directory.
 */
public class PwdBuiltin implements Builtin {

    @Override
    public Environment execute(Environment env, List<String> args) throws InterruptedException {
        env.printStream().println(env.getWorkingDir().toAbsolutePath());
        return env.update().setLastExitCode(0).finish();
    }
}

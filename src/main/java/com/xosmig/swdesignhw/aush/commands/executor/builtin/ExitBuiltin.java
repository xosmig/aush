package com.xosmig.swdesignhw.aush.commands.executor.builtin;

import com.xosmig.swdesignhw.aush.environment.Environment;

import java.util.List;

/**
 * {@code `exit`} command just informs the user interface that the used wants to exit the shell.
 */
public class ExitBuiltin implements Builtin {

    @Override
    public Environment execute(Environment env, List<String> args) {
        return env.update().shouldExit(true).setLastExitCode(0).finish();
    }
}

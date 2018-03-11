package com.xosmig.swdesignhw.aush.commands;

import com.xosmig.swdesignhw.aush.environment.Environment;

public interface Command {
    Environment execute(CommandExecutor executor, Environment environment);
}

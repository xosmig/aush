package com.xosmig.swdesignhw.aush.commands;

import com.xosmig.swdesignhw.aush.commands.executor.CommandExecutor;
import com.xosmig.swdesignhw.aush.environment.Environment;

import java.io.IOException;

public interface Command {
    Environment execute(CommandExecutor executor, Environment environment) throws IOException;
}

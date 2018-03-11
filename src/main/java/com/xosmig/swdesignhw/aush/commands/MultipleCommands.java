package com.xosmig.swdesignhw.aush.commands;

import com.google.common.collect.ImmutableList;
import com.xosmig.swdesignhw.aush.environment.Environment;

public class MultipleCommands implements Command {
    private final ImmutableList<Command> commands;

    public MultipleCommands(ImmutableList<Command> commands) {
        this.commands = commands;
    }

    @Override
    public Environment execute(CommandExecutor executor, Environment environment) {
        return executor.execute(this, environment);
    }

    public ImmutableList<Command> getCommands() {
        return commands;
    }
}

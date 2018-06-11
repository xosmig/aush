package com.xosmig.swdesignhw.aush.commands;

import com.xosmig.swdesignhw.aush.commands.executor.CommandExecutor;
import com.xosmig.swdesignhw.aush.environment.Environment;

import java.io.IOException;

public class MultipleCommands implements Command {
    private final Command left;
    private final Command right;

    public MultipleCommands(Command left, Command right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Environment execute(CommandExecutor executor, Environment environment) throws IOException {
        return executor.execute(this, environment);
    }

    public Command getLeft() {
        return left;
    }

    public Command getRight() {
        return right;
    }
}

package com.xosmig.swdesignhw.aush.commands;

import com.xosmig.swdesignhw.aush.commands.executor.CommandExecutor;
import com.xosmig.swdesignhw.aush.environment.Environment;

import java.io.IOException;
import java.util.Objects;

public class MultipleCommands implements Command {
    private final Command left;
    private final Command right;

    public MultipleCommands(Command left, Command right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Environment accept(Environment environment, CommandExecutor executor)
            throws IOException, InterruptedException {
        return executor.execute(this, environment);
    }

    public Command getLeft() {
        return left;
    }

    public Command getRight() {
        return right;
    }

    @Override
    public String toString() {
        return "MultipleCommands{" +
                "left=" + left +
                ", right=" + right +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MultipleCommands)) return false;
        final MultipleCommands commands = (MultipleCommands) obj;
        return Objects.equals(left, commands.left) &&
                Objects.equals(right, commands.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }
}

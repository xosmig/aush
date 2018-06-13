package com.xosmig.swdesignhw.aush.commands;

import com.xosmig.swdesignhw.aush.commands.executor.CommandExecutor;
import com.xosmig.swdesignhw.aush.environment.Environment;

import java.io.IOException;
import java.util.Objects;

/**
 * Represents multiple commands, which are supposed to be run one after another.
 * Exact semantic depends on the command executor.
 * See <coode>StandardCommandExecutor</coode> for information about the default semantic.
 *
 * Note that it's a common technique to use another instance of <code>MultipleCommand</code>
 * as one (preferably, left) or both sub-commands.
 */
public class MultipleCommands implements Command {
    private final Command left;
    private final Command right;

    /**
     * Creates a new command, which consists of two sub-commands.
     *
     * @param left the command to be executed first.
     * @param right the command to be executed second.
     */
    public MultipleCommands(Command left, Command right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Environment accept(Environment environment, CommandExecutor executor)
            throws IOException, InterruptedException {
        return executor.execute(environment, this);
    }

    /**
     * Returns the command to be executed first.
     *
     * @return the command to be executed first.
     */
    public Command getLeft() {
        return left;
    }

    /**
     * Returns the command to be executed second.
     *
     * @return the command to be executed second.
     */
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

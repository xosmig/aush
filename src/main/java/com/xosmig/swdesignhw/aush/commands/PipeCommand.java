package com.xosmig.swdesignhw.aush.commands;

import com.xosmig.swdesignhw.aush.commands.executor.CommandExecutor;
import com.xosmig.swdesignhw.aush.environment.Environment;

import java.io.IOException;
import java.util.Objects;

/**
 * Represents two, possibly concurrent, commands with output of the source one
 * piped to the input of the destination one.
 * Exact semantic depends on the command executor.
 * See <coode>StandardCommandExecutor</coode> for information about the default semantic.
 *
 * Note that it's a common technique to use another instance of <code>PipeCommand</code>
 * as one (preferably, source) or both sub-commands.
 */
public class PipeCommand implements Command {
    private final Command source;
    private final Command destination;

    /**
     * Creates a new command, which consists of two sub-commands piped together.
     *
     * @param source the command, output of which will be piped to the input for <code>destination</code>.
     * @param destination the command, input for which will be piped from the output of <code>source</code>.
     */
    public PipeCommand(Command source, Command destination) {
        this.source = source;
        this.destination = destination;
    }

    @Override
    public Environment accept(Environment environment, CommandExecutor executor)
            throws IOException, InterruptedException {
        return executor.execute(environment, this);
    }

    /**
     * Returns the command, output of which will be piped to the input for <code>destination</code>.
     *
     * @return the source command of the pipe.
     */
    public Command getSource() {
        return source;
    }

    /**
     * Returns the command, input for which will be piped from the output of <code>source</code>.
     *
     * @return the destination command of the pipe.
     */
    public Command getDestination() {
        return destination;
    }

    @Override
    public String toString() {
        return "PipeCommand{" +
                "source=" + source +
                ", destination=" + destination +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PipeCommand)) return false;
        final PipeCommand command = (PipeCommand) obj;
        return Objects.equals(source, command.source) &&
                Objects.equals(destination, command.destination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, destination);
    }
}

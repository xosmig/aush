package com.xosmig.swdesignhw.aush.commands;

import com.xosmig.swdesignhw.aush.environment.Environment;

public class LocalAssignmentCommand implements Command {
    private final AssignmentCommand assignment;
    private final Command cmd;

    public LocalAssignmentCommand(AssignmentCommand assignment, Command cmd) {
        this.assignment = assignment;
        this.cmd = cmd;
    }

    @Override
    public Environment execute(CommandExecutor executor, Environment environment) {
        return executor.execute(this, environment);
    }

    public AssignmentCommand getAssignment() {
        return assignment;
    }

    public Command getCmd() {
        return cmd;
    }
}

package com.xosmig.swdesignhw.aush.commands;

import com.xosmig.swdesignhw.aush.environment.Environment;
import com.xosmig.swdesignhw.aush.token.Token;

public final class AssignmentCommand implements Command {
    private final String name;
    private final Token value;

    public AssignmentCommand(String name, Token value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public Environment execute(CommandExecutor executor, Environment environment) {
        return executor.execute(this, environment);
    }

    public String getName() {
        return name;
    }

    public Token getValue() {
        return value;
    }
}

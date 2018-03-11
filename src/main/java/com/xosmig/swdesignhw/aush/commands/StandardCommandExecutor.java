package com.xosmig.swdesignhw.aush.commands;

import com.xosmig.swdesignhw.aush.environment.Environment;

public class StandardCommandExecutor implements CommandExecutor {
    @Override
    public Environment execute(AssignCommand cmd, Environment environment) {
        return environment.assign(cmd.getName(), cmd.getValue());
    }

    @Override
    public Environment execute(MultipleCommands cmd, Environment environment) {
        for (Command cur : cmd.getCommands()) {
            environment = cur.execute(this, environment);
        }
        return environment;
    }
}

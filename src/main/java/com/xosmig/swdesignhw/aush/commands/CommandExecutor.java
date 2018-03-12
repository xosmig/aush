package com.xosmig.swdesignhw.aush.commands;

import com.xosmig.swdesignhw.aush.environment.Environment;

public interface CommandExecutor {
    Environment execute(AssignCommand cmd, Environment environment);

    Environment execute(MultipleCommands cmd, Environment environment);

    Environment execute(PipeCommand cmd, Environment environment);

    Environment execute(TokenSequenceCommand cmd, Environment environment);
}

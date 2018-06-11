package com.xosmig.swdesignhw.aush.commands.executor;

import com.xosmig.swdesignhw.aush.commands.*;
import com.xosmig.swdesignhw.aush.environment.Environment;

import java.io.IOException;

public interface CommandExecutor {
    Environment execute(AssignmentCommand cmd, Environment env) throws IOException;

    Environment execute(MultipleCommands cmd, Environment env) throws IOException;

    Environment execute(PipeCommand cmd, Environment env) throws IOException;

    Environment execute(TokenSequenceCommand cmd, Environment env) throws IOException;

    Environment execute(LocalAssignmentCommand cmd, Environment env) throws IOException;
}

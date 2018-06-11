package com.xosmig.swdesignhw.aush.commands.executor;

import com.xosmig.swdesignhw.aush.environment.Environment;

import java.io.IOException;
import java.util.List;

@FunctionalInterface
public interface Builtin {
    Environment execute(Environment env, List<String> args) throws IOException;
}

package com.xosmig.swdesignhw.aush.commands;

import com.xosmig.swdesignhw.aush.commands.executor.CommandExecutor;
import com.xosmig.swdesignhw.aush.environment.Environment;
import com.xosmig.swdesignhw.aush.token.Token;

import java.io.IOException;
import java.util.List;

public class TokenSequenceCommand implements Command {
    private final List<Token> tokens;

    public TokenSequenceCommand(List<Token> tokens) {
        this.tokens = tokens;
    }

    @Override
    public Environment execute(CommandExecutor executor, Environment env) throws IOException {
        return executor.execute(this, env);
    }

    public List<Token> getTokens() {
        return tokens;
    }
}

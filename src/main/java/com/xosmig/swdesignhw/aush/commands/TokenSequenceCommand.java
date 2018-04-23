package com.xosmig.swdesignhw.aush.commands;

import com.xosmig.swdesignhw.aush.environment.Environment;
import com.xosmig.swdesignhw.aush.token.Token;

import java.util.List;

public class TokenSequenceCommand implements Command {
    private final List<Token> tokens;

    public TokenSequenceCommand(List<Token> tokens) {
        this.tokens = tokens;
    }

    @Override
    public Environment execute(CommandExecutor executor, Environment environment) {
        return executor.execute(this, environment);
    }

    public List<Token> getTokens() {
        return tokens;
    }
}

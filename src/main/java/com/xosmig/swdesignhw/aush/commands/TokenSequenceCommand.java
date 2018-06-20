package com.xosmig.swdesignhw.aush.commands;

import com.xosmig.swdesignhw.aush.commands.executor.CommandExecutor;
import com.xosmig.swdesignhw.aush.environment.Environment;
import com.xosmig.swdesignhw.aush.token.Token;

import java.io.IOException;
import java.util.List;

/**
 * Represents a command, which is basically a sequence of tokens.
 * Exact semantic depends on the command executor.
 * See <coode>StandardCommandExecutor</coode> for information about the default semantic.
 */
public final class TokenSequenceCommand implements Command {

    private final List<Token> tokens;

    /**
     * Creates a new <code>TokenSequenceCommand</code>
     *
     * @param tokens the tokens of the sequence.
     */
    public TokenSequenceCommand(List<Token> tokens) {
        this.tokens = tokens;
    }

    @Override
    public Environment accept(Environment environment, CommandExecutor executor)
            throws IOException, InterruptedException {
        return executor.execute(environment, this);
    }

    /**
     * Returns a list of the tokens in the sequence.
     *
     * @return the tokens of the sequence.
     */
    public List<Token> getTokens() {
        return tokens;
    }
}

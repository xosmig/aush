package com.xosmig.swdesignhw.aush.parser;

import com.xosmig.swdesignhw.aush.commands.Command;
import com.xosmig.swdesignhw.aush.commands.TokenSequenceCommand;
import com.xosmig.swdesignhw.aush.token.Token;

import java.util.List;

public final class TokenSequenceParser implements Parser {
    @Override
    public Command parse(List<Token> text) {
        return new TokenSequenceCommand(text);
    }
}

package com.xosmig.swdesignhw.aush.parser;

import com.xosmig.swdesignhw.aush.commands.Command;
import com.xosmig.swdesignhw.aush.commands.PipeCommand;
import com.xosmig.swdesignhw.aush.token.PlainTextToken;
import com.xosmig.swdesignhw.aush.token.Token;

import java.util.List;

public final class PipeParser implements Parser {
    private final Parser childParser;

    public PipeParser(Parser childParser) {
        this.childParser = childParser;
    }

    @Override
    public Command parse(List<Token> text) throws ParseErrorException {
        int idx = text.indexOf(new PlainTextToken("|"));
        if (idx == -1) {
            return childParser.parse(text);
        }
        return new PipeCommand(childParser.parse(text.subList(0, idx)),
                this.parse(text.subList(idx + 1, text.size())));
    }
}

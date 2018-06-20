package com.xosmig.swdesignhw.aush.parser;

import com.xosmig.swdesignhw.aush.commands.Command;
import com.xosmig.swdesignhw.aush.commands.MultipleCommands;
import com.xosmig.swdesignhw.aush.token.SemicolonToken;
import com.xosmig.swdesignhw.aush.token.Token;

import java.util.List;

public final class SemicolonParser implements Parser {

    private final Parser nextParser;

    public SemicolonParser(Parser nextParser) {
        this.nextParser = nextParser;
    }

    @Override
    public Command parse(List<Token> text) throws ParseErrorException {
        int idx = text.lastIndexOf(SemicolonToken.get());
        if (idx == -1) {
            return nextParser.parse(text);
        }
        return new MultipleCommands(this.parse(text.subList(0, idx)),
                nextParser.parse(text.subList(idx + 1, text.size())));
    }
}

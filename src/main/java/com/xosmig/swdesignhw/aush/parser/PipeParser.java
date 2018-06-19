package com.xosmig.swdesignhw.aush.parser;

import com.xosmig.swdesignhw.aush.commands.Command;
import com.xosmig.swdesignhw.aush.commands.PipeCommand;
import com.xosmig.swdesignhw.aush.token.PipeToken;
import com.xosmig.swdesignhw.aush.token.Token;

import java.util.List;

public final class PipeParser implements Parser {

    private final Parser nextParser;

    public PipeParser(Parser nextParser) {
        this.nextParser = nextParser;
    }

    @Override
    public Command parse(List<Token> text) throws ParseErrorException {
        int idx = text.lastIndexOf(PipeToken.get());
        if (idx == -1) {
            return nextParser.parse(text);
        }
        return new PipeCommand(this.parse(text.subList(0, idx)),
                nextParser.parse(text.subList(idx + 1, text.size())));
    }
}

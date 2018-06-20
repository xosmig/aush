package com.xosmig.swdesignhw.aush.parser;

import com.xosmig.swdesignhw.aush.commands.Command;
import com.xosmig.swdesignhw.aush.token.Token;

import java.util.List;

public final class BashLikeFullParser implements Parser {

    private final Parser parser =
            new SemicolonParser(
                    new PipeParser(
                            new AssignmentParser(
                                    new TokenSequenceParser())));

    @Override
    public Command parse(List<Token> text) throws ParseErrorException {
        return parser.parse(text);
    }
}

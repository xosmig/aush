package com.xosmig.swdesignhw.aush.parser;

import com.xosmig.swdesignhw.aush.commands.Command;
import com.xosmig.swdesignhw.aush.token.Token;

import java.util.List;

public interface Parser {
    Command parse(List<Token> text) throws ParseErrorException;
}

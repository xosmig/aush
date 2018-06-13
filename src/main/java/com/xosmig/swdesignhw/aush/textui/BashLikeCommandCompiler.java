package com.xosmig.swdesignhw.aush.textui;

import com.xosmig.swdesignhw.aush.commands.Command;
import com.xosmig.swdesignhw.aush.parser.ParseErrorException;
import com.xosmig.swdesignhw.aush.parser.Parser;
import com.xosmig.swdesignhw.aush.parser.BashLikeFullParser;
import com.xosmig.swdesignhw.aush.token.Tokenizer;


public class BashLikeCommandCompiler implements TextCommandCompiler {
    public Tokenizer tokenizer = new Tokenizer();
    public Parser parser = new BashLikeFullParser();

    @Override
    public Command compile(String text) throws ParseErrorException {
        return parser.parse(tokenizer.tokenize(text));
    }
}

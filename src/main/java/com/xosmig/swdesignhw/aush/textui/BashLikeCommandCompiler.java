package com.xosmig.swdesignhw.aush.textui;

import com.xosmig.swdesignhw.aush.commands.Command;
import com.xosmig.swdesignhw.aush.parser.ParseErrorException;
import com.xosmig.swdesignhw.aush.parser.Parser;
import com.xosmig.swdesignhw.aush.parser.BashLikeFullParser;
import com.xosmig.swdesignhw.aush.token.Tokenizer;


/**
 * Compiles commands in a way similar to bash.
 */
public final class BashLikeCommandCompiler implements TextCommandCompiler {

    private BashLikeCommandCompiler() {
    }

    public static BashLikeCommandCompiler get() {
        return new BashLikeCommandCompiler();
    }

    private Parser parser = new BashLikeFullParser();

    @Override
    public Command compile(String text) throws ParseErrorException {
        return parser.parse(Tokenizer.tokenize(text));
    }
}

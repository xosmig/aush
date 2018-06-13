package com.xosmig.swdesignhw.aush.textui;

import com.xosmig.swdesignhw.aush.commands.Command;
import com.xosmig.swdesignhw.aush.parser.ParseErrorException;

/**
 * Compiles text to executable commands.
 */
public interface TextCommandCompiler {

    /**
     * Compiles text to executable commands.
     *
     * @param text the text of the command to be compiled.
     * @return the resulting command.
     * @throws ParseErrorException if {@code text} doesn't represent a valid command.
     */
    Command compile(String text) throws ParseErrorException;
}

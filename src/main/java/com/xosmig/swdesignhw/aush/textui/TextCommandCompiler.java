package com.xosmig.swdesignhw.aush.textui;

import com.xosmig.swdesignhw.aush.commands.Command;
import com.xosmig.swdesignhw.aush.parser.ParseErrorException;

public interface TextCommandCompiler {
    Command compile(String text) throws ParseErrorException;
}

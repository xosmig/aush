package com.xosmig.swdesignhw.aush.parser;

import com.xosmig.swdesignhw.aush.commands.Command;
import com.xosmig.swdesignhw.aush.commands.PipeCommand;
import com.xosmig.swdesignhw.aush.token.PipeToken;
import com.xosmig.swdesignhw.aush.token.Token;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class PipeParserTest extends ParserTestBase {
    public PipeParserTest() {
        this.parser = new PipeParser(mockParser);
    }

    @Test
    public void testNoPipes() throws Exception {
        final Command mockCommand = mock(Command.class);
        when(mockParser.parse(any())).thenReturn(mockCommand);
        List<Token> tokens = asList(plainText("hello"));
        assertEquals(mockCommand, parseTokensList(tokens));
        verify(mockParser, times(1)).parse(tokens);
    }

    @Test
    public void testPipeline() throws Exception {
        final Command mockCommand1 = mock(Command.class);
        final Command mockCommand2 = mock(Command.class);
        final Command mockCommand3 = mock(Command.class);

        Token left = plainText("left");
        Token mid = plainText("middle");
        Token right = plainText("right");

        List<Token> tokens = asList(left, PipeToken.get(), mid, PipeToken.get(), right);
        when(mockParser.parse(asList(left))).thenReturn(mockCommand1);
        when(mockParser.parse(asList(mid))).thenReturn(mockCommand2);
        when(mockParser.parse(asList(right))).thenReturn(mockCommand3);

        assertEquals(new PipeCommand(new PipeCommand(mockCommand1, mockCommand2), mockCommand3),
                parseTokensList(tokens));
        verify(mockParser, times(3)).parse(any());
    }
}

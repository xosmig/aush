package com.xosmig.swdesignhw.aush.parser;

import com.xosmig.swdesignhw.aush.commands.Command;
import com.xosmig.swdesignhw.aush.commands.MultipleCommands;
import com.xosmig.swdesignhw.aush.token.PlainTextToken;
import com.xosmig.swdesignhw.aush.token.SemicolonToken;
import com.xosmig.swdesignhw.aush.token.Token;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class SemicolonParserTest extends ParserTestBase {
    public SemicolonParserTest() {
        this.parser = new SemicolonParser(mockParser);
    }

    @Test
    public void testNoSemicolons() throws Exception {
        final Command mockCommand = mock(Command.class);
        when(mockParser.parse(any())).thenReturn(mockCommand);
        List<Token> tokens = asList(new PlainTextToken("hello"));
        assertEquals(mockCommand, parseTokensList(tokens));
        verify(mockParser, times(1)).parse(tokens);
    }

    @Test
    public void testMultipleCommands() throws Exception {
        final Command mockCommand1 = mock(Command.class);
        final Command mockCommand2 = mock(Command.class);
        final Command mockCommand3 = mock(Command.class);

        Token left = new PlainTextToken("left");
        Token mid = new PlainTextToken("middle");
        Token right = new PlainTextToken("right");

        List<Token> tokens = asList(left, SemicolonToken.get(), mid, SemicolonToken.get(), right);
        when(mockParser.parse(asList(left))).thenReturn(mockCommand1);
        when(mockParser.parse(asList(mid))).thenReturn(mockCommand2);
        when(mockParser.parse(asList(right))).thenReturn(mockCommand3);

        assertEquals(new MultipleCommands(new MultipleCommands(mockCommand1, mockCommand2), mockCommand3),
                parseTokensList(tokens));
        verify(mockParser, times(3)).parse(any());
    }
}

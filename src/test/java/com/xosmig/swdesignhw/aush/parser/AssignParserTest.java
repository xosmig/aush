package com.xosmig.swdesignhw.aush.parser;

import com.xosmig.swdesignhw.aush.commands.AssignmentCommand;
import com.xosmig.swdesignhw.aush.commands.Command;
import com.xosmig.swdesignhw.aush.token.ConcatenatedToken;
import com.xosmig.swdesignhw.aush.token.DoubleQuotedToken;
import com.xosmig.swdesignhw.aush.token.PlainTextToken;
import com.xosmig.swdesignhw.aush.token.Token;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AssignParserTest {
    private final Command mockCommand = mock(Command.class);
    private final Parser mockParser = mock(Parser.class);
    private final AssignmentParser assignParser = new AssignmentParser(mockParser);

    private Command parseToken(Token token) throws Exception {
        return assignParser.parse(Collections.singletonList(token));
    }

    @Test
    public void testNoAssignment() throws Exception {
        when(mockParser.parse(any())).thenReturn(mockCommand);
        assertEquals(mockCommand, parseToken(new PlainTextToken("hello")));
    }

    @Test
    public void testSimpleAssignment() throws Exception {
        assertEquals(new AssignmentCommand("myVar", new PlainTextToken("5")),
                parseToken(new PlainTextToken("myVar=5")));
    }

    @Test
    public void testQuotedAssignment() throws Exception {
        Command result = parseToken(new ConcatenatedToken(
                new PlainTextToken("myVar="), new DoubleQuotedToken("hello")));
        assertTrue(result instanceof AssignmentCommand);
        AssignmentCommand assignment = (AssignmentCommand) result;
        assertEquals("\"hello\"", assignment.getValue().backToString());
    }

    @Test
    public void testConcatenatedAssignment() throws Exception {
        Command result = parseToken(new ConcatenatedToken(
                new PlainTextToken("myVar=hello,"), new DoubleQuotedToken(" world")));
        assertTrue(result instanceof AssignmentCommand);
        AssignmentCommand assignment = (AssignmentCommand) result;
        assertEquals("hello,\" world\"", assignment.getValue().backToString());
    }
}

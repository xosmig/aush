package com.xosmig.swdesignhw.aush.parser;

import com.xosmig.swdesignhw.aush.commands.AssignmentCommand;
import com.xosmig.swdesignhw.aush.commands.Command;
import com.xosmig.swdesignhw.aush.token.ConcatenatedToken;
import com.xosmig.swdesignhw.aush.token.Token;
import com.xosmig.swdesignhw.aush.token.Tokenizer;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class AssignParserTest extends ParserTestBase {
    public AssignParserTest() {
        this.parser = new AssignmentParser(mockParser);
    }

    @Test
    public void testNoAssignment() throws Exception {
        final Command mockCommand = mock(Command.class);
        when(mockParser.parse(any())).thenReturn(mockCommand);
        List<Token> tokens = asList(plainText("hello"));
        assertEquals(mockCommand, parseTokensList(tokens));
        verify(mockParser).parse(tokens);
    }

    @Test
    public void testSimpleAssignment() throws Exception {
        assertEquals(new AssignmentCommand("myVar", plainText("5")),
                parseTokens(plainText("myVar=5")));
    }

    @Test
    public void testQuotedAssignment() throws Exception {
        Command result = parseTokens(ConcatenatedToken.concat(plainText("myVar="), doubleQuoted("hello")));
        assertTrue(result instanceof AssignmentCommand);
        AssignmentCommand assignment = (AssignmentCommand) result;
        assertEquals("\"hello\"", Tokenizer.detokenize(assignment.getValueToken()));
    }

    @Test
    public void testConcatenatedAssignment() throws Exception {
        Command result = parseTokens(ConcatenatedToken.concat(plainText("myVar=hello,"), doubleQuoted(" world")));
        assertTrue(result instanceof AssignmentCommand);
        AssignmentCommand assignment = (AssignmentCommand) result;
        assertEquals("hello,\" world\"", Tokenizer.detokenize(assignment.getValueToken()));
    }
}

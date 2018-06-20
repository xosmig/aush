package com.xosmig.swdesignhw.aush.parser;

import com.xosmig.swdesignhw.aush.TestBase;
import com.xosmig.swdesignhw.aush.commands.Command;
import com.xosmig.swdesignhw.aush.token.Token;
import org.junit.After;

import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public abstract class ParserTestBase extends TestBase {
    protected final Parser mockParser = mock(Parser.class);
    protected Parser parser;

    @After
    public final void verifyMockParser() {
        verifyNoMoreInteractions(mockParser);
    }

    protected Command parseTokens(Token... tokens) throws Exception {
        return parseTokensList(asList(tokens));
    }

    protected Command parseTokensList(List<Token> tokens) throws Exception {
        return parser.parse(tokens);
    }
}

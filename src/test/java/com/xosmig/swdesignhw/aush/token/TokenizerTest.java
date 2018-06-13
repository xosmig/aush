package com.xosmig.swdesignhw.aush.token;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class TokenizerTest {
    private PlainTextToken plainText(String text) {
        return new PlainTextToken(CmdString.parse(text));
    }

    private DoubleQuotedToken doubleQuoted(String text) {
        return new DoubleQuotedToken(CmdString.parse(text));
    }

    @Test
    public void testPlainText() {
        assertEquals(Collections.singletonList(plainText("foobar")),
                Tokenizer.tokenize("foobar"));
    }

    @Test
    public void testTrailingSpaces() {
        assertEquals(Collections.singletonList(plainText("foobar")),
                Tokenizer.tokenize("  foobar     "));
    }

    @Test
    public void testEmptyInput() {
        assertEquals(Collections.emptyList(), Tokenizer.tokenize(""));
    }

    @Test
    public void testSingleSeparator() {
        assertEquals(Arrays.asList(plainText("hello,"), plainText("world")),
                Tokenizer.tokenize("hello, world"));
    }

    @Test
    public void testMultipleSeparators() {
        assertEquals(Arrays.asList(plainText("hello,"), plainText("world")),
                Tokenizer.tokenize("hello,\t  \n\n world"));
    }

    @Test
    public void testDoubleQuotedText() {
        assertEquals(Collections.singletonList(doubleQuoted("hello, world")),
                Tokenizer.tokenize("\"hello, world\""));
    }

    @Test
    public void testConcatenated() {
        Token expected = ConcatenatedToken.concat(
                ConcatenatedToken.concat(
                        plainText("hello,"),
                        doubleQuoted(" world")
                ),
                plainText("!")
        );
        assertEquals(Collections.singletonList(expected), Tokenizer.tokenize("hello,\" world\"!"));
    }

    @Test
    public void testSemicolonAndPipe() {
        List<Token> expected = Arrays.asList(plainText("foo"), SemicolonToken.get(),
                plainText("bar"), PipeToken.get(), plainText("baz"));
        assertEquals(expected, Tokenizer.tokenize("foo;bar|baz"));
    }

    @Test
    public void testEscapedDoubleQuote() {
        assertEquals(Collections.singletonList(doubleQuoted("hello,\\\" world")),
                Tokenizer.tokenize("\"hello,\\\" world\""));
    }
}

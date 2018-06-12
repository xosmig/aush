package com.xosmig.swdesignhw.aush.token;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class TokenizerTest {
    Tokenizer tokenizer = new Tokenizer();

    private PlainTextToken plainText(String text) {
        return new PlainTextToken(CmdString.parse(text));
    }

    private DoubleQuotedToken doubleQuoted(String text) {
        return new DoubleQuotedToken(CmdString.parse(text));
    }

    @Test
    public void testPlainText() {
        assertEquals(Collections.singletonList(plainText("foobar")),
                tokenizer.tokenize("foobar"));
    }

    @Test
    public void testTrailingSpaces() {
        assertEquals(Collections.singletonList(plainText("foobar")),
                tokenizer.tokenize("  foobar     "));
    }

    @Test
    public void testEmptyInput() {
        assertEquals(Collections.emptyList(), tokenizer.tokenize(""));
    }

    @Test
    public void testSingleSeparator() {
        assertEquals(Arrays.asList(plainText("hello,"), plainText("world")),
                tokenizer.tokenize("hello, world"));
    }

    @Test
    public void testMultipleSeparators() {
        assertEquals(Arrays.asList(plainText("hello,"), plainText("world")),
                tokenizer.tokenize("hello,\t  \n\n world"));
    }

    @Test
    public void testDoubleQuotedText() {
        assertEquals(Collections.singletonList(doubleQuoted("hello, world")),
                tokenizer.tokenize("\"hello, world\""));
    }

    @Test
    public void testConcatenated() {
        Token expected = new ConcatenatedToken(
                new ConcatenatedToken(
                        plainText("hello,"),
                        doubleQuoted(" world")
                ),
                plainText("!")
        );
        assertEquals(Collections.singletonList(expected), tokenizer.tokenize("hello,\" world\"!"));
    }

    @Test
    public void testSemicolonAndPipe() {
        List<Token> expected = Arrays.asList(plainText("foo"), SemicolonToken.get(),
                plainText("bar"), PipeToken.get(), plainText("baz"));
        assertEquals(expected, tokenizer.tokenize("foo;bar|baz"));
    }

    @Test
    public void testEscapedDoubleQuote() {
        assertEquals(Collections.singletonList(doubleQuoted("hello,\\\" world")),
                tokenizer.tokenize("\"hello,\\\" world\""));
    }
}

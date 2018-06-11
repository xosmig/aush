package com.xosmig.swdesignhw.aush.parser;

import com.xosmig.swdesignhw.aush.token.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class TokenizerTest {
    Tokenizer tokenizer = new Tokenizer();

    @Test
    public void testPlainText() {
        assertEquals(Collections.singletonList(new PlainTextToken("foobar")),
                tokenizer.tokenize("foobar"));
    }

    @Test
    public void testTrailingSpaces() {
        assertEquals(Collections.singletonList(new PlainTextToken("foobar")),
                tokenizer.tokenize("  foobar     "));
    }

    @Test
    public void testEmptyInput() {
        assertEquals(Collections.emptyList(), tokenizer.tokenize(""));
    }

    @Test
    public void testSingleSeparator() {
        assertEquals(Arrays.asList(new PlainTextToken("hello,"), new PlainTextToken("world")),
                tokenizer.tokenize("hello, world"));
    }

    @Test
    public void testMultipleSeparators() {
        assertEquals(Arrays.asList(new PlainTextToken("hello,"), new PlainTextToken("world")),
                tokenizer.tokenize("hello,\t  \n\n world"));
    }

    @Test
    public void testSingleQuotedText() {
        assertEquals(Collections.singletonList(new SingleQuotedToken("hello, world")),
                tokenizer.tokenize("'hello, world'"));
    }

    @Test
    public void testDoubleQuotedText() {
        assertEquals(Collections.singletonList(new DoubleQuotedToken("hello, world")),
                tokenizer.tokenize("\"hello, world\""));
    }

    @Test
    public void testConcatenated() {
        Token expected = new ConcatenatedToken(
                new ConcatenatedToken(
                        new PlainTextToken("hello,"),
                        new SingleQuotedToken(" world")
                ),
                new PlainTextToken("!"));
        assertEquals(Collections.singletonList(expected), tokenizer.tokenize("hello,' world'!"));
    }
}
package com.xosmig.swdesignhw.aush.environment;

import com.xosmig.swdesignhw.aush.TestBase;
import com.xosmig.swdesignhw.aush.token.ConcatenatedToken;
import com.xosmig.swdesignhw.aush.token.Token;
import org.apache.commons.io.input.NullInputStream;
import org.apache.commons.io.output.NullOutputStream;
import org.junit.Test;
import org.pcollections.HashTreePMap;

import static org.junit.Assert.*;

public class EnvironmentTest extends TestBase {

    private Environment env = Environment.builder()
            .setInput(StreamInput.get(new NullInputStream(0)))
            .setOutput(StreamOutput.get(new NullOutputStream()))
            .setVarValues(HashTreePMap.singleton("myVar", "myValue"))
            .finish();

    @Test
    public void testExpandPlainTextWithoutVariables() {
        assertEquals("hello,world", env.expand(plainText("hello,world")));
    }

    @Test
    public void testExpandConcatenatedTokenWithoutVariables() {
        Token token = new ConcatenatedToken(plainText("hello"), doubleQuoted(", world"));
        assertEquals("hello, world", env.expand(token));
    }

    @Test
    public void testExpandPlainTextWithVariables() {
        assertEquals("hello,myValue,how_r_u?", env.expand(plainText("hello,$myVar,how_r_u?")));
    }

    @Test
    public void testExpandConcatenatedTokenWithVariables() {
        Token token = new ConcatenatedToken(plainText("hello"), doubleQuoted(", $myVar how r u?"));
        assertEquals("hello, myValue how r u?", env.expand(token));
    }
}

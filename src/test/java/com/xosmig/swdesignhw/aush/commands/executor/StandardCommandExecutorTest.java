package com.xosmig.swdesignhw.aush.commands.executor;

import com.xosmig.swdesignhw.aush.commands.Command;
import com.xosmig.swdesignhw.aush.environment.Environment;
import com.xosmig.swdesignhw.aush.environment.StreamInput;
import com.xosmig.swdesignhw.aush.environment.StreamOutput;
import com.xosmig.swdesignhw.aush.parser.ParseErrorException;
import com.xosmig.swdesignhw.aush.textui.BashLikeCommandCompiler;
import org.apache.commons.io.input.NullInputStream;
import org.junit.Test;
import org.pcollections.HashTreePMap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static org.junit.Assert.*;

public class StandardCommandExecutorTest {

    protected Command compile(String command) throws ParseErrorException {
        return BashLikeCommandCompiler.get().compile(command);
    }

    private CommandExecutor executor = StandardCommandExecutor.get();
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private Environment env = Environment.builder()
            .setInput(StreamInput.get(new NullInputStream(0)))
            .setOutput(StreamOutput.get(output))
            .setVarValues(HashTreePMap.singleton("myVar", "myValue"))
            .finish();

    private void compileAndRun(Environment env, String command) throws Exception {
        compile(command).accept(env, executor);
    }
    private void assertOutput(String expected) {
        assertEquals(expected, new String(output.toByteArray()));
    }

    @Test
    public void testEchoHelloWorld() throws Exception {
        compileAndRun(env, "echo hello, world!");
        assertOutput("hello, world!\n");
    }

    @Test
    public void testWcPoem() throws Exception {
        final byte[] poem = ("Студенту, не сдавшему экзамен\n" +
                "Бывает, в жизни нет альтернативы.\n" +
                "Экзамен покидаешь налегке...\n" +
                "И сессии несданной перспективы,\n" +
                "Увы! - уже маячат вдалеке.\n" +
                "Смотри на жизнь с упорным оптимизмом.\n" +
                "Махни рукой – и с музыкой вперёд!\n" +
                "В сравнении с глобальным катаклизмом\n" +
                "Что значит, блин, какой-то незачёт!\n" +
                "И, в общем, всё не так уж плохо, братцы.\n" +
                "И главное во всём – не унывать,\n" +
                "Легко и бодро жизнью наслаждаться,\n" +
                "А сессию... успешно пересдать.\n").getBytes();
        final InputStream input = new ByteArrayInputStream(poem);
        compile("wc").accept(
                env.update().setInput(StreamInput.get(input)).finish(),
                executor
        );
        assertOutput("     13      69     779\n");
    }
}
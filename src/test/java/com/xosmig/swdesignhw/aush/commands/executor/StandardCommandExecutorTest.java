package com.xosmig.swdesignhw.aush.commands.executor;

import com.xosmig.swdesignhw.aush.TestBase;
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
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class StandardCommandExecutorTest extends TestBase {

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

    private void compileAndRun(Environment env, String command, String inputData) throws Exception {
        compileAndRun(env, command, new ByteArrayInputStream(inputData.getBytes()));
    }

    private void compileAndRun(Environment env, String command, InputStream input) throws Exception {
        compileAndRun(env.update().setInput(StreamInput.get(input)).finish(), command);
    }

    private void compileAndRun(Environment env, String command) throws Exception {
        compile(command).accept(env, executor);
    }

    private void assertOutput(String expected) {
        assertEquals(expected, new String(output.toByteArray()));
    }

    @Test
    public void testEchoHelloWorld() throws Exception {
        compileAndRun(env, "echo hello, world!");
        assertOutput(fromUnixStr("hello, world!\n"));
    }

    @Test
    public void testWcPoem() throws Exception {
        // don't use fromUnixStr here, since it would make the test fail on windows
        final String poem = "Farewell to the Highlands, farewell to the North, \n" +
                        "The birth-place of Valour, the country of Worth; \n" +
                        "Wherever I wander, wherever I rove, \n" +
                        "The hills of the Highlands for ever I love. \n" +
                        "\n" +
                        "Chorus.-My heart's in the Highlands, my heart is not here, \n" +
                        "My heart's in the Highlands, a-chasing the deer; \n" +
                        "Chasing the wild-deer, and following the roe, \n" +
                        "My heart's in the Highlands, wherever I go. \n" +
                        "\n" +
                        "Farewell to the mountains, high-cover'd with snow, \n" +
                        "Farewell to the straths and green vallies below; \n" +
                        "Farewell to the forests and wild-hanging woods, \n" +
                        "Farewell to the torrents and loud-pouring floods. \n";
        compileAndRun(env, "wc", poem);
        assertOutput(fromUnixStr("     14      93     589\n"));
    }

    @Test(timeout = 1000)
    public void testPipeEchoCat() throws Exception {
        compileAndRun(env, "echo hello, world! | cat");
        assertOutput(fromUnixStr("hello, world!\n"));
    }

    @Test(timeout = 1000)
    public void testPipeEchoWc() throws Exception {
        // We just test that it doesn't hang. The output of this command is platform-dependant.
        compileAndRun(env, "echo hello, world | wc");
    }
}

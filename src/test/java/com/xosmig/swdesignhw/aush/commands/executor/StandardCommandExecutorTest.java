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
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public final class StandardCommandExecutorTest extends TestBase {
    // don't use fromUnixStr here, since it would make the number of bytes platform-dependant
    private static final String POEM = "" +
            "Farewell to the Highlands, farewell to the North, \n" +
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

    private Command compile(String command) throws ParseErrorException {
        return BashLikeCommandCompiler.get().compile(command);
    }

    private final CommandExecutor executor = StandardCommandExecutor.get();
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final Environment env = Environment.builder()
            .setInput(StreamInput.get(new NullInputStream(0)))
            .setOutput(StreamOutput.get(output))
            .setVarValues(HashTreePMap.singleton("myVar", "myValue"))
            .setLastExitCode(123)
            .finish();

    private String getOutputString() {
        return new String(output.toByteArray());
    }

    private Environment compileAndRun(Environment env, String command, String inputData) throws Exception {
        return compileAndRun(env, command, new ByteArrayInputStream(inputData.getBytes()));
    }

    private Environment compileAndRun(Environment env, String command, InputStream input) throws Exception {
        return compileAndRun(env.update().setInput(StreamInput.get(input)).finish(), command);
    }

    private Environment compileAndRun(Environment env, String command) throws Exception {
        return compile(command).accept(env, executor);
    }

    private void assertSuccess(Environment env) {
        try {
            assertEquals(0, env.getLastExitCode());
        } catch (AssertionError e) {
            System.err.println(getOutputString());
            throw e;
        }
    }

    private void assertOutput(String expected) {
        assertEquals(expected, getOutputString());
    }

    @Test
    public void testEchoHelloWorld() throws Exception {
        assertSuccess(compileAndRun(env, "echo hello, world!"));
        assertOutput(fromUnixStr("hello, world!\n"));
    }

    @Test(timeout = 1000)
    public void testWcPoem() throws Exception {
        assertSuccess(compileAndRun(env, "wc", POEM));
        assertOutput(fromUnixStr("     14      93     589\n"));
    }

    @Test(timeout = 1000)
    public void testPipeEchoToCat() throws Exception {
        assertSuccess(compileAndRun(env, "echo hello, world! | cat"));
        assertOutput(fromUnixStr("hello, world!\n"));
    }

    @Test(timeout = 1000)
    public void testPipeCatPoemToWc() throws Exception {
        assertSuccess(compileAndRun(env, "cat | wc", POEM));
        assertOutput(fromUnixStr("     14      93     589\n"));
    }

    @Test
    public void testPwd() throws Exception {
        Path workingDirPath = Paths.get("/hello/world/path");
        assertSuccess(compileAndRun(env.update().setWorkingDir(workingDirPath).finish(), "pwd"));
        assertOutput(fromUnixStr(workingDirPath.toAbsolutePath().toString() + "\n"));
    }

    @Test
    public void testMultipleCommandsSimple() throws Exception {
        assertSuccess(compileAndRun(env, "echo hello; echo world; echo;"));
        assertOutput(fromUnixStr("hello\nworld\n\n"));
    }

    @Test
    public void testMultipleCommandsWithAssignment() throws Exception {
        assertSuccess(compileAndRun(env, "a=hello; echo $a, world!; a=hi; echo $a, world!"));
        assertOutput(fromUnixStr("hello, world!\nhi, world!\n"));
    }

    @Test
    public void testExit() throws Exception {
        Environment resEnv = compileAndRun(env, "echo hello; exit; echo world!");
        assertSuccess(resEnv);
        assertTrue(resEnv.shouldExit());
        assertOutput(fromUnixStr("hello\n"));
    }

    @Test
    public void testExpansionAndQuotes() throws Exception {
        assertSuccess(compileAndRun(env, "a=5; echo \'123$a\'; echo \"123$a\"; echo 123$a"));
        assertOutput(fromUnixStr("123$a\n1235\n1235\n"));
    }

    @Test(timeout = 1000)
    public void testGrepSimple() throws Exception {
        // The last like doesn't end with \n deliberately
        String input = fromUnixStr("" +
                "hello, world!\n" +
                "hello, foobar1, how r u?\n" +
                "hellofoobaz2\n" +
                "\n" +
                "hello, foo bar 3\n" +
                "   hello, fooba 4");

        assertSuccess(compileAndRun(env, "grep fooba.\\\\d", input));
        assertOutput(fromUnixStr("" +
                "hello, foobar1, how r u?\n" +
                "hellofoobaz2\n" +
                "   hello, fooba 4\n"));
    }

    @Test(timeout = 1000)
    public void testGrepAfterContext() throws Exception {
        String input = fromUnixStr("" +
                "01-YES\n" +
                "02-YES\n" +
                "03-NO \n" +
                "04-YES\n" +
                "05-NO\n" +
                "06-NO\n" +
                "07-NO\n" +
                "08-NO\n" +
                "09-YES\n" +
                "10-NO\n" +
                "11-NO\n" +
                "12-YES\n" +
                "13-NO\n");

        assertSuccess(compileAndRun(env, "grep YES -A 2", input));
        assertOutput(fromUnixStr("" +
                "01-YES\n" +
                "02-YES\n" +
                "03-NO \n" +
                "04-YES\n" +
                "05-NO\n" +
                "06-NO\n" +
                "09-YES\n" +
                "10-NO\n" +
                "11-NO\n" +
                "12-YES\n" +
                "13-NO\n"));
    }

    @Test(timeout = 1000)
    public void testGrepIgnoreCase() throws Exception {
        String input = fromUnixStr("" +
                "01-YES\n" +
                "02-yes\n" +
                "03-NO \n" +
                "04-YeS\n" +
                "05-No\n" +
                "06-nO\n" +
                "07-no\n" +
                "09-Yes\n" +
                "12-yES\n" +
                "13-NO\n");

        assertSuccess(compileAndRun(env, "grep YES -i", input));
        assertOutput(fromUnixStr("" +
                "01-YES\n" +
                "02-yes\n" +
                "04-YeS\n" +
                "09-Yes\n" +
                "12-yES\n"));
    }

    @Test(timeout = 1000)
    public void testGrepWordRegexp() throws Exception {
        // The last like doesn't end with \n deliberately
        String input = fromUnixStr("" +
                "YES qqq\n" +
                "notYES qqq\n" +
                "qqq YESnot\n" +
                "qqq YES qqq\n" +
                "qqq YES");

        assertSuccess(compileAndRun(env, "grep YES -w", input));
        assertOutput(fromUnixStr("" +
                "YES qqq\n" +
                "qqq YES qqq\n" +
                "qqq YES\n"));
    }

    @Test(timeout = 1000)
    public void testGrepRequestHelp() throws Exception {
        // check that at least something was printed and that the command finished with exit code 0
        assertSuccess(compileAndRun(env, "grep --help"));
        assertTrue(getOutputString().length() > 10);
        String[] lines = getOutputString().trim().split(System.lineSeparator());
        // errors are usually printed on a single line
        assertTrue(lines.length >= 2);
    }
}

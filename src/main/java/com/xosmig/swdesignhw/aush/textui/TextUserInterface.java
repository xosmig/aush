package com.xosmig.swdesignhw.aush.textui;

import com.xosmig.swdesignhw.aush.commands.Command;
import com.xosmig.swdesignhw.aush.commands.executor.CommandExecutor;
import com.xosmig.swdesignhw.aush.commands.executor.StandardCommandExecutor;
import com.xosmig.swdesignhw.aush.environment.*;

import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.PrintStream;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Text-based user interface, similar to bash with a prompt and
 * Read-Execute-Print-Loop (REPL) architecture.
 */
public class TextUserInterface {

    private final InputStream inputStream;
    private final PrintStream outputStream;

    // For dependency injection
    public CommandExecutor executor = new StandardCommandExecutor();
    public TextCommandCompiler compiler = new BashLikeCommandCompiler();
    public Environment environment;

    /**
     * Creates a new instance of {@code TextUserInterface} which reads the input from
     * {@code input} and prints the output to {@code output}.
     * It's advised for {@code input} and {@code output} to refer to the same terminal.
     *
     * @param input the input stream.
     * @param output the output stream.
     */
    public TextUserInterface(InputStream input, PrintStream output) {
        this.inputStream = input;
        this.outputStream = output;
        this.environment = Environment.builder()
                .setInput(StreamInput.get(input))
                .setOutput(StreamOutput.get(output))
                .finish();
    }

    /**
     * Runs potentially infinite Read-Execute-Print-Loop (REPL).
     * Stops normally when encounters the end of input or when {@code exit} command is used.
     * It's not guaranteed to be safe to run the same instance multiple times.
     *
     * @throws InterruptedException if the thread is interrupted.
     */
    public void run() throws InterruptedException {
        final Scanner scanner = new Scanner(inputStream);
        Environment environment = this.environment;

        while (true) {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }

            outputStream.print("> ");
            outputStream.flush();
            String cmdLine;
            try {
                cmdLine = scanner.nextLine();
            } catch (NoSuchElementException e) {
                return;
            }

            try {
                Command command = compiler.compile(cmdLine);
                environment = command.accept(environment, executor);
            } catch (InterruptedIOException e) {
                throw new InterruptedException();
            } catch (Exception e) {
                outputStream.println(e.getMessage());
            }

            if (environment.shouldExit()) {
                return;
            }
        }
    }
}

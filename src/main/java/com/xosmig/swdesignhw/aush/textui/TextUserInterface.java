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


public class TextUserInterface {
    private final InputStream inputStream;
    private final PrintStream outputStream;

    // For dependency injection
    public CommandExecutor executor = new StandardCommandExecutor();
    public TextCommandCompiler compiler = new BashLikeCommandCompiler();
    public Environment environment;

    public TextUserInterface(InputStream inputStream, PrintStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.environment = Environment.builder()
                .setInput(StreamInput.get(inputStream))
                .setOutput(StreamOutput.get(outputStream))
                .finish();
    }

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

package com.xosmig.swdesignhw.aush;

import com.xosmig.swdesignhw.aush.commands.Command;
import com.xosmig.swdesignhw.aush.commands.executor.CommandExecutor;
import com.xosmig.swdesignhw.aush.commands.executor.StandardCommandExecutor;
import com.xosmig.swdesignhw.aush.environment.*;
import com.xosmig.swdesignhw.aush.parser.Parser;
import com.xosmig.swdesignhw.aush.parser.StandardFullParser;
import com.xosmig.swdesignhw.aush.token.Tokenizer;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.NoSuchElementException;
import java.util.Scanner;


public class TextUserInterface {
    private final InputStream inputStream;
    private final PrintStream outputStream;

    // For dependency injection
    public Tokenizer tokenizer = new Tokenizer();
    public Parser parser = new StandardFullParser();
    public CommandExecutor executor = new StandardCommandExecutor();
    public Environment environment;

    public TextUserInterface(InputStream inputStream, PrintStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.environment = Environment.builder()
                .setInput(StreamInput.get(inputStream))
                .setOutput(StreamOutput.get(outputStream))
                .finish();
    }

    void run() throws InterruptedException {
        final Scanner scanner = new Scanner(inputStream);
        final Tokenizer tokenizer = this.tokenizer;
        final Parser parser = this.parser;
        final CommandExecutor executor = this.executor;
        Environment environment = this.environment;

        while (!Thread.currentThread().isInterrupted()) {
            outputStream.print("> ");
            outputStream.flush();
            String cmdLine;
            try {
                cmdLine = scanner.nextLine();
            } catch (NoSuchElementException e) {
                return;
            }

            try {
                Command command = parser.parse(tokenizer.tokenize(cmdLine));
                environment = command.accept(environment, executor);
            } catch (Exception e) {
                outputStream.println(e.getMessage());
            }

            if (environment.shouldExit()) {
                return;
            }
        }
    }
}

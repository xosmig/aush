package com.xosmig.swdesignhw.aush;

import com.xosmig.swdesignhw.aush.commands.Command;
import com.xosmig.swdesignhw.aush.commands.executor.CommandExecutor;
import com.xosmig.swdesignhw.aush.commands.executor.StandardCommandExecutor;
import com.xosmig.swdesignhw.aush.environment.Environment;
import com.xosmig.swdesignhw.aush.parser.ParseErrorException;
import com.xosmig.swdesignhw.aush.parser.Parser;
import com.xosmig.swdesignhw.aush.parser.StandardFullParser;
import com.xosmig.swdesignhw.aush.token.Tokenizer;

import java.io.*;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.logging.Logger;

public class TextUserInterface {
    private final static Logger LOGGER = Logger.getLogger(TextUserInterface.class.getName());

    private final InputStream inputStream;
    private final PrintStream outputStream;
    private final PrintStream errorStream;
    private boolean stopped = false;

    // For dependency injection
    public Tokenizer tokenizer = new Tokenizer();
    public Parser parser = new StandardFullParser();
    public CommandExecutor executor = new StandardCommandExecutor();
    public Environment environment;

    public TextUserInterface(InputStream inputStream, PrintStream outputStream,
                             PrintStream errorStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.errorStream = errorStream;
        this.environment = new Environment(inputStream, outputStream, errorStream, Paths.get(""));
    }

    void run() {
        final Scanner scanner = new Scanner(inputStream);
        final Tokenizer tokenizer = this.tokenizer;
        final Parser parser = this.parser;
        final CommandExecutor executor = this.executor;
        Environment environment = this.environment;

        while (!Thread.currentThread().isInterrupted()) {
            outputStream.print("> ");
            outputStream.flush();
            String cmdLine = scanner.nextLine();
            LOGGER.fine(() -> "Received a command: `" + cmdLine + "`");
            Command command;
            try {
                command = parser.parse(tokenizer.tokenize(cmdLine));
            } catch (ParseErrorException | IllegalArgumentException e) {
                errorStream.println(e);
                continue;
            }
            try {
                environment = command.execute(executor, environment);
            } catch (IOException e) {
                errorStream.println(e);
            }
        }
    }
}

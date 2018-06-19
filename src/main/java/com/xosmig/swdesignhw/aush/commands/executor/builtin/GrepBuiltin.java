package com.xosmig.swdesignhw.aush.commands.executor.builtin;

import com.xosmig.swdesignhw.aush.environment.Environment;
import org.apache.commons.cli.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;

/**
 * {@code grep} command prints lines matching a pattern.
 */
public class GrepBuiltin implements Builtin {

    @Override
    public Environment execute(Environment env, List<String> args) throws InterruptedException {
        final Options options = new Options()
                .addOption(Option.builder("i")
                        .longOpt("ignore-case")
                        .desc("Ignore case distinctions in both the PATTERN and the input files.")
                        .build())
                .addOption(Option.builder("w")
                        .longOpt("word-regexp")
                        .desc("Select only those lines containing matches that form  whole  words.")
                        .build())
                .addOption(Option.builder("A")
                        .longOpt("after-context")
                        .hasArg(true)
                        .argName("NUM")
                        .desc("Print NUM lines of trailing context after matching lines.")
                        .build())
                .addOption(Option.builder("h")
                        .longOpt("help")
                        .desc("Print this message.")
                        .build());

        final HelpFormatter formatter = new HelpFormatter();

        final Runnable printHelp = () -> {
            final PrintWriter pw = new PrintWriter(env.printStream());
            formatter.printHelp(pw, formatter.getWidth(), "grep [OPTIONS] PATTERN [FILE...]",
                    null, options, formatter.getLeftPadding(), formatter.getDescPadding(), null, false);
            pw.flush();
        };

        final CommandLine cmd;
        final List<String> cmdArgs;
        final long afterContext;

        try {
            cmd = new DefaultParser().parse(options, args.toArray(new String[0]));

            if (cmd.hasOption("h")) {
                printHelp.run();
                return env.update().setLastExitCode(0).finish();
            }

            try {
                afterContext = Long.parseLong(cmd.getOptionValue("A"));
            } catch (NumberFormatException e) {
                throw new ParseException("invalid number format: " + e.getMessage());
            }
            if (afterContext < 0) {
                throw new ParseException("invalid argument for option -A: negative numbers are not allowed");
            }

            cmdArgs = cmd.getArgList();
            if (cmdArgs.isEmpty()) {
                throw new ParseException("required parameter PATTERN is not provided");
            }
        } catch (ParseException e) {
            env.printStream().println("grep: parse error: " + e.getMessage());
            printHelp.run();
            return env.update().setLastExitCode(2).finish();
        }

        Parameters parameters = new Parameters(cmd.hasOption("i"), cmd.hasOption("w"), afterContext,
                cmdArgs.get(0), cmdArgs.subList(1, cmdArgs.size()));

        return doGrep(env, parameters);
    }

    private Environment doGrep(Environment env, Parameters parameters) throws InterruptedException {
        boolean found;

        try {
            if (parameters.files.isEmpty()) {
                found = doGrepStreams(parameters, env.inputStream(), env.printStream());
            } else {
                found = doGrepFiles(parameters, env.printStream());
            }
        } catch (InterruptedIOException e) {
            throw new InterruptedException();
        } catch (IOException e) {
            env.printStream().println("grep: error: " + e.getMessage());
            return env.update().setLastExitCode(2).finish();
        }

        return env.update().setLastExitCode(found ? 0 : 1).finish();
    }

    private boolean doGrepFiles(Parameters parameters, PrintStream output) throws IOException {
        boolean found = false;
        for (String fileName : parameters.files) {
            if (Thread.interrupted()) {
                throw new InterruptedIOException();
            }

            final Path filePath;
            try {
                filePath = Paths.get(fileName);
            } catch (InvalidPathException e) {
                throw new IOException(e);
            }

            // `found = found || doGrep` is a bad idea due to the lazy semantic of `||` operator
            if (doGrepStreams(parameters, Files.newInputStream(filePath), output)) {
                found = true;
            }
        }
        return found;
    }

    private boolean doGrepStreams(Parameters parameters, InputStream input, PrintStream output) throws IOException {
        final BufferedReader scanner = new BufferedReader(new InputStreamReader(input));

        Pattern regexp = createRegexp(parameters);

        boolean found = false;
        long contextToPrint = 0;
        while (true) {
            if (Thread.interrupted()) {
                throw new InterruptedIOException();
            }

            String line = scanner.readLine();
            if (line == null) {
                // EOF
                return found;
            }

            if (regexp.matcher(line).find()) {
                output.println(line);
                found = true;
                contextToPrint = parameters.afterContext;
            } else {
                if (contextToPrint > 0) {
                    output.println(line);
                }
                contextToPrint--;
            }
        }
    }

    private Pattern createRegexp(Parameters parameters) {
        String pattern = (parameters.wordRegexp ? "\\b" + parameters.pattern + "\\b" : parameters.pattern);
        int flags = (parameters.ignoreCase ? Pattern.CASE_INSENSITIVE : 0);
        return Pattern.compile(pattern, flags);
    }

    private static class Parameters {
        public final boolean ignoreCase;
        public final boolean wordRegexp;
        public final long afterContext;
        public final String pattern;
        public final List<String> files;

        public Parameters(boolean ignoreCase, boolean wordRegexp, long afterContext,
                          String pattern, List<String> files) {
            this.ignoreCase = ignoreCase;
            this.wordRegexp = wordRegexp;
            this.afterContext = afterContext;
            this.pattern = pattern;
            this.files = files;
        }
    }
}

package com.xosmig.swdesignhw.aush.environment;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import com.xosmig.swdesignhw.aush.commands.AssignmentCommand;
import com.xosmig.swdesignhw.aush.token.*;
import org.pcollections.HashTreePMap;
import org.pcollections.PMap;

/**
 * Represents the executing environment.
 * The class is immutable, hence a new environment should be returned instead of modifying
 * the existing one (non-destructive update).
 * Use <code>Environment.builder()</code> to construct a new Environment.
 * Use <code>Environment.update()</code> to non-destructively update an existing one.
 */
public final class Environment {
    private static final Environment DEFAULT = new Environment(Inherit.input(), Inherit.output(),
            Paths.get(""), HashTreePMap.empty(), 0, false);

    private final Input input;
    private final Output output;
    private final Path workingDir;
    private final PMap<String, String> varValues;
    private final int lastExitCode;
    private final boolean shouldExit;

    private Environment(Input input, Output output, Path workingDir,
                        PMap<String, String> varValues, int lastExitCode, boolean shouldExit) {
        this.input = input;
        this.output = output;
        this.workingDir = workingDir.toAbsolutePath();
        this.varValues = varValues;
        this.lastExitCode = lastExitCode;
        this.shouldExit = shouldExit;
    }

    /**
     * Returns the default environment with inherited from this process input, output and working
     * directory and without any declared variables.
     * <code>lastExitCode</code> and <code>shouldExit</code> are set to 0 and false respectively.
     *
     * @return the default environment.
     */
    public static Environment getDefault() {
        return DEFAULT;
    }

    /**
     * Creates a new builder with all attributes set to the values of corresponding attributes
     * of this Environment.
     * Use this method to non-destructively modify the Environment.
     *
     * @return a new builder.
     */
    public Builder update() {
        return new Builder(this);
    }

    /**
     * Creates a new builder with the default values for all attributes.
     *
     * @return a new builder.
     */
    public static Builder builder() {
        return getDefault().update();
    }

    /**
     * See <code>Environment.Builder</code> documentation.
     */
    public Input getInput() {
        return input;
    }

    /**
     * See <code>Environment.Builder</code> documentation.
     */
    public Output getOutput() {
        return output;
    }

    /**
     * Same as {@code getInput().inputStream()}
     */
    public InputStream inputStream() {
        return getInput().inputStream();
    }

    /**
     * Same as get {@code getOutput().printStream()}
     */
    public PrintStream printStream() {
        return getOutput().printStream();
    }

    /**
     * See <code>Environment.Builder</code> documentation.
     */
    public Path getWorkingDir() {
        return workingDir;
    }

    /**
     * See <code>Environment.Builder</code> documentation.
     */
    public int getLastExitCode() {
        return lastExitCode;
    }

    /**
     * See <code>Environment.Builder</code> documentation.
     */
    public boolean shouldExit() {
        return shouldExit;
    }

    /**
     * Converts a token to a string. Replaces variable references (e.g. <code>$foobar</code>)
     * with their values.
     *
     * @param token token for the expansion.
     * @return the string version of the token with all variables expanded.
     */
    public String expand(Token token) {
        TokenExpander expander = new TokenExpander();
        token.accept(expander);
        return expander.result.toString();
    }

    private Optional<String> parseVarName(CmdString str) {
        int length = 0;
        while (length < str.length()) {
            CmdChar ch = str.charAt(length);
            if (ch.isEscaped() || !AssignmentCommand.isValidNameCharacter(ch.getCh())) {
                break;
            }
            length++;
        }
        return length == 0 ? Optional.empty() : Optional.of(str.substring(0, length).toString());
    }

    private String expandVariables(CmdString text) {
        final StringBuilder result = new StringBuilder();

        for (int idx = 0; idx < text.length(); ) {
            CmdChar ch = text.charAt(idx);

            if (!ch.equals(CmdChar.get('$', false))) {
                result.append(ch);
                idx += 1;
                continue;
            }

            Optional<String> varNameOpt = parseVarName(text.substring(idx + 1));
            if (!varNameOpt.isPresent()) {
                result.append(ch);
                idx += 1;
                continue;
            }

            String varName = varNameOpt.get();
            idx += varName.length() + 1;
            String value = varValues.get(varName);
            result.append(value != null ? value : "");
        }

        return result.toString();
    }

    /**
     * Helper class to create new instances of the class <code>Environment</code> and to
     * non-destructively modify existing ones.
     * Use <code>Environment.builder()</code> and <code>Environment.update()</code>
     * to create a new builder.
     */
    public static class Builder {
        public Input input;
        public Output output;
        public Path workingDir;
        public PMap<String, String> varValues;
        public int lastExitCode;
        public boolean shouldExit;

        private Builder(Environment source) {
            this.input = source.input;
            this.output = source.output;
            this.workingDir = source.workingDir;
            this.varValues = source.varValues;
            this.lastExitCode = source.lastExitCode;
        }

        /**
         * Finish the construction and return the new Environment.
         *
         * @return a new environment, constructed using the given parameters.
         */
        public Environment finish() {
            return new Environment(input, output, workingDir, varValues, lastExitCode, shouldExit);
        }

        /**
         * Sets the input source for the commands executed with this Environment.
         *
         * @param input new input source.
         * @return a reference to this object.
         */
        public Builder setInput(Input input) {
            this.input = input;
            return this;
        }

        /**
         * Sets the output destination for the commands executed with this Environment.
         *
         * @param output new output destination.
         * @return a reference to this object.
         */
        public Builder setOutput(Output output) {
            this.output = output;
            return this;
        }

        /**
         * Sets the working directory for the commands executed with this Environment.
         *
         * @param workingDir path to the new working directory.
         * @return a reference to this object.
         */
        public Builder setWorkingDir(Path workingDir) {
            this.workingDir = workingDir;
            return this;
        }

        /**
         * Sets the mapping from variable names to the values.
         * Use <code>assign</code> if you need to just change a value of a single variable.
         *
         * @param varValues new mapping.
         * @return a reference to this object.
         */
        public Builder setVarValues(PMap<String, String> varValues) {
            this.varValues = varValues;
            return this;
        }

        /**
         * Sets the information about the result of execution of the last executed command.
         * Don't forget to clean (set to 0) this field after successful execution of a command
         * (possibly, a command that never fails. E.g. an assignment).
         *
         * @param lastExitCode exit code of the last executed command.
         * @return a reference to this object.
         */
        public Builder setLastExitCode(int lastExitCode) {
            this.lastExitCode = lastExitCode;
            return this;
        }

        /**
         * Sets the flag, whether the execution should be stopped.
         * This flag to provide feedback to the object, which runs commands.
         *
         * @param value new value for the flag.
         * @return a reference to this object.
         */
        public Builder shouldExit(boolean value) {
            this.shouldExit = value;
            return this;
        }

        /**
         * Assigns to the variable <code>name</code> value <code>value</code>.
         * No need to explicitly create a variable, it will be created at the moment of the first
         * assignment. If a variable with this value already existed, it will be overwritten.
         *
         * @param name  name of the variable.
         * @param value new value for the variable.
         * @return a reference to this object.
         */
        public Builder assign(String name, String value) {
            return setVarValues(varValues.plus(name, value));
        }
    }

    private final class TokenExpander implements TokenVisitor {
        public StringBuilder result = new StringBuilder();

        @Override
        public void visit(DoubleQuotedToken token) {
            result.append(expandVariables(token.getContent()));
        }

        @Override
        public void visit(PlainTextToken token) {
            result.append(expandVariables(token.getContent()));
        }

        @Override
        public void visit(ConcatenatedToken token) {
            token.getLeft().accept(this);
            token.getRight().accept(this);
        }

        @Override
        public void visit(SemicolonToken token) {
            throw new IllegalArgumentException("Unexpected semicolon");
        }

        @Override
        public void visit(PipeToken token) {
            throw new IllegalArgumentException("Unexpected pipe");
        }
    }
}

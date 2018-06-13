package com.xosmig.swdesignhw.aush.environment;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import com.xosmig.swdesignhw.aush.commands.AssignmentCommand;
import com.xosmig.swdesignhw.aush.token.*;
import org.pcollections.HashTreePMap;
import org.pcollections.PMap;


public final class Environment {
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

    public static Environment empty() {
        return new Environment(Inherit.input(), Inherit.output(), Paths.get(""), HashTreePMap.empty(), 0, false);
    }

    public Builder update() {
        return new Builder(this);
    }

    public static Builder builder() {
        return empty().update();
    }

    public Input getInput() {
        return input;
    }

    public Output getOutput() {
        return output;
    }

    public Path getWorkingDir() {
        return workingDir;
    }

    public int getLastExitCode() {
        return lastExitCode;
    }

    public boolean shouldExit() {
        return shouldExit;
    }

    public String expand(Token token) {
        TokenExpander expander = new TokenExpander();
        token.accept(expander);
        return expander.result.toString();
    }

    public Environment assign(String name, Token value) {
        return update().setVarValues(varValues.plus(name, expand(value))).finish();
    }

    public Environment updateInput(Input newInput) {
        return update().setInput(newInput).finish();
    }

    public Environment updateOutput(Output newOutput) {
        return update().setOutput(newOutput).finish();
    }

    public Environment updateVars(Environment other) {
        return update().setVarValues(other.varValues).finish();
    }

    public Environment changeWorkingDir(Path path) {
        return update().setWorkingDir(workingDir.resolve(path)).finish();
    }

    public Environment setLastExitCode(int exitCode) {
        return update().setLastExitCode(exitCode).finish();
    }

    public Environment shouldExit(boolean value) {
        return update().shouldExit(value).finish();
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

    public static class Builder {
        public Input input;
        public Output output;
        public Path workingDir;
        public PMap<String, String> varValues;
        public int lastExitCode;
        public boolean shouldExit;

        public Builder(Environment source) {
            this.input = source.input;
            this.output = source.output;
            this.workingDir = source.workingDir;
            this.varValues = source.varValues;
            this.lastExitCode = source.lastExitCode;
        }

        public Environment finish() {
            return new Environment(input, output, workingDir, varValues, lastExitCode, shouldExit);
        }

        public Builder setInput(Input input) {
            this.input = input;
            return this;
        }

        public Builder setOutput(Output output) {
            this.output = output;
            return this;
        }

        public Builder setWorkingDir(Path workingDir) {
            this.workingDir = workingDir;
            return this;
        }

        public Builder setVarValues(PMap<String, String> varValues) {
            this.varValues = varValues;
            return this;
        }

        public Builder setLastExitCode(int lastExitCode) {
            this.lastExitCode = lastExitCode;
            return this;
        }

        public Builder shouldExit(boolean value) {
            this.shouldExit = value;
            return this;
        }
    }
}

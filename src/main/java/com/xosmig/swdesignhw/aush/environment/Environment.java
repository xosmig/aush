package com.xosmig.swdesignhw.aush.environment;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Optional;

import com.xosmig.swdesignhw.aush.commands.AssignmentCommand;
import com.xosmig.swdesignhw.aush.token.*;
import org.pcollections.HashTreePMap;
import org.pcollections.PMap;


public final class Environment {
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final OutputStream errorStream;
    private final Path workingDir;
    private final PMap<String, String> varValues;

    public Environment(InputStream inputStream, OutputStream outputStream,
                       OutputStream errorStream, Path workingDir) {
        this(inputStream, outputStream, errorStream, workingDir, HashTreePMap.empty());
    }

    public Environment(InputStream inputStream, OutputStream outputStream,
                        OutputStream errorStream, Path workingDir,
                        PMap<String, String> varValues) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.errorStream = errorStream;
        this.workingDir = workingDir;
        this.varValues = varValues;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public OutputStream getErrorStream() {
        return errorStream;
    }

    public Path getWorkingDir() { return workingDir; }

    public String expand(Token token) {
        TokenExpander expander = new TokenExpander();
        token.accept(expander);
        return expander.result.toString();
    }

    public Environment assign(String name, Token value) {
        return new Environment(inputStream, outputStream, errorStream, workingDir,
                varValues.plus(name, expand(value)));
    }

    public Environment updateInputStream(InputStream newInputStream) {
        return new Environment(newInputStream, outputStream, errorStream, workingDir, varValues);
    }

    public Environment updateOutputStream(OutputStream newOutputStream) {
        return new Environment(inputStream, newOutputStream, errorStream, workingDir, varValues);
    }

    public Environment updateErrorStream(OutputStream newErrorStream) {
        return new Environment(inputStream, outputStream, newErrorStream, workingDir, varValues);
    }

    public Environment updateVars(Environment other) {
        return new Environment(inputStream, outputStream, errorStream, workingDir, other.varValues);
    }

    public Environment changeWorkingDir(Path path) {
        return new Environment(inputStream, outputStream, errorStream, workingDir.resolve(path),
                varValues);
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

        for (int idx = 0; idx < text.length();) {
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
}

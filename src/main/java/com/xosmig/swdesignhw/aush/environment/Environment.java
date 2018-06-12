package com.xosmig.swdesignhw.aush.environment;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
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

    private Environment(InputStream inputStream, OutputStream outputStream,
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

    public List<Word> expand(Token token) {
//        return token.accept(new ListTokenExpander()).words;
        return null; // TODO
    }

    public Environment assign(String name, Token value) {
//        return new Environment(inputStream, outputStream, errorStream, workingDir,
//                varValues.plus(name, value.accept(new StringTokenExpander()).toString()));
        return null; // TODO
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

    private String expandVariables(String text) {
        boolean backslash = false;
        final StringBuilder result = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            final char ch = text.charAt(i);
            final boolean wasBackslash = backslash;

            if (ch == '\\') {
                if (wasBackslash) {
                    backslash = false;
                    continue;
                } else {
                    backslash = true;
                    result.append(ch);
                    continue;
                }
            }

            backslash = false;

            // not a backspace
            if (ch == '$' && !wasBackslash) {
                // expand variable
                int beginOfVar = i + 1;
                while (i + 1 < text.length() && Character.isLetter(text.charAt(i + 1))) {
                    i++;
                }
                String value = varValues.get(text.substring(beginOfVar, i + 1));
                if (value == null) {
                    value = "";
                    // TODO: log warning
                }
                result.append(value);
                continue;
            }

            result.append(ch);
        }

        return result.toString();
    }

//    private final class ListTokenExpander implements TokenVisitor<ExpansionResult> {
//        @Override
//        public ExpansionResult visit(DoubleQuotedToken token) {
//            return ExpansionResult.singleWord(new Word(expandVariables(token.getContent())));
//        }
//
//        @Override
//        public ExpansionResult visit(SingleQuotedToken token) {
//            return ExpansionResult.singleWord(new Word(token.getContent()));
//        }
//
//        @Override
//        public ExpansionResult visit(PlainTextToken token) {
//            String expandedText = expandVariables(token.getContent());
//            return new ExpansionResult(
//                    Arrays.stream(expandedText.split("\\s+"))
//                            .map(Word::new)
//                            .collect(Collectors.toList()),
//                    Character.isWhitespace(expandedText.charAt(0)),
//                    Character.isWhitespace(expandedText.charAt(expandedText.length() - 1)));
//        }
//
//        @Override
//        public ExpansionResult visit(ConcatenatedToken token) {
//            ExpansionResult left = token.getLeft().accept(this);
//            ExpansionResult right = token.getRight().accept(this);
//
//            List<Word> result = new ArrayList<>();
//            if (!left.rightSeparated && !right.leftSeparated) {
//                // merge the words in the center
//                result.addAll(left.words.subList(0, left.words.size() - 1));
//                result.add(Word.concat(left.words.get(left.words.size() - 1), right.words.get(0)));
//                result.addAll(right.words.subList(1, right.words.size()));
//            } else {
//                result.addAll(left.words.subList(0, left.words.size()));
//                result.addAll(right.words.subList(0, right.words.size()));
//            }
//            return new ExpansionResult(result, left.leftSeparated, right.rightSeparated);
//        }
//
//        @Override
//        public ExpansionResult visit(SemicolonToken token) {
//            return null; // TODO
//        }
//
//        @Override
//        public ExpansionResult visit(PipeToken token) {
//            return null; // TODO
//        }
//    }
//
//    private final class StringTokenExpander implements TokenVisitor<StringBuilder> {
//        @Override
//        public StringBuilder visit(DoubleQuotedToken token) {
//            return new StringBuilder(expandVariables(token.getContent()));
//        }
//
//        @Override
//        public StringBuilder visit(SingleQuotedToken token) {
//            return new StringBuilder(token.getContent());
//        }
//
//        @Override
//        public StringBuilder visit(PlainTextToken token) {
//            return new StringBuilder(expandVariables(token.getContent()));
//        }
//
//        @Override
//        public StringBuilder visit(ConcatenatedToken token) {
//            return token.getLeft().accept(this).append(token.getRight().accept(this));
//        }
//
//        @Override
//        public StringBuilder visit(SemicolonToken token) {
//            return null; // TODO
//        }
//
//        @Override
//        public StringBuilder visit(PipeToken token) {
//            return null; // TODO
//        }
//
//        @Override
//        public StringBuilder visit(VariableToken token) {
//            return null; // TODO
//        }
//    }

    private final static class ExpansionResult {
        public final List<Word> words;
        public final boolean leftSeparated;
        public final boolean rightSeparated;

        public ExpansionResult(List<Word> words, boolean leftSeparated, boolean rightSeparated) {
            this.words = words;
            this.leftSeparated = leftSeparated;
            this.rightSeparated = rightSeparated;
        }

        public static ExpansionResult singleWord(Word word) {
            return new ExpansionResult(ImmutableList.of(word), false, false);
        }
    }
}

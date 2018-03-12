package com.xosmig.swdesignhw.aush.environment;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.xosmig.swdesignhw.aush.token.*;
import org.pcollections.HashTreePMap;
import org.pcollections.PMap;

public final class StandardEnvironment implements Environment {
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final OutputStream errorStream;
    private final PMap<String, String> varValues;

    public StandardEnvironment(InputStream inputStream, OutputStream outputStream,
                               OutputStream errorStream) {
        this(inputStream, outputStream, errorStream, HashTreePMap.empty());
    }

    private StandardEnvironment(InputStream inputStream, OutputStream outputStream,
                                OutputStream errorStream, PMap<String, String> varValues) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.errorStream = errorStream;
        this.varValues = varValues;
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public OutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public OutputStream getErrorStream() {
        return errorStream;
    }

    @Override
    public List<Word> expand(Token token) {
        return token.accept(new ListTokenExpander()).words;
    }

    @Override
    public Environment assign(String name, Token value) {
        return new StandardEnvironment(inputStream, outputStream, errorStream,
                varValues.plus(name, value.accept(new StringTokenExpander()).toString()));
    }

    @Override
    public Environment updateInputStream(InputStream newInputStream) {
        return new StandardEnvironment(newInputStream, outputStream, errorStream, varValues);
    }

    @Override
    public Environment updateOutputStream(OutputStream newOutputStream) {
        return new StandardEnvironment(inputStream, newOutputStream, errorStream, varValues);
    }

    @Override
    public Environment updateErrorStream(OutputStream newErrorStream) {
        return new StandardEnvironment(inputStream, outputStream, newErrorStream, varValues);
    }

    private String expandVariables(String text) {
        boolean backspace = false;
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            boolean wasBackspace = backspace;

            if (ch == '\\') {
                if (wasBackspace) {
                    backspace = false;
                    continue;
                } else {
                    backspace = true;
                    result.append(ch);
                    continue;
                }
            }

            backspace = false;

            // not a backspace
            if (ch == '$' && !wasBackspace) {
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

    private final class ListTokenExpander implements TokenVisitor<ExpansionResult> {
        @Override
        public ExpansionResult visit(DoubleQuotedToken token) {
            return ExpansionResult.singleWord(new Word(expandVariables(token.getContent())));
        }

        @Override
        public ExpansionResult visit(SingleQuotedToken token) {
            return ExpansionResult.singleWord(new Word(token.getContent()));
        }

        @Override
        public ExpansionResult visit(PlainTextToken token) {
            String expandedText = expandVariables(token.getContent());
            return new ExpansionResult(
                    Arrays.stream(expandedText.split("\\s+"))
                            .map(Word::new)
                            .collect(Collectors.toList()),
                    Character.isWhitespace(expandedText.charAt(0)),
                    Character.isWhitespace(expandedText.charAt(expandedText.length() - 1)));
        }

        @Override
        public ExpansionResult visit(ConcatenatedToken token) {
            ExpansionResult left = token.getLeft().accept(this);
            ExpansionResult right = token.getRight().accept(this);

            List<Word> result = new ArrayList<>();
            if (!left.rightSeparated && !right.leftSeparated) {
                // merge the words in the center
                result.addAll(left.words.subList(0, left.words.size() - 1));
                result.add(Word.concat(left.words.get(left.words.size() - 1), right.words.get(0)));
                result.addAll(right.words.subList(1, right.words.size()));
            } else {
                result.addAll(left.words.subList(0, left.words.size()));
                result.addAll(right.words.subList(0, right.words.size()));
            }
            return new ExpansionResult(result, left.leftSeparated, right.rightSeparated);
        }
    }

    private final class StringTokenExpander implements TokenVisitor<StringBuilder> {
        @Override
        public StringBuilder visit(DoubleQuotedToken token) {
            return new StringBuilder(expandVariables(token.getContent()));
        }

        @Override
        public StringBuilder visit(SingleQuotedToken token) {
            return new StringBuilder(token.getContent());
        }

        @Override
        public StringBuilder visit(PlainTextToken token) {
            return new StringBuilder(expandVariables(token.getContent()));
        }

        @Override
        public StringBuilder visit(ConcatenatedToken token) {
            return token.getLeft().accept(this).append(token.getRight().accept(this));
        }
    }

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

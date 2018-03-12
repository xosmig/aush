package com.xosmig.swdesignhw.aush.parser;

import com.sun.tools.corba.se.idl.InvalidArgument;
import com.xosmig.swdesignhw.aush.token.*;

import java.util.*;

public class Tokenizer {

    public List<Token> tokenize(String text) throws InvalidArgument {
        return new Helper(text).tokenize();
    }

    private static class Helper {
        private final String text;
        private final List<Token> result = new ArrayList<>();
        private final List<Token> concatenated = new ArrayList<>();
        private final StringBuilder buffer = new StringBuilder();
        private boolean wasBackslash = false;

        public Helper(String text) {
            this.text = text;
        }

        public List<Token> tokenize() throws InvalidArgument {
            boolean backslash = false;

            for (int idx = 0; idx < text.length();) {
                wasBackslash = backslash;

                if (text.charAt(idx) == '\\') {
                    if (wasBackslash) {
                        addChar('\\');
                        backslash = false;
                    } else {
                        backslash = true;
                    }

                    idx += 1;
                    continue;
                }
                backslash = false;

                final int curIdx = idx;
                idx = tryQuote(curIdx).orElseGet(() ->
                        trySpecial(curIdx).orElseGet(() ->
                        tryWhitespace(curIdx).orElseGet(() ->
                        tryPlainText(curIdx))));
            }
            endAllTokens();

            return result;
        }

        private void addChar(char ch) {
            buffer.append(ch);
        }

        private void addAtomicToken(Token token) {
            concatenated.add(token);
        }

        private void endPlainTextToken() {
            if (buffer.length() == 0) {
                return;
            }

            addAtomicToken(new PlainTextToken(buffer.toString()));
            buffer.delete(0, buffer.length()); // clean the buffer
        }

        private void endAllTokens() {
            endPlainTextToken();
            concatenated.stream().reduce(ConcatenatedToken::new).ifPresent(result::add);
            concatenated.clear();
        }

        private Optional<Integer> tryQuote(int idx) throws InvalidArgument {
            char ch = text.charAt(idx);
            if (ch == '\'' || ch == '\"') {
                if (wasBackslash) {
                    addChar(ch);
                    return Optional.of(idx + 1);
                } else {
                    endPlainTextToken();
                    int rightQuote = text.substring(idx + 1).indexOf('\'');
                    if (rightQuote == -1) {
                        throw new InvalidArgument("Quote without a pair");
                    }
                    if (ch == '\'') {
                        addAtomicToken(new SingleQuotedToken(text.substring(idx + 1, rightQuote)));
                    } else {
                        addAtomicToken(new DoubleQuotedToken(text.substring(idx + 1, rightQuote)));
                    }
                    return Optional.of(rightQuote + 1);
                }
            }
            return Optional.empty();
        }

        private Optional<Integer> trySpecial(int idx) {
            char ch = text.charAt(idx);
            if (ch == '|' || ch == ';') {
                if (wasBackslash) {
                    addChar(ch);
                } else {
                    endAllTokens();
                    addAtomicToken(new PlainTextToken(Character.toString(ch)));
                }
                return Optional.of(idx + 1);
            }
            return Optional.empty();
        }

        private Optional<Integer> tryWhitespace(int idx) {
            char ch = text.charAt(idx);
            if (ch == '|' || ch == ';') {
                if (wasBackslash) {
                    addChar(ch);
                } else {
                    endAllTokens();
                }
                return Optional.of(idx + 1);
            }
            return Optional.empty();
        }

        private int tryPlainText(int idx) {
            addChar(text.charAt(idx));
            return idx + 1;
        }
    }
}

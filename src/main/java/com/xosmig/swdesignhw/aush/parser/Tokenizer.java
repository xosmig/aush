package com.xosmig.swdesignhw.aush.parser;

import com.xosmig.swdesignhw.aush.token.*;

import java.util.*;

public class Tokenizer {

    public List<Token> tokenize(String text) {
        return new Helper(text).tokenize();
    }

    private static class Helper {
        private final String text;
        private final List<Token> result = new ArrayList<>();
        private final List<Token> concatenated = new ArrayList<>();
        private final StringBuilder buffer = new StringBuilder();

        public Helper(String text) {
            this.text = text;
        }

        public List<Token> tokenize() {
            for (int idx = 0; idx < text.length();) {
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

        private void addToken(Token token) {
            concatenated.add(token);
        }

        private void endPlainTextToken() {
            if (buffer.length() == 0) {
                return;
            }

            addToken(new PlainTextToken(buffer.toString()));
            buffer.delete(0, buffer.length()); // clean the buffer
        }

        private void endAllTokens() {
            endPlainTextToken();
            if (concatenated.isEmpty()) {
                return;
            }

            Deque<Token> queue = new ArrayDeque<>(concatenated);
            while (queue.size() >= 2) {
                Token left = queue.removeFirst();
                Token right = queue.removeFirst();
                queue.addFirst(new ConcatenatedToken(left, right));
            }
            result.add(queue.getFirst());
            concatenated.clear();
        }

        private boolean consumeBackslash() {
            if (buffer.length() > 0 && buffer.charAt(buffer.length() - 1) == '\\') {
                // delete the backslash from the text
                buffer.deleteCharAt(buffer.length() - 1);
                return true;
            } else {
                return false;
            }
        }

        private Optional<Integer> tryQuote(int idx) {
            char ch = text.charAt(idx);
            if (ch == '\'' || ch == '\"') {
                if (consumeBackslash()) {
                    addChar(ch);
                    return Optional.of(idx + 1);
                } else {
                    endPlainTextToken();
                    int rightQuote = text.substring(idx + 1).indexOf('\'');
                    if (ch == '\'') {
                        addToken(new SingleQuotedToken(text.substring(idx + 1, rightQuote)));
                    } else {
                        addToken(new DoubleQuotedToken(text.substring(idx + 1, rightQuote)));
                    }
                    return Optional.of(rightQuote + 1);
                }
            }
            return Optional.empty();
        }

        private Optional<Integer> trySpecial(int idx) {
            char ch = text.charAt(idx);
            if (ch == '|' || ch == ';') {
                if (consumeBackslash()) {
                    addChar(ch);
                } else {
                    endAllTokens();
                    addToken(new PlainTextToken(Character.toString(ch)));
                }
                return Optional.of(idx + 1);
            }
            return Optional.empty();
        }

        private Optional<Integer> tryWhitespace(int idx) {
            char ch = text.charAt(idx);
            if (ch == '|' || ch == ';') {
                if (consumeBackslash()) {
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

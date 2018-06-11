package com.xosmig.swdesignhw.aush.token;

import java.util.*;

public class Tokenizer {

    /**
     * Splits `text` into a list of tokens.
     */
    public List<Token> tokenize(String text) throws IllegalArgumentException {
        return new Helper(text).tokenize();
    }

    private static class Helper {
        private static final Map<Character, Token> SPECIAL;
        static {
            SPECIAL = new TreeMap<>();
            SPECIAL.put(';', SemicolonToken.get());
            SPECIAL.put('|', PipeToken.get());
        }

        private final String text;
        private final List<Token> result = new ArrayList<>();
        private final List<Token> concatenated = new ArrayList<>();
        private final StringBuilder buffer = new StringBuilder();

        public Helper(String text) {
            this.text = text;
        }

        public List<Token> tokenize() throws IllegalArgumentException {
            for (int idx = 0; idx < text.length();) {
                // can't use idx directly in the closures, since it is not final nor effectively final
                final int curIdx = idx;
                idx = tryBackslash(curIdx).orElseGet(() ->
                        tryQuote(curIdx).orElseGet(() ->
                        trySpecial(curIdx).orElseGet(() ->
                        tryWhitespace(curIdx).orElseGet(() ->
                        tryPlainText(curIdx)))));
            }
            endConcatenatedSequence();

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

        private void endConcatenatedSequence() {
            endPlainTextToken();
            concatenated.stream().reduce(ConcatenatedToken::new).ifPresent(result::add);
            concatenated.clear();
        }

        private Optional<Integer> tryBackslash(int idx) throws IllegalArgumentException {
            char ch = text.charAt(idx);
            if (ch == '\\') {
                if (idx + 1 == text.length()) {
                    throw new IllegalArgumentException("Unexpected end of line");
                }
                addChar(text.charAt(idx + 1));
                return Optional.of(idx + 2);
            }
            return Optional.empty();
        }

        private Optional<Integer> tryQuote(int idx) throws IllegalArgumentException {
            char ch = text.charAt(idx);
            if (ch == '\'' || ch == '\"') {
                endPlainTextToken();
                int rightQuote = idx + 1 + text.substring(idx + 1).indexOf(ch);
                if (rightQuote == -1) {
                    throw new IllegalArgumentException("Quote without a pair");
                }
                if (ch == '\'') {
                    addAtomicToken(new SingleQuotedToken(text.substring(idx + 1, rightQuote)));
                } else {
                    addAtomicToken(new DoubleQuotedToken(text.substring(idx + 1, rightQuote)));
                }
                return Optional.of(rightQuote + 1);
            }
            return Optional.empty();
        }

        private Optional<Integer> trySpecial(int idx) {
            char ch = text.charAt(idx);
            if (SPECIAL.containsKey(ch)) {
                endConcatenatedSequence();
                addAtomicToken(SPECIAL.get(ch));
                endConcatenatedSequence();
                return Optional.of(idx + 1);
            }
            return Optional.empty();
        }

        private Optional<Integer> tryWhitespace(int idx) {
            char ch = text.charAt(idx);
            if (Character.isWhitespace(ch)) {
                endConcatenatedSequence();
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

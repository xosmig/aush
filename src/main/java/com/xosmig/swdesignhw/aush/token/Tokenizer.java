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
        private static final Map<CmdChar, Token> SPECIAL;

        static {
            SPECIAL = new HashMap<>();
            SPECIAL.put(CmdChar.get(';', false), SemicolonToken.get());
            SPECIAL.put(CmdChar.get('|', false), PipeToken.get());
        }

        private final CmdString text;
        private final List<Token> result = new ArrayList<>();
        private final List<Token> concatenated = new ArrayList<>();
        private final CmdStringBuilder buffer = new CmdStringBuilder();

        public Helper(String text) {
            this.text = CmdString.parse(text);
        }

        public List<Token> tokenize() throws IllegalArgumentException {
            for (int idx = 0; idx < text.length(); ) {
                // can't use idx directly in the closures, since it is not final nor effectively final
                final int curIdx = idx;
                idx = tryQuote(curIdx).orElseGet(() ->
                        trySpecial(curIdx).orElseGet(() ->
                                tryWhitespace(curIdx).orElseGet(() ->
                                        tryPlainText(curIdx))));
            }
            endConcatenatedSequence();

            return result;
        }

        private void addChar(CmdChar ch) {
            buffer.append(ch);
        }

        private void addAtomicToken(Token token) {
            concatenated.add(token);
        }

        private void endPlainTextToken() {
            if (buffer.isEmpty()) {
                return;
            }

            addAtomicToken(new PlainTextToken(buffer.finish()));
            buffer.clear();
        }

        private void endConcatenatedSequence() {
            endPlainTextToken();
            concatenated.stream().reduce(ConcatenatedToken::new).ifPresent(result::add);
            concatenated.clear();
        }

        private Optional<Integer> tryQuote(int idx) {
            CmdChar ch = text.charAt(idx);
            if (ch.equals(CmdChar.get('\"', false))) {
                endPlainTextToken();
                int rightQuoteRelative = text.substring(idx + 1).indexOf(ch);
                if (rightQuoteRelative == -1) {
                    throw new IllegalArgumentException("Double quote without a pair");
                }
                int rightQuote = idx + 1 + rightQuoteRelative;
                addAtomicToken(new DoubleQuotedToken(text.substring(idx + 1, rightQuote)));
                return Optional.of(rightQuote + 1);
            }
            return Optional.empty();
        }

        private Optional<Integer> trySpecial(int idx) {
            CmdChar ch = text.charAt(idx);
            if (SPECIAL.containsKey(ch)) {
                endConcatenatedSequence();
                addAtomicToken(SPECIAL.get(ch));
                endConcatenatedSequence();
                return Optional.of(idx + 1);
            }
            return Optional.empty();
        }

        private Optional<Integer> tryWhitespace(int idx) {
            CmdChar ch = text.charAt(idx);
            if (CmdChar.isWhitespace(ch)) {
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

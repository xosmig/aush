package com.xosmig.swdesignhw.aush.token;

import java.util.*;

public final class Tokenizer {

    private Tokenizer() {
    }

    /**
     * Converts the {@code text} to a list of tokens. All whitespaces are considered
     * as separators except the escaped ones and the ones within quotes.
     *
     * Escaping rules from {@code CmdString.parse(text)} are applied, and thus
     * the text should meet the requirements for this method.
     *
     * Each non-escaped quote in {@code text} should have a pair
     * (another non-escaped quote of the same type).
     *
     * @param text the text to be tokenized.
     * @throws IllegalArgumentException if
     */
    public static List<Token> tokenize(String text) throws IllegalArgumentException {
        return new TokenizeHelper(text).tokenize();
    }

    /**
     * The method opposite to tokenize. Returns a string which can be tokenized to
     * a token equal to the given one.
     * Note that there might be (or might not be) multiple different strings which can
     * be tokenized to the same set of tokens.
     *
     * @param token the token for the reverse transformation.
     * @return a sting that can be tokenized to a token equal to the given one.
     */
    public static String detokenize(Token token) {
        final DetokenizeVisitor visitor = new DetokenizeVisitor();
        token.accept(visitor);
        return visitor.result.toString();
    }

    private static final class TokenizeHelper {
        private static final Map<CmdChar, Token> SPECIAL;

        static {
            SPECIAL = new HashMap<>();
            SPECIAL.put(CmdChar.get(';', false), SemicolonToken.get());
            SPECIAL.put(CmdChar.get('|', false), PipeToken.get());
        }

        private final CmdString text;
        private final List<Token> result = new ArrayList<>();
        private final List<ConcatenatableToken> concatenated = new ArrayList<>();
        private final CmdStringBuilder buffer = new CmdStringBuilder();

        public TokenizeHelper(String text) {
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

        private void addConcatenatableToken(ConcatenatableToken token) {
            concatenated.add(token);
        }

        private void endPlainTextToken() {
            if (buffer.isEmpty()) {
                return;
            }

            addConcatenatableToken(new PlainTextToken(buffer.finish()));
            buffer.clear();
        }

        private void endConcatenatedSequence() {
            endPlainTextToken();
            concatenated.stream().reduce(ConcatenatedToken::concat).ifPresent(result::add);
            concatenated.clear();
        }

        private void addSeparateToken(Token token) {
            endConcatenatedSequence();
            result.add(token);
        }

        private Optional<Integer> tryQuote(int idx) {
            CmdChar ch = text.charAt(idx);
            CmdChar doubleQuote = CmdChar.get('\"', false);
            CmdChar singleQuote = CmdChar.get('\'', false);

            if (ch.equals(doubleQuote) || ch.equals(singleQuote)) {
                endPlainTextToken();
                final int rightQuoteRelative = text.substring(idx + 1).indexOf(ch);
                if (rightQuoteRelative == -1) {
                    throw new IllegalArgumentException("quote without a pair");
                }
                final int rightQuote = idx + 1 + rightQuoteRelative;

                final CmdString content = text.substring(idx + 1, rightQuote);
                if (ch.equals(doubleQuote)) {
                    addConcatenatableToken(new DoubleQuotedToken(content));
                } else {
                    addConcatenatableToken(new SingleQuotedToken(content));
                }

                return Optional.of(rightQuote + 1);
            }
            return Optional.empty();
        }

        private Optional<Integer> trySpecial(int idx) {
            CmdChar ch = text.charAt(idx);
            if (SPECIAL.containsKey(ch)) {
                addSeparateToken(SPECIAL.get(ch));
                return Optional.of(idx + 1);
            }
            return Optional.empty();
        }

        private Optional<Integer> tryWhitespace(int idx) {
            CmdChar ch = text.charAt(idx);
            if (CmdChar.isNonEscapedWhitespace(ch)) {
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

    private static final class DetokenizeVisitor implements TokenVisitor {

        public StringBuilder result = new StringBuilder();

        @Override
        public void visit(DoubleQuotedToken token) {
            result.append("\"");
            result.append(token.getContent().toString());
            result.append("\"");
        }

        @Override
        public void visit(SingleQuotedToken token) {
            result.append("\'");
            result.append(token.getContent().toString());
            result.append("\'");
        }

        @Override
        public void visit(PlainTextToken token) {
            result.append(token.getContent().toString());
        }

        @Override
        public void visit(ConcatenatedToken token) {
            token.getLeft().accept(this);
            token.getRight().accept(this);
        }

        @Override
        public void visit(SemicolonToken token) {
            result.append(";");
        }

        @Override
        public void visit(PipeToken token) {
            result.append("|");
        }
    }
}

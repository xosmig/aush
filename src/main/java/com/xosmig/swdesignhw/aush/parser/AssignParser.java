package com.xosmig.swdesignhw.aush.parser;

import com.xosmig.swdesignhw.aush.commands.AssignCommand;
import com.xosmig.swdesignhw.aush.commands.Command;
import com.xosmig.swdesignhw.aush.token.*;

import java.util.List;
import java.util.Optional;

public class AssignParser implements Parser {
    private final Parser childParser;

    public AssignParser(Parser childParser) {
        this.childParser = childParser;
    }

    @Override
    public Command parse(List<Token> text) throws ParseErrorException {
        if (text.isEmpty()) {
            return childParser.parse(text);
        }

        return text.get(0).accept(new AssignmentSearch())
                .map(res -> (Command) new AssignCommand(res.name, res.value))
                .orElse(childParser.parse(text));
    }

    private static final class SearchResult {
        public final String name;
        public final Token value;

        public SearchResult(String name, Token value) {
            this.name = name;
            this.value = value;
        }
    }

    private static final class AssignmentSearch implements TokenVisitor<Optional<SearchResult>> {
        @Override
        public Optional<SearchResult> visit(DoubleQuotedToken token) {
            return Optional.empty();
        }

        @Override
        public Optional<SearchResult> visit(SingleQuotedToken token) {
            return Optional.empty();
        }

        @Override
        public Optional<SearchResult> visit(PlainTextToken token) {
            int idx = token.getContent().indexOf('=');
            if (idx == -1 || idx == 0) {
                return Optional.empty();
            }

            String name = token.getContent().substring(0, idx);
            // check that the name consists only of letters
            for (int i = 0; i < name.length(); i++) {
                if (!Character.isLetter(name.charAt(i))) {
                    return Optional.empty();
                }
            }

            return Optional.of(new SearchResult(name,
                    new PlainTextToken(token.getContent().substring(idx + 1))));
        }

        @Override
        public Optional<SearchResult> visit(ConcatenatedToken token) {
            return token.getLeft().accept(this).map(cmd ->
                    new SearchResult(cmd.name, new ConcatenatedToken(cmd.value, token.getRight())));
        }
    }
}

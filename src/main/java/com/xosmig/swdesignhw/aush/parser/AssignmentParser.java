package com.xosmig.swdesignhw.aush.parser;

import com.xosmig.swdesignhw.aush.commands.AssignmentCommand;
import com.xosmig.swdesignhw.aush.commands.Command;
import com.xosmig.swdesignhw.aush.commands.LocalAssignmentCommand;
import com.xosmig.swdesignhw.aush.token.*;

import java.util.List;
import java.util.Optional;

public final class AssignmentParser implements Parser {
    private final Parser nextParser;

    public AssignmentParser(Parser nextParser) {
        this.nextParser = nextParser;
    }

    @Override
    public Command parse(List<Token> text) throws ParseErrorException {
        if (text.isEmpty()) {
            return nextParser.parse(text);
        }

        Optional<SearchResult> resultOpt = text.get(0).accept(new AssignmentSearch());
        if (!resultOpt.isPresent()) {
            return nextParser.parse(text);
        }
        SearchResult result = resultOpt.get();

        if (!AssignmentCommand.isValidName(result.name)) {
            throw new ParseErrorException("Invalid variable name in assignment: `" + result.name + "`");
        }

        if (text.size() > 1) {
            throw new ParseErrorException("Unexpected token after an assignment: `" +
                    text.get(1).backToString() + "`");
        }

        return new AssignmentCommand(result.name, result.value);
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
            if (!AssignmentCommand.isValidName(name)) {
                return Optional.empty();
            }

            SearchResult result =
                    new SearchResult(name, new PlainTextToken(token.getContent().substring(idx + 1)));
            return Optional.of(result);
        }

        @Override
        public Optional<SearchResult> visit(ConcatenatedToken token) {
            return token.getLeft().accept(this).map(cmd -> {
                return new SearchResult(cmd.name, new ConcatenatedToken(cmd.value, token.getRight()));
            });
        }

        @Override
        public Optional<SearchResult> visit(SemicolonToken token) {
            return Optional.empty();
        }

        @Override
        public Optional<SearchResult> visit(PipeToken token) {
            return Optional.empty();
        }
    }
}

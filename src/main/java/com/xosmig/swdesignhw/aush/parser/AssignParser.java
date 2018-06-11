package com.xosmig.swdesignhw.aush.parser;

import com.xosmig.swdesignhw.aush.commands.AssignmentCommand;
import com.xosmig.swdesignhw.aush.commands.Command;
import com.xosmig.swdesignhw.aush.commands.LocalAssignmentCommand;
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

        Optional<SearchResult> resultOpt = text.get(0).accept(new AssignmentSearch());

        if (!resultOpt.isPresent()) {
            return childParser.parse(text);
        }
        SearchResult result = resultOpt.get();
        AssignmentCommand assignment = new AssignmentCommand(result.name, result.value);

        if (text.size() == 1) {
            return assignment;
        }

        return new LocalAssignmentCommand(assignment,
                childParser.parse(text.subList(1, text.size())));
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

        @Override
        public Optional<SearchResult> visit(SemicolonToken token) {
            return null; // TODO
        }

        @Override
        public Optional<SearchResult> visit(PipeToken token) {
            return null; // TODO
        }
    }
}

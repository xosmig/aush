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

        Optional<AssignmentCommand> resultOpt;
        try {
            resultOpt = text.get(0).accept(new AssignmentSearch());
        } catch (RuntimeException e) {
            if (e.getCause() instanceof ParseErrorException) {
                throw (ParseErrorException) e.getCause();
            }
            throw e;
        }

        if (!resultOpt.isPresent()) {
            return nextParser.parse(text);
        }
        if (text.size() > 1) {
            throw new ParseErrorException("Unexpected token after an assignment: `" +
                    text.get(1).backToString() + "`");
        }

        return resultOpt.get();
    }

    private static final class AssignmentSearch implements TokenVisitor<Optional<AssignmentCommand>> {
        @Override
        public Optional<AssignmentCommand> visit(DoubleQuotedToken token) {
            return Optional.empty();
        }

        @Override
        public Optional<AssignmentCommand> visit(PlainTextToken token) {
            int idx = token.getContent().indexOf(CmdChar.get('=', false));
            if (idx == -1 || idx == 0) {
                return Optional.empty();
            }

            String name = token.getContent().substring(0, idx).toString();
            if (!AssignmentCommand.isValidName(name)) {
                throw new RuntimeException(
                        new ParseErrorException("Invalid variable name in assignment: `" + name + "`"));
            }

            Token value = new PlainTextToken(token.getContent().substring(idx + 1));
            return Optional.of(new AssignmentCommand(name, value));
        }

        @Override
        public Optional<AssignmentCommand> visit(ConcatenatedToken token) {
            return token.getLeft().accept(this).map(cmd -> {
                return new AssignmentCommand(cmd.getName(),
                        new ConcatenatedToken(cmd.getValue(), token.getRight()));
            });
        }

        @Override
        public Optional<AssignmentCommand> visit(SemicolonToken token) {
            return Optional.empty();
        }

        @Override
        public Optional<AssignmentCommand> visit(PipeToken token) {
            return Optional.empty();
        }
    }
}

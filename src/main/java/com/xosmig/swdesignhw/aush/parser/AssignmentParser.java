package com.xosmig.swdesignhw.aush.parser;

import com.xosmig.swdesignhw.aush.commands.AssignmentCommand;
import com.xosmig.swdesignhw.aush.commands.Command;
import com.xosmig.swdesignhw.aush.token.*;

import java.util.List;

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

        AssignmentSearch search = new AssignmentSearch();
        try {
            text.get(0).accept(search);
        } catch (RuntimeException e) {
            if (e.getCause() instanceof ParseErrorException) {
                throw (ParseErrorException) e.getCause();
            }
            throw e;
        }

        if (search.result == null) {
            return nextParser.parse(text);
        }
        if (text.size() > 1) {
            throw new ParseErrorException("Unexpected token after an assignment: " +
                    "`" + Tokenizer.detokenize(text.get(1)) + "`");
        }

        return search.result;
    }

    private static final class AssignmentSearch implements TokenVisitor {
        public AssignmentCommand result = null;

        @Override
        public void visit(PlainTextToken token) {
            int idx = token.getContent().indexOf(CmdChar.get('=', false));
            if (idx == -1 || idx == 0) {
                return;
            }

            String name = token.getContent().substring(0, idx).toString();
            if (!AssignmentCommand.isValidName(name)) {
                throw new RuntimeException(
                        new ParseErrorException("Invalid variable name in assignment: `" + name + "`"));
            }

            Token value = new PlainTextToken(token.getContent().substring(idx + 1));
            result = new AssignmentCommand(name, value);
        }

        @Override
        public void visit(ConcatenatedToken token) {
            token.getLeft().accept(this);
            if (result == null) {
                return;
            }
            Token value = ConcatenatedToken.concat((ConcatenatableToken) result.getValueToken(), token.getRight());
            result = new AssignmentCommand(result.getName(), value);
        }

        @Override
        public void visit(DoubleQuotedToken token) {
        }

        @Override
        public void visit(SemicolonToken token) {
        }

        @Override
        public void visit(PipeToken token) {
        }
    }
}

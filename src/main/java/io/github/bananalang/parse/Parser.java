package io.github.bananalang.parse;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import io.github.bananalang.parse.ast.ASTNode;
import io.github.bananalang.parse.ast.ImportStatement;
import io.github.bananalang.parse.ast.StatementList;
import io.github.bananalang.parse.token.IdentifierToken;
import io.github.bananalang.parse.token.LiteralToken;
import io.github.bananalang.parse.token.ReservedToken;
import io.github.bananalang.parse.token.Token;

public final class Parser {
    private static final String IMPORT_STATEMENT = "import statement";

    private Tokenizer tokenizer;
    private List<Token> inputTokens;
    private StatementList root;
    private int i;

    public Parser(List<Token> tokens) {
        this.tokenizer = null;
        this.inputTokens = tokens;
    }

    public Parser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
        this.inputTokens = null;
    }

    public Parser(Reader inputReader) {
        this.tokenizer = new Tokenizer(inputReader);
        this.inputTokens = null;
    }

    public Parser(String inputString) {
        this.tokenizer = new Tokenizer(inputString);
        this.inputTokens = null;
    }

    public ASTNode parse() throws IOException {
        if (root == null) {
            if (inputTokens == null) {
                inputTokens = tokenizer.tokenize();
                tokenizer = null;
            }
            parse0();
        }
        return root;
    }

    private void parse0() {
        root = new StatementList(1, 1);
        while (hasNext()) {
            Token tok = next();
            if (ReservedToken.matchReservedWord(tok, ReservedToken.IMPORT)) {
                root.children.add(importStatement());
            }
        }
    }

    private ImportStatement importStatement() {
        Token tok;
        StringBuilder module = new StringBuilder();
        String last = null;
        while (true) {
            tok = nextOrError(IMPORT_STATEMENT);
            if (tok instanceof IdentifierToken) {
                if (last != null) {
                    module.append(last).append('/');
                }
                last = ((IdentifierToken)tok).identifier;
                tok = nextOrError(IMPORT_STATEMENT);
                if (LiteralToken.matchLiteral(tok, ";")) {
                    if (module.length() == 0) {
                        error("Expected . after identifier in import, but import statement ended");
                    }
                    module.setLength(module.length() - 1); // Remove trailing /
                    break;
                }
                if (!LiteralToken.matchLiteral(tok, ".")) {
                    error("Expected . in import, not " + tok);
                }
            } else if (LiteralToken.matchLiteral(tok, "*")) {
                if (last == null) {
                    error("Cannot import * by itself");
                }
                module.append(last);
                last = "*";
                tok = nextOrError(IMPORT_STATEMENT);
                if (!LiteralToken.matchLiteral(tok, ";")) {
                    error("Expected ; after * in import, not " + tok);
                }
                break;
            } else {
                error("Expected identifier or * in import, not " + tok);
            }
        }
        assert last != null;
        return new ImportStatement(module.toString(), last, tok.row, tok.column);
    }

    // Utility methods
    private void unexpectedToken(Token tok) {
        unexpectedToken(tok, null);
    }

    private void unexpectedToken(Token tok, String where) {
        error("Unexpected token " + (where == null ? "" : (where + " ")) + tok);
    }

    private void error() {
        throw new SyntaxException(last().row, last().column);
    }

    private void error(String message) {
        throw new SyntaxException(message, last().row, last().column);
    }

    private boolean hasNext() {
        return i < inputTokens.size();
    }

    private Token last() {
        return i == 0 ? null : inputTokens.get(i - 1);
    }

    private Token peek() {
        return safeTokenAt(i);
    }

    private Token nextOrError() {
        return nextOrError(null);
    }

    private Token nextOrError(String inWhat) {
        Token c = next();
        if (c == null) {
            error(inWhat == null ? "EOF" : ("EOF in " + inWhat));
        }
        return c;
    }

    private Token next() {
        if (i < inputTokens.size()) {
            return inputTokens.get(i++);
        }
        return null;
    }

    private int advance() {
        if (i < inputTokens.size()) {
            i++;
        }
        return i - 1;
    }

    private Token safeTokenAt(int i) {
        return i < inputTokens.size() ? inputTokens.get(i) : null;
    }
}

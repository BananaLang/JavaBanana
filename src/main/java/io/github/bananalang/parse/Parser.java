package io.github.bananalang.parse;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import io.github.bananalang.parse.ast.ASTNode;
import io.github.bananalang.parse.ast.StatementList;
import io.github.bananalang.parse.token.Token;

public final class Parser {
    private Tokenizer tokenizer;
    private List<Token> inputTokens;
    private ASTNode root;

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

    public void parse0() {
        root = new StatementList(1, 1);
    }
}

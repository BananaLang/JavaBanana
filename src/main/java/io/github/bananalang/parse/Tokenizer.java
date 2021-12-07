package io.github.bananalang.parse;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import io.github.bananalang.parse.token.LiteralToken;
import io.github.bananalang.parse.token.Token;

public final class Tokenizer {
    public final Reader inputReader;
    private String input;
    private List<Token> tokens;
    private int row, column;
    private int i, inputLength;

    public Tokenizer(Reader inputReader) {
        this.inputReader = inputReader;
    }

    public Tokenizer(String inputString) {
        this.inputReader = null;
        this.input = inputString;
        this.inputLength = inputString.length();
    }

    public List<Token> tokenize() throws IOException {
        if (this.tokens == null) {
            this.tokens = new ArrayList<>();
            if (input == null) {
                char[] cs = new char[8192];
                StringBuilder result = new StringBuilder();
                while (inputReader.read(cs) > 0) result.append(cs);
                input = result.toString();
                inputLength = result.length();
            }
            tokenize0();
        }
        return this.tokens;
    }

    private void tokenize0() throws IOException {
        row = 1;
        column = 0;
        while (hasNext()) {
            char c = next();
            switch (c) {
                case ' ':
                case '\t':
                case '\r':
                case '\n':
                    continue;
                case '/':
                    if (advanceIfEqual('/')) {
                        while (hasNext() && input.charAt(i++) != '\n');
                        row++;
                        column = 1;
                    }
                    continue;
                case '{':
                case '}':
                case '(':
                case ')':
                case ',':
                case '.':
                case ';':
                    tokens.add(new LiteralToken(String.valueOf(c)));
                    continue;
                case '+':
                    if (peek() == '=') {
                        tokens.add(new LiteralToken("+="));
                        advance();
                    } else if (peek() == '+') {
                        tokens.add(new LiteralToken("++"));
                        advance();
                    } else {
                        tokens.add(new LiteralToken("+"));
                    }
                    continue;
                default:
                    throw new SyntaxException("Unexpected character '" + c + "'", row, column);
            }
        }
        System.out.println(row);
        System.out.println(column);
    }

    private boolean hasNext() {
        return i < inputLength;
    }

    private boolean advanceIfEqual(char x) {
        if (peek() == x) {
            advance();
            return true;
        }
        return false;
    }

    private char peek() {
        return safeCharAt(i);
    }

    private char next() {
        if (i < inputLength) {
            char c;
            if ((c = input.charAt(i++)) == '\n') {
                row++;
                column = 1;
            } else {
                column++;
            }
            return c;
        }
        return '\0';
    }

    private int advance() {
        if (i < inputLength) {
            if (input.charAt(i++) == '\n') {
                row++;
                column = 1;
            } else {
                column++;
            }
        }
        return i - 1;
    }

    private char safeCharAt(int i) {
        return i < inputLength ? input.charAt(i) : '\0';
    }
}

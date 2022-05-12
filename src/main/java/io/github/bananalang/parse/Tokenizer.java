package io.github.bananalang.parse;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import io.github.bananalang.JavaBananaConstants;
import io.github.bananalang.compilecommon.problems.ProblemCollector;
import io.github.bananalang.parse.token.DecimalToken;
import io.github.bananalang.parse.token.IdentifierToken;
import io.github.bananalang.parse.token.IntegerToken;
import io.github.bananalang.parse.token.LiteralToken;
import io.github.bananalang.parse.token.StringToken;
import io.github.bananalang.parse.token.Token;

public final class Tokenizer {
    private final ProblemCollector problemCollector;
    private Reader inputReader;
    private String input;
    private List<Token> tokens;
    private int row, column;
    private int i, inputLength;

    public Tokenizer(Reader inputReader, ProblemCollector problemCollector) {
        this.problemCollector = problemCollector;
        this.inputReader = inputReader;
    }

    public Tokenizer(String inputString, ProblemCollector problemCollector) {
        this.problemCollector = problemCollector;
        this.inputReader = null;
        this.input = inputString;
        this.inputLength = inputString.length();
    }

    public List<Token> tokenize() throws IOException {
        if (tokens == null) {
            tokens = new ArrayList<>();
            if (input == null) {
                char[] cs = new char[8192];
                StringBuilder result = new StringBuilder();
                int n;
                while ((n = inputReader.read(cs)) > 0) result.append(cs, 0, n);
                input = result.toString();
                inputLength = result.length();
                inputReader = null;
            }
            double startTime = System.nanoTime();
            if (JavaBananaConstants.DEBUG) {
                System.out.println("Beginning tokenize of 0x" + Integer.toHexString(System.identityHashCode(input)));
            }
            try {
                tokenize0();
            } catch (SyntaxException e) {
                problemCollector.error(e.getMessage(), e.row, e.column);
            }
            if (JavaBananaConstants.DEBUG) {
                System.out.println("Finished tokenize in " + (System.nanoTime() - startTime) / 1_000_000D + "ms");
            }
            problemCollector.throwIfFailing();
        }
        return tokens;
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
                    } else if (advanceIfEqual('*')) {
                        while (true) {
                            char next = nextOrError("multiline comment");
                            if (next == '*') {
                                if (peek() == '/') {
                                    advance();
                                    break;
                                }
                            }
                        }
                    } else if (advanceIfEqual('=')) {
                        tokens.add(new LiteralToken("/=", row, column));
                    } else {
                        tokens.add(new LiteralToken("/", row, column));
                    }
                    continue;
                case '{':
                case '}':
                case '(':
                case ')':
                case ',':
                case '.':
                case ':':
                case ';':
                    tokens.add(new LiteralToken(String.valueOf(c), row, column));
                    continue;
                case '?':
                    if (advanceIfEqual('.')) {
                        tokens.add(new LiteralToken("?.", row, column));
                    } else if (advanceIfEqual('?')) {
                        if (advanceIfEqual('=')) {
                            tokens.add(new LiteralToken("??=", row, column));
                        } else {
                            tokens.add(new LiteralToken("??", row, column));
                        }
                    } else {
                        tokens.add(new LiteralToken("?", row, column));
                    }
                    continue;
                case '+':
                case '-':
                    if (advanceIfEqual('=')) {
                        tokens.add(new LiteralToken("-=", row, column));
                    } else if (advanceIfEqual(c)) {
                        tokens.add(new LiteralToken("--", row, column));
                    } else if (advanceIfEqual('>')) {
                        tokens.add(new LiteralToken("->", row, column));
                    } else {
                        tokens.add(new LiteralToken("-", row, column));
                    }
                    continue;
                case '&':
                case '|':
                    if (advanceIfEqual('=')) {
                        tokens.add(new LiteralToken(new String(new char[] {c, '='}), row, column));
                    } else if (advanceIfEqual(c)) {
                        tokens.add(new LiteralToken(new String(new char[] {c, c}), row, column));
                    } else {
                        tokens.add(new LiteralToken(String.valueOf(c), row, column));
                    }
                    continue;
                case '*':
                case '%':
                case '!':
                case '=':
                case '^':
                    if (advanceIfEqual('=')) {
                        if ((c == '=' || c == '!') && advanceIfEqual('=')) {
                            tokens.add(new LiteralToken(new String(new char[] {c, '=', '='}), row, column));
                        } else {
                            tokens.add(new LiteralToken(new String(new char[] {c, '='}), row, column));
                        }
                    } else if (advanceIfEqual('!')) {
                        tokens.add(new LiteralToken("!!", row, column));
                    } else {
                        tokens.add(new LiteralToken(String.valueOf(c), row, column));
                    }
                    continue;
                case '>':
                case '<':
                    if (advanceIfEqual('=')) {
                        tokens.add(new LiteralToken(new String(new char[] {c, '='}), row, column));
                    } else if (advanceIfEqual(c)) {
                        if (advanceIfEqual('=')) {
                            tokens.add(new LiteralToken(new String(new char[] {c, c, '='}), row, column));
                        } else {
                            tokens.add(new LiteralToken(new String(new char[] {c, c}), row, column));
                        }
                    } else {
                        tokens.add(new LiteralToken(String.valueOf(c), row, column));
                    }
                    continue;
                case 'a': case 'b': case 'c': case 'd': case 'e': case 'f': case 'g': case 'h': case 'i': case 'j': case 'k':
                case 'l': case 'm': case 'n': case 'o': case 'p': case 'q': case 'r': case 's': case 't': case 'u': case 'v':
                case 'w': case 'x': case 'y': case 'z': case 'A': case 'B': case 'C': case 'D': case 'E': case 'F': case 'G':
                case 'H': case 'I': case 'J': case 'K': case 'L': case 'M': case 'N': case 'O': case 'P': case 'Q': case 'R':
                case 'S': case 'T': case 'U': case 'V': case 'W': case 'X': case 'Y': case 'Z': case '_':
                    tokens.add(identifier(c));
                    continue;
                case '0': case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9':
                    tokens.add(number(c));
                    continue;
                case '"':
                    tokens.add(string());
                    continue;
            }
            error("Unexpected character '" + c + "'");
        }
    }

    private Token identifier(char c) {
        StringBuilder ident = new StringBuilder().append(c);
        while (hasNext() && CharData.isValidIdentifierMid(c = input.charAt(i))) {
            ident.append(c);
            advance();
        }
        return IdentifierToken.identifierOrReserved(ident.toString(), row, column);
    }

    private Token number(char c) {
        StringBuilder result = new StringBuilder();
        boolean isDecimal = false;
        if (c != '0') {
            if (!CharData.isDigit(c)) {
                error("Expected number");
            }
            do {
                result.append(c);
                c = next();
            } while (c != '\0' && CharData.isDigit(c));
        } else {
            result.append('0');
            c = next();
        }
        if (c == '.') {
            isDecimal = true;
            do {
                result.append(c);
                c = next();
            } while (c != '\0' && CharData.isDigit(c));
        }
        if (c == 'e' || c == 'E') {
            isDecimal = true;
            result.append(c);
            c = nextOrError("decimal literal");
            if (c != '-' && c != '+' && !CharData.isDigit(c)) {
                error("Exponential notation missing exponent");
            }
            do {
                result.append(c);
                c = next();
            } while (c != '\0' && CharData.isDigit(c));
        }
        if (c != '\0') i--;
        if (isDecimal) {
            return new DecimalToken(result.toString(), row, column);
        }
        return new IntegerToken(result.toString(), row, column);
    }

    private StringToken string() {
        StringBuilder result = new StringBuilder();
        char c;
        while ((c = nextOrError("string literal")) != '"') {
            if (c == '\\') {
                char control = nextOrError("string escape");
                if (control == 'u') {
                    char[] uni = new char[4];
                    for (int i = 0; i < 4; i++) {
                        uni[i] = nextOrError("unicode escape");
                    }
                    result.append((char)Integer.parseUnsignedInt(new String(uni), 16));
                } else {
                    Character escape = CharData.CONTROL_CODES.get(control);
                    if (escape == null) {
                        error("Unkown string escape '" + escape + "'");
                    }
                    result.append(escape);
                }
                continue;
            }
            result.append(c);
        }
        return new StringToken(result.toString(), row, column);
    }

    // Utility methods
    private void error(String message) {
        throw new SyntaxException(message, row, column);
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

    @SuppressWarnings("unused")
    private char nextOrError() {
        return nextOrError(null);
    }

    private char nextOrError(String inWhat) {
        char c = next();
        if (c == '\0') {
            error(inWhat == null ? "EOF" : ("EOF in " + inWhat));
        }
        return c;
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

package io.github.bananalang.parse;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import io.github.bananalang.parse.ast.ASTNode;
import io.github.bananalang.parse.ast.AssignmentExpression;
import io.github.bananalang.parse.ast.BinaryExpression;
import io.github.bananalang.parse.ast.BinaryExpression.BinaryOperator;
import io.github.bananalang.parse.ast.BooleanExpression;
import io.github.bananalang.parse.ast.CallExpression;
import io.github.bananalang.parse.ast.DecimalExpression;
import io.github.bananalang.parse.ast.ExpressionNode;
import io.github.bananalang.parse.ast.ExpressionStatement;
import io.github.bananalang.parse.ast.IdentifierExpression;
import io.github.bananalang.parse.ast.ImportStatement;
import io.github.bananalang.parse.ast.IntegerExpression;
import io.github.bananalang.parse.ast.StatementList;
import io.github.bananalang.parse.ast.StatementNode;
import io.github.bananalang.parse.ast.StringExpression;
import io.github.bananalang.parse.ast.UnaryExpression;
import io.github.bananalang.parse.ast.UnaryExpression.UnaryOperator;
import io.github.bananalang.parse.ast.VariableDeclarationStatement;
import io.github.bananalang.parse.ast.VariableDeclarationStatement.VariableDeclaration;
import io.github.bananalang.parse.token.DecimalToken;
import io.github.bananalang.parse.token.IdentifierToken;
import io.github.bananalang.parse.token.IntegerToken;
import io.github.bananalang.parse.token.LiteralToken;
import io.github.bananalang.parse.token.ReservedToken;
import io.github.bananalang.parse.token.StringToken;
import io.github.bananalang.parse.token.Token;

public final class Parser {
    private static final String IMPORT_STATEMENT = "import statement";
    private static final String VARIABLE_DECLARATION = "variable declaration";
    private static final String INFERRED_TYPE_MISSING_ASSIGNMENT = "Cannot create variable with inferred type without assignment";

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
            } else {
                root.children.add(statement(tok));
            }
        }
    }

    private StatementNode statement(Token tok) {
        if (ReservedToken.matchReservedWord(tok, ReservedToken.DEF)) {
            return variableDeclaration();
        } else {
            ExpressionStatement result = new ExpressionStatement(expression(tok));
            if (!LiteralToken.matchLiteral(tok = nextOrErrorMessage("Expected ; after expression statement"), ";")) {
                error("Expected ; after expression statement, not " + tok);
            }
            return result;
        }
    }

    private ExpressionNode expression() {
        return expression(nextOrErrorMessage("Expect expression"));
    }

    private ExpressionNode expression(Token tok) {
        return assignment(tok);
    }

    private ExpressionNode assignment(Token tok) {
        ExpressionNode assignee = logicalOr(tok);
        if (assignee instanceof IdentifierExpression) {
            if (LiteralToken.matchLiteral(peek(), "=")) {
                advance();
                ExpressionNode value = expression();
                return new AssignmentExpression(assignee, value, tok.row, tok.column);
            }
        }
        return assignee;
    }

    private ExpressionNode logicalOr(Token tok) {
        ExpressionNode left = logicalAnd(tok);
        if (LiteralToken.matchLiteral(peek(), "||")) {
            advance();
            ExpressionNode right = expression();
            return new BinaryExpression(left, right, BinaryOperator.LOGICAL_OR, tok.row, tok.column);
        }
        return left;
    }

    private ExpressionNode logicalAnd(Token tok) {
        ExpressionNode left = bitwiseOr(tok);
        if (LiteralToken.matchLiteral(peek(), "&&")) {
            advance();
            ExpressionNode right = expression();
            return new BinaryExpression(left, right, BinaryOperator.LOGICAL_AND, tok.row, tok.column);
        }
        return left;
    }

    private ExpressionNode bitwiseOr(Token tok) {
        ExpressionNode left = bitwiseXor(tok);
        if (LiteralToken.matchLiteral(peek(), "|")) {
            advance();
            ExpressionNode right = expression();
            return new BinaryExpression(left, right, BinaryOperator.BITWISE_OR, tok.row, tok.column);
        }
        return left;
    }

    private ExpressionNode bitwiseXor(Token tok) {
        ExpressionNode left = bitwiseAnd(tok);
        if (LiteralToken.matchLiteral(peek(), "^")) {
            advance();
            ExpressionNode right = expression();
            return new BinaryExpression(left, right, BinaryOperator.BITWISE_XOR, tok.row, tok.column);
        }
        return left;
    }

    private ExpressionNode bitwiseAnd(Token tok) {
        ExpressionNode left = equality(tok);
        if (LiteralToken.matchLiteral(peek(), "&")) {
            advance();
            ExpressionNode right = expression();
            return new BinaryExpression(left, right, BinaryOperator.BITWISE_AND, tok.row, tok.column);
        }
        return left;
    }

    private ExpressionNode equality(Token tok) {
        ExpressionNode left = relational(tok);
        Token rightToken = peek();
        if (rightToken instanceof LiteralToken) {
            BinaryOperator op;
            switch (((LiteralToken)rightToken).literal) {
                case "==":
                    op = BinaryOperator.EQUALS;
                    break;
                case "!=":
                    op = BinaryOperator.NOT_EQUALS;
                    break;
                default:
                    return left;
            }
            advance();
            ExpressionNode right = expression();
            return new BinaryExpression(left, right, op, tok.row, tok.column);
        }
        return left;
    }

    private ExpressionNode relational(Token tok) {
        ExpressionNode left = bitShift(tok);
        Token rightToken = peek();
        if (rightToken instanceof LiteralToken) {
            BinaryOperator op;
            switch (((LiteralToken)rightToken).literal) {
                case "<":
                    op = BinaryOperator.LESS_THAN;
                    break;
                case ">":
                    op = BinaryOperator.GREATER_THAN;
                    break;
                case "<=":
                    op = BinaryOperator.LESS_THAN_EQUALS;
                    break;
                case ">=":
                    op = BinaryOperator.GREATER_THAN_EQUALS;
                    break;
                default:
                    return left;
            }
            advance();
            ExpressionNode right = expression();
            return new BinaryExpression(left, right, op, tok.row, tok.column);
        }
        return left;
    }

    private ExpressionNode bitShift(Token tok) {
        ExpressionNode left = addition(tok);
        Token rightToken = peek();
        if (rightToken instanceof LiteralToken) {
            BinaryOperator op;
            switch (((LiteralToken)rightToken).literal) {
                case "<<":
                    op = BinaryOperator.LEFT_SHIFT;
                    break;
                case ">>":
                    op = BinaryOperator.RIGHT_SHIFT;
                    break;
                default:
                    return left;
            }
            advance();
            ExpressionNode right = expression();
            return new BinaryExpression(left, right, op, tok.row, tok.column);
        }
        return left;
    }

    private ExpressionNode addition(Token tok) {
        ExpressionNode left = multiplication(tok);
        Token rightToken = peek();
        if (rightToken instanceof LiteralToken) {
            BinaryOperator op;
            switch (((LiteralToken)rightToken).literal) {
                case "+":
                    op = BinaryOperator.ADD;
                    break;
                case "-":
                    op = BinaryOperator.SUBTRACT;
                    break;
                default:
                    return left;
            }
            advance();
            ExpressionNode right = expression();
            return new BinaryExpression(left, right, op, tok.row, tok.column);
        }
        return left;
    }

    private ExpressionNode multiplication(Token tok) {
        ExpressionNode left = unary(tok);
        Token rightToken = peek();
        if (rightToken instanceof LiteralToken) {
            BinaryOperator op;
            switch (((LiteralToken)rightToken).literal) {
                case "*":
                    op = BinaryOperator.MULTIPLY;
                    break;
                case "/":
                    op = BinaryOperator.DIVIDE;
                    break;
                case "%":
                    op = BinaryOperator.MODULUS;
                    break;
                default:
                    return left;
            }
            advance();
            ExpressionNode right = expression();
            return new BinaryExpression(left, right, op, tok.row, tok.column);
        }
        return left;
    }

    private ExpressionNode unary(Token tok) {
        if (tok instanceof LiteralToken) {
            UnaryOperator op;
            switch (((LiteralToken)tok).literal) {
                case "++":
                    op = UnaryOperator.PRE_INCREMENT;
                    break;
                case "--":
                    op = UnaryOperator.PRE_DECREMENT;
                    break;
                case "+":
                    op = UnaryOperator.PLUS;
                    break;
                case "-":
                    op = UnaryOperator.NEGATE;
                    break;
                case "!":
                    op = UnaryOperator.NOT;
                    break;
                case "~":
                    op = UnaryOperator.BITWISE_INVERT;
                    break;
                default:
                    op = null;
            }
            if (op != null) {
                return new UnaryExpression(expression(), op, tok.row, tok.column);
            }
        }
        return call(tok);
    }

    private ExpressionNode call(Token tok) {
        ExpressionNode target = primary(tok);
        while (LiteralToken.matchLiteral(peek(), "(")) {
            next();
            List<ExpressionNode> args = new ArrayList<>();
            tok = nextOrErrorMessage("Expected ) or argument in argument list");
            if (!LiteralToken.matchLiteral(tok, ")")) {
                while (true) {
                    args.add(expression(tok));
                    tok = nextOrErrorMessage("Expected ) or , in argument list");
                    if (LiteralToken.matchLiteral(tok, ")")) {
                        break;
                    } else if (LiteralToken.matchLiteral(tok, ",")) {
                        continue;
                    } else {
                        error("Expected ) or , in argument list, not " + tok);
                    }
                }
            }
            target = new CallExpression(target, args.toArray(new ExpressionNode[0]), target.row, target.column);
        }
        return target;
    }

    private ExpressionNode primary(Token tok) {
        if (tok instanceof IntegerToken) {
            return new IntegerExpression(((IntegerToken)tok).value, tok.row, tok.column);
        } else if (tok instanceof DecimalToken) {
            return new DecimalExpression(((DecimalToken)tok).value, tok.row, tok.column);
        } else if (tok instanceof StringToken) {
            return new StringExpression(((StringToken)tok).value, tok.row, tok.column);
        } else if (tok instanceof IdentifierToken) {
            return new IdentifierExpression(((IdentifierToken)tok).identifier, tok.row, tok.column);
        } else if (ReservedToken.matchReservedWord(tok, ReservedToken.TRUE)) {
            return new BooleanExpression(true, tok.row, tok.column);
        } else if (ReservedToken.matchReservedWord(tok, ReservedToken.FALSE)) {
            return new BooleanExpression(false, tok.row, tok.column);
        } else if (LiteralToken.matchLiteral(tok, "(")) {
            ExpressionNode result = expression();
            if (!LiteralToken.matchLiteral(tok = nextOrErrorMessage("Expected ) after paranthesized expression"), ")")) {
                error("Expected ) after paranthesized expression, not " + tok);
            }
            return result;
        } else {
            error("Unexpected token in expression " + tok);
            return null; // UNREACHABLE
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

    private VariableDeclarationStatement variableDeclaration() {
        Token tok;
        List<VariableDeclaration> declarations = new ArrayList<>();
        while (true) {
            String type;
            tok = nextOrError(VARIABLE_DECLARATION);
            if (tok instanceof IdentifierToken) {
                type = ((IdentifierToken)tok).identifier;
            } else if (ReservedToken.matchReservedWord(tok, ReservedToken.VAR)) {
                type = null;
            } else {
                error("Expected type name or var in variable declaration, not " + tok);
                break; // UNREACHABLE
            }
            tok = nextOrError(VARIABLE_DECLARATION);
            if (!(tok instanceof IdentifierToken)) {
                error("Expected variable name in variable declaration, not " + tok);
            }
            String name = ((IdentifierToken)tok).identifier;
            ExpressionNode value;
            tok = nextOrError(VARIABLE_DECLARATION);
            if (LiteralToken.matchLiteral(tok, "=")) {
                value = expression();
                tok = nextOrErrorMessage("Expected ; or , after variable declaration");
            } else {
                value = null;
            }
            if (type != null || value != null) { // Otherwise we error a few lines down
                declarations.add(new VariableDeclaration(type, name, value));
            }
            if (LiteralToken.matchLiteral(tok, ",")) {
                if (type == null && value == null) {
                    error(INFERRED_TYPE_MISSING_ASSIGNMENT);
                }
                continue;
            } else if (LiteralToken.matchLiteral(tok, ";")) {
                if (type == null && value == null) {
                    error(INFERRED_TYPE_MISSING_ASSIGNMENT);
                }
                break;
            } else {
                error("Expected ; or , after variable declaration, not " + tok);
            }
        }
        return new VariableDeclarationStatement(declarations.toArray(new VariableDeclaration[0]), tok.row, tok.column);
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

    private Token nextOrErrorMessage(String message) {
        Token c = next();
        if (c == null) {
            error(message);
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

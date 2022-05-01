package io.github.bananalang.parse;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import io.github.bananalang.parse.ast.AccessExpression;
import io.github.bananalang.parse.ast.AssignmentExpression;
import io.github.bananalang.parse.ast.BinaryExpression;
import io.github.bananalang.parse.ast.BinaryExpression.BinaryOperator;
import io.github.bananalang.parse.ast.BooleanExpression;
import io.github.bananalang.parse.ast.CallExpression;
import io.github.bananalang.parse.ast.DecimalExpression;
import io.github.bananalang.parse.ast.ExpressionNode;
import io.github.bananalang.parse.ast.ExpressionStatement;
import io.github.bananalang.parse.ast.FunctionDefinitionStatement;
import io.github.bananalang.parse.ast.IdentifierExpression;
import io.github.bananalang.parse.ast.IfOrWhileStatement;
import io.github.bananalang.parse.ast.ImportStatement;
import io.github.bananalang.parse.ast.IntegerExpression;
import io.github.bananalang.parse.ast.IterationForStatement;
import io.github.bananalang.parse.ast.NullExpression;
import io.github.bananalang.parse.ast.ReturnStatement;
import io.github.bananalang.parse.ast.StatementList;
import io.github.bananalang.parse.ast.StatementNode;
import io.github.bananalang.parse.ast.StringExpression;
import io.github.bananalang.parse.ast.ThreePartForStatement;
import io.github.bananalang.parse.ast.UnaryExpression;
import io.github.bananalang.parse.ast.UnaryExpression.UnaryOperator;
import io.github.bananalang.parse.ast.VariableDeclarationStatement;
import io.github.bananalang.parse.ast.VariableDeclarationStatement.Modifier;
import io.github.bananalang.parse.ast.VariableDeclarationStatement.TypeReference;
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
    private static final String FUNCTION_DEFINITION = "function definition";
    private static final String INFERRED_TYPE_MISSING_ASSIGNMENT = "Cannot create variable with inferred type without assignment";
    private static final String EXPECT_EXPRESSION = "Expect expression";

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

    public StatementList parse() throws IOException {
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
            return variableDeclaration(false);
        } else if (ReservedToken.matchReservedWord(tok, ReservedToken.IF)) {
            return ifOrWhileStatement(tok, false);
        } else if (ReservedToken.matchReservedWord(tok, ReservedToken.WHILE)) {
            return ifOrWhileStatement(tok, true);
        } else if (ReservedToken.matchReservedWord(tok, ReservedToken.FOR)) {
            return forStatement(tok);
        } else if (ReservedToken.matchReservedWord(tok, ReservedToken.RETURN)) {
            return returnStatement(tok);
        } else if (LiteralToken.matchLiteral(tok, "{")) {
            return block(tok);
        } else {
            ExpressionStatement result = new ExpressionStatement(expression(tok));
            if (!LiteralToken.matchLiteral(tok = nextOrErrorMessage("Expected ; after expression statement"), ";")) {
                error("Expected ; after expression statement, not " + tok);
            }
            return result;
        }
    }

    private StatementNode returnStatement(Token tok) {
        if (LiteralToken.matchLiteral(peek(), ";")) {
            advance();
            return new ReturnStatement(null, tok.row, tok.column);
        }
        ReturnStatement result = new ReturnStatement(expression());
        if (!LiteralToken.matchLiteral(tok = nextOrErrorMessage("Expected ; after return statement"), ";")) {
            error("Expected ; after return statement, not " + tok);
        }
        return result;
    }

    private StatementNode forStatement(Token tok) {
        if (!LiteralToken.matchLiteral(tok = nextOrErrorMessage("Expect ( after for"), "(")) {
            error("Expected ( after for, not " + tok);
        }
        tok = nextOrErrorMessage("Expect expression or assignement after ( in for");
        boolean is3Part;
        StatementNode initializer;
        if (LiteralToken.matchLiteral(tok, ";")) {
            initializer = null;
            is3Part = true;
        } else {
            if (ReservedToken.matchReservedWord(tok, ReservedToken.DEF)) {
                initializer = variableDeclaration(true);
                tok = peek();
                if (LiteralToken.matchLiteral(tok, ";")) {
                    is3Part = true;
                } else if (LiteralToken.matchLiteral(tok, ":")) {
                    if (initializer instanceof FunctionDefinitionStatement) {
                        error("Variable in for iteration loop cannot be function");
                    }
                    if (((VariableDeclarationStatement)initializer).declarations.length > 1) {
                        error("Iteration for loop cannot have multiple variables");
                    }
                    is3Part = false;
                } else {
                    error("Expected ; or : after initializer in for loop");
                    return null; // UNREACHABLE
                }
            } else {
                initializer = new ExpressionStatement(expression(tok));
                tok = peek();
                if (LiteralToken.matchLiteral(tok, ";")) {
                    is3Part = true;
                } else if (LiteralToken.matchLiteral(tok, ":")) {
                    if (!(((ExpressionStatement)initializer).expression instanceof IdentifierExpression)) {
                        error("Variable in iteration for loop cannot be expression");
                    }
                    is3Part = false;
                } else {
                    error("Expected ; or : after initializer in for loop");
                    return null; // UNREACHABLE
                }
            }
            advance();
        }
        tok = nextOrError("for loop");
        if (is3Part) {
            ExpressionNode condition;
            if (LiteralToken.matchLiteral(tok, ";")) {
                condition = null;
                tok = nextOrError("for loop");
            } else {
                condition = expression(tok);
                tok = nextOrError("for loop");
                if (!LiteralToken.matchLiteral(tok, ";")) {
                    error("Expected ; after condition in for loop");
                }
                tok = nextOrError("for loop");
            }
            ExpressionNode increment;
            if (LiteralToken.matchLiteral(tok, ")")) {
                increment = null;
            } else {
                increment = expression(tok);
                tok = nextOrError("for loop");
                if (!LiteralToken.matchLiteral(tok, ")")) {
                    error("Expected ) after condition in for loop");
                }
            }
            StatementNode body;
            if (LiteralToken.matchLiteral(tok = nextOrErrorMessage("Expect body after for header"), "{")) {
                body = block(tok);
            } else {
                body = statement(tok);
                if (body instanceof VariableDeclarationStatement || body instanceof FunctionDefinitionStatement) {
                    error("Cannot have variable declaration or function definition in blockless for body");
                }
            }
            return new ThreePartForStatement(initializer, condition, increment, body, tok.row, tok.column);
        } else {
            ExpressionNode iterable;
            iterable = expression(tok);
            tok = nextOrError("for loop");
            if (!LiteralToken.matchLiteral(tok, ")")) {
                error("Expected ) after iterable in for loop");
            }
            StatementNode body;
            if (LiteralToken.matchLiteral(tok = nextOrErrorMessage("Expect body after for header"), "{")) {
                body = block(tok);
            } else {
                body = statement(tok);
                if (body instanceof VariableDeclarationStatement || body instanceof FunctionDefinitionStatement) {
                    error("Cannot have variable declaration or function definition in blockless for body");
                }
            }
            return new IterationForStatement(initializer, iterable, body, tok.row, tok.column);
        }
    }

    private IfOrWhileStatement ifOrWhileStatement(Token ifOrWhileToken, boolean isWhile) {
        Token tok;
        if (!LiteralToken.matchLiteral(tok = nextOrErrorMessage("Expect ( after " + (isWhile ? "while" : "if")), "(")) {
            error("Expected ( after " + (isWhile ? "while" : "if") + ", not " + tok);
        }
        ExpressionNode condition = expression();
        if (!LiteralToken.matchLiteral(tok = nextOrErrorMessage("Expect ) after condition in " + (isWhile ? "while" : "if")), ")")) {
            error("Expected ) after condition in " + (isWhile ? "while" : "if") + ", not " + tok);
        }
        StatementNode body;
        if (LiteralToken.matchLiteral(tok = nextOrErrorMessage("Expect body after condition in " + (isWhile ? "while" : "if")), "{")) {
            body = block(tok);
        } else {
            body = statement(tok);
            if (body instanceof VariableDeclarationStatement || body instanceof FunctionDefinitionStatement) {
                error("Cannot have variable declaration or function definition in blockless " + (isWhile ? "while" : "if") + " body");
            }
        }
        StatementNode elseBody;
        if (!isWhile && ReservedToken.matchReservedWord(peek(), "else")) {
            advance();
            if (LiteralToken.matchLiteral(tok = nextOrErrorMessage("Expect body in else block"), "{")) {
                elseBody = block(tok);
            } else {
                elseBody = statement(tok);
                if (elseBody instanceof VariableDeclarationStatement || elseBody instanceof FunctionDefinitionStatement) {
                    error("Cannot have variable declaration or function definition in blockless else body");
                }
            }
        } else {
            elseBody = null;
        }
        return new IfOrWhileStatement(condition, body, elseBody, isWhile, ifOrWhileToken.row, ifOrWhileToken.column);
    }

    private StatementList block(Token tok) {
        StatementList result = new StatementList(tok.row, tok.column);
        while (true) {
            tok = nextOrErrorMessage("Expected statement or } in block");
            if (LiteralToken.matchLiteral(tok, "}")) {
                break;
            }
            result.children.add(statement(tok));
        }
        return result;
    }

    private ExpressionNode expression() {
        return expression(expectExpression());
    }

    private ExpressionNode expression(Token tok) {
        return assignment(tok);
    }

    private ExpressionNode assignment(Token tok) {
        ExpressionNode assignee = logicalOr(tok);
        if (assignee instanceof IdentifierExpression) {
            if (LiteralToken.matchLiteral(peek(), "=")) {
                advance();
                ExpressionNode value = assignment(expectExpression());
                return new AssignmentExpression(assignee, value, tok.row, tok.column);
            }
        }
        return assignee;
    }

    private ExpressionNode logicalOr(Token tok) {
        ExpressionNode left = logicalAnd(tok);
        while (LiteralToken.matchLiteral(peek(), "||")) {
            advance();
            ExpressionNode right = logicalAnd(expectExpression());
            left = new BinaryExpression(left, right, BinaryOperator.LOGICAL_OR, tok.row, tok.column);
        }
        return left;
    }

    private ExpressionNode logicalAnd(Token tok) {
        ExpressionNode left = bitwiseOr(tok);
        while (LiteralToken.matchLiteral(peek(), "&&")) {
            advance();
            ExpressionNode right = bitwiseOr(expectExpression());
            left = new BinaryExpression(left, right, BinaryOperator.LOGICAL_AND, tok.row, tok.column);
        }
        return left;
    }

    private ExpressionNode bitwiseOr(Token tok) {
        ExpressionNode left = bitwiseXor(tok);
        while (LiteralToken.matchLiteral(peek(), "|")) {
            advance();
            ExpressionNode right = bitwiseXor(expectExpression());
            left = new BinaryExpression(left, right, BinaryOperator.BITWISE_OR, tok.row, tok.column);
        }
        return left;
    }

    private ExpressionNode bitwiseXor(Token tok) {
        ExpressionNode left = bitwiseAnd(tok);
        while (LiteralToken.matchLiteral(peek(), "^")) {
            advance();
            ExpressionNode right = bitwiseAnd(expectExpression());
            left = new BinaryExpression(left, right, BinaryOperator.BITWISE_XOR, tok.row, tok.column);
        }
        return left;
    }

    private ExpressionNode bitwiseAnd(Token tok) {
        ExpressionNode left = equality(tok);
        while (LiteralToken.matchLiteral(peek(), "&")) {
            advance();
            ExpressionNode right = equality(expectExpression());
            left = new BinaryExpression(left, right, BinaryOperator.BITWISE_AND, tok.row, tok.column);
        }
        return left;
    }

    private ExpressionNode equality(Token tok) {
        ExpressionNode left = relational(tok);
        Token rightToken = peek();
        while (rightToken instanceof LiteralToken) {
            BinaryOperator op;
            switch (((LiteralToken)rightToken).literal) {
                case "==":
                    op = BinaryOperator.EQUALS;
                    break;
                case "!=":
                    op = BinaryOperator.NOT_EQUALS;
                    break;
                case "===":
                    op = BinaryOperator.IDENTITY_EQUALS;
                    break;
                case "!==":
                    op = BinaryOperator.IDENTITY_NOT_EQUALS;
                    break;
                default:
                    return left;
            }
            advance();
            ExpressionNode right = relational(expectExpression());
            left = new BinaryExpression(left, right, op, tok.row, tok.column);
            rightToken = peek();
        }
        return left;
    }

    private ExpressionNode relational(Token tok) {
        ExpressionNode left = nullCoalesce(tok);
        Token rightToken = peek();
        while (rightToken instanceof LiteralToken) {
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
            ExpressionNode right = nullCoalesce(expectExpression());
            left = new BinaryExpression(left, right, op, tok.row, tok.column);
            rightToken = peek();
        }
        return left;
    }

    private ExpressionNode nullCoalesce(Token tok) {
        ExpressionNode left = bitShift(tok);
        while (LiteralToken.matchLiteral(peek(), "??")) {
            advance();
            ExpressionNode right = bitShift(expectExpression());
            left = new BinaryExpression(left, right, BinaryOperator.NULL_COALESCE, tok.row, tok.column);
        }
        return left;
    }

    private ExpressionNode bitShift(Token tok) {
        ExpressionNode left = addition(tok);
        Token rightToken = peek();
        while (rightToken instanceof LiteralToken) {
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
            ExpressionNode right = addition(expectExpression());
            left = new BinaryExpression(left, right, op, tok.row, tok.column);
            rightToken = peek();
        }
        return left;
    }

    private ExpressionNode addition(Token tok) {
        ExpressionNode left = multiplication(tok);
        Token rightToken = peek();
        while (rightToken instanceof LiteralToken) {
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
            ExpressionNode right = multiplication(expectExpression());
            left = new BinaryExpression(left, right, op, tok.row, tok.column);
            rightToken = peek();
        }
        return left;
    }

    private ExpressionNode multiplication(Token tok) {
        ExpressionNode left = unary(tok);
        Token rightToken = peek();
        while (rightToken instanceof LiteralToken) {
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
            ExpressionNode right = unary(expectExpression());
            left = new BinaryExpression(left, right, op, tok.row, tok.column);
            rightToken = peek();
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
                return new UnaryExpression(unary(expectExpression()), op, tok.row, tok.column);
            }
        }
        return call(tok);
    }

    private ExpressionNode call(Token tok) {
        ExpressionNode target = primary(tok);
        while (true) {
            Token rightToken = peek();
            if (!(rightToken instanceof LiteralToken)) {
                break;
            }
            switch (((LiteralToken)rightToken).literal) {
                case "(": {
                    advance();
                    List<ExpressionNode> args = new ArrayList<>();
                    tok = nextOrErrorMessage("Expected ) or argument in argument list");
                    if (!LiteralToken.matchLiteral(tok, ")")) {
                        while (true) {
                            args.add(expression(tok));
                            tok = nextOrErrorMessage("Expected ) or , in argument list");
                            if (LiteralToken.matchLiteral(tok, ")")) {
                                break;
                            } else if (LiteralToken.matchLiteral(tok, ",")) {
                                tok = nextOrErrorMessage("Expected next argument after , in argument list");
                                continue;
                            } else {
                                error("Expected ) or , in argument list, not " + tok);
                            }
                        }
                    }
                    target = new CallExpression(target, args.toArray(new ExpressionNode[0]), target.row, target.column);
                    continue;
                }
                case ".": {
                    advance();
                    tok = nextOrErrorMessage("Expected identifier");
                    if (!(tok instanceof IdentifierToken)) {
                        error("Expected identifier, not " + tok);
                    }
                    IdentifierToken identifierToken = (IdentifierToken)tok;
                    target = new AccessExpression(target, identifierToken.identifier, false, target.row, target.column);
                    continue;
                }
                case "?.": {
                    advance();
                    tok = nextOrErrorMessage("Expected identifier");
                    if (!(tok instanceof IdentifierToken)) {
                        error("Expected identifier, not " + tok);
                    }
                    IdentifierToken identifierToken = (IdentifierToken)tok;
                    target = new AccessExpression(target, identifierToken.identifier, true, target.row, target.column);
                    continue;
                }
            }
            break;
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
        } else if (ReservedToken.matchReservedWord(tok, ReservedToken.NULL)) {
            return new NullExpression(tok.row, tok.column);
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

    private StatementNode variableDeclaration(boolean isInFor) {
        Token tok;
        List<VariableDeclaration> declarations = new ArrayList<>();
        boolean canBeFunction = true;
        Set<Modifier> modifiers = EnumSet.noneOf(Modifier.class);
        while (true) {
            tok = peek();
            Modifier modifier;
            if (
                !(tok instanceof IdentifierToken) ||
                (modifier = Modifier.fromName(((IdentifierToken)tok).identifier)) == null
            ) {
                break;
            }
            if (!modifiers.add(modifier)) {
                throw new SyntaxException("Duplicate modifier " + modifier, tok.row, tok.column);
            }
            advance();
        }
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
            boolean nullable = false;
            if (LiteralToken.matchLiteral(tok, "?")) {
                nullable = true;
                tok = nextOrError(VARIABLE_DECLARATION);
            }
            if (!(tok instanceof IdentifierToken)) {
                error("Expected variable name in variable declaration, not " + tok);
            }
            String name = ((IdentifierToken)tok).identifier;
            ExpressionNode value;
            tok = nextOrError(VARIABLE_DECLARATION);
            if (canBeFunction && LiteralToken.matchLiteral(tok, "(")) {
                // Is a function declaration
                while (!LiteralToken.matchLiteral(tok = nextOrError(FUNCTION_DEFINITION), ")")) {
                    String argType;
                    if (tok instanceof IdentifierToken) {
                        argType = ((IdentifierToken)tok).identifier;
                    } else if (ReservedToken.matchReservedWord(tok, ReservedToken.VAR)) {
                        argType = null;
                    } else {
                        error("Expected type name or var in function definition, not " + tok);
                        argType = null; // UNREACHABLE
                    }
                    tok = nextOrError(VARIABLE_DECLARATION);
                    boolean nullableArg = false;
                    if (LiteralToken.matchLiteral(tok, "?")) {
                        nullableArg = true;
                        tok = nextOrError(VARIABLE_DECLARATION);
                    }
                    if (!(tok instanceof IdentifierToken)) {
                        error("Expected variable name in variable declaration, not " + tok);
                    }
                    String argName = ((IdentifierToken)tok).identifier;
                    ExpressionNode defaultValue;
                    tok = nextOrError(VARIABLE_DECLARATION);
                    if (LiteralToken.matchLiteral(tok, "=")) {
                        defaultValue = expression();
                        tok = nextOrErrorMessage("Expected ) or , after variable declaration");
                    } else {
                        defaultValue = null;
                    }
                    if (argType != null || defaultValue != null) { // Otherwise we error a few lines down
                        declarations.add(new VariableDeclaration(
                            argType != null ? new TypeReference(argType, nullableArg) : null,
                            argName,
                            defaultValue
                        ));
                    }
                    if (LiteralToken.matchLiteral(tok, ",")) {
                        if (argType == null && defaultValue == null) {
                            error(INFERRED_TYPE_MISSING_ASSIGNMENT);
                        }
                        continue;
                    } else if (LiteralToken.matchLiteral(tok, ")")) {
                        if (argType == null && defaultValue == null) {
                            error(INFERRED_TYPE_MISSING_ASSIGNMENT);
                        }
                        break;
                    } else {
                        error("Expected ) or , after variable declaration, not " + tok);
                    }
                    declarations.add(new VariableDeclaration(
                        argType != null ? new TypeReference(argType, nullableArg) : null,
                        argName
                    )); // reuse existing list
                }
                if (!LiteralToken.matchLiteral(tok = nextOrErrorMessage("Expect { after function header"), "{")) {
                    error("Expected { after function header, not " + tok);
                }
                StatementList body = block(tok);
                return new FunctionDefinitionStatement(
                    modifiers,
                    type != null ? new TypeReference(type, nullable) : null,
                    name,
                    declarations.toArray(new VariableDeclaration[declarations.size()]),
                    body,
                    tok.row,
                    tok.column
                );
            }
            if (LiteralToken.matchLiteral(tok, "=")) {
                value = expression();
                tok = nextOrErrorMessage("Expected ; or , after variable declaration");
            } else {
                value = null;
            }
            if (isInFor || type != null || value != null) { // Otherwise we error a few lines down
                declarations.add(new VariableDeclaration(
                    type != null ? new TypeReference(type, nullable) : null,
                    name,
                    value
                ));
            }
            if (LiteralToken.matchLiteral(tok, ",")) {
                if (type == null && value == null) {
                    error(INFERRED_TYPE_MISSING_ASSIGNMENT);
                }
                canBeFunction = false;
                continue;
            } else if (LiteralToken.matchLiteral(tok, ";") || (isInFor && LiteralToken.matchLiteral(tok, ":"))) {
                if (!isInFor && type == null && value == null) {
                    error(INFERRED_TYPE_MISSING_ASSIGNMENT);
                }
                if (isInFor) i--;
                break;
            } else {
                error("Expected ; or , after variable declaration, not " + tok);
            }
        }
        return new VariableDeclarationStatement(
            declarations.toArray(new VariableDeclaration[declarations.size()]),
            modifiers,
            tok.row, tok.column
        );
    }

    // Utility methods
    @SuppressWarnings("unused")
    private void unexpectedToken(Token tok) {
        unexpectedToken(tok, null);
    }

    private void unexpectedToken(Token tok, String where) {
        error("Unexpected token " + (where == null ? "" : (where + " ")) + tok);
    }

    @SuppressWarnings("unused")
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

    @SuppressWarnings("unused")
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

    private Token expectExpression() {
        Token c = next();
        if (c == null) {
            error(EXPECT_EXPRESSION);
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

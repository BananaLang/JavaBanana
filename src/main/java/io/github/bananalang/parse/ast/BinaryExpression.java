package io.github.bananalang.parse.ast;

public final class BinaryExpression extends ExpressionNode {
    public static enum BinaryOperator {
        LOGICAL_OR("||"),
        LOGICAL_AND("&&"),
        BITWISE_OR("|"),
        BITWISE_XOR("^"),
        BITWISE_AND("&"),
        EQUALS("=="), NOT_EQUALS("!="), IDENTITY_EQUALS("==="), IDENTITY_NOT_EQUALS("!=="),
        LESS_THAN("<"), GREATER_THAN(">"), LESS_THAN_EQUALS("<="), GREATER_THAN_EQUALS(">="),
        NULL_COALESCE("??"),
        LEFT_SHIFT("<<"), RIGHT_SHIFT(">>"),
        ADD("+"), SUBTRACT("-"),
        MULTIPLY("*"), DIVIDE("/"), MODULUS("%");

        private final String op;

        private BinaryOperator(String op) {
            this.op = op;
        }

        public String toString() {
            return op;
        }
    }

    public final ExpressionNode left, right;
    public final BinaryOperator type;

    public BinaryExpression(ExpressionNode left, ExpressionNode right, BinaryOperator type, int row, int column) {
        super(row, column);
        this.left = left;
        this.right = right;
        this.type = type;
    }

    public BinaryExpression(ExpressionNode left, ExpressionNode right, BinaryOperator type) {
        this(left, right, type, 0, 0);
    }

    @Override
    protected void dump(StringBuilder output, int currentIndent, int indent) {
        output.append("BinaryExpression{\n")
              .append(getIndent(currentIndent + indent))
              .append("left=");
        left.dump(output, currentIndent + indent, indent);
        output.append(",\n")
              .append(getIndent(currentIndent + indent))
              .append("right=");
        right.dump(output, currentIndent + indent, indent);
        output.append(",\n")
              .append(getIndent(currentIndent + indent))
              .append("type=")
              .append(type)
              .append('\n')
              .append(getIndent(currentIndent))
              .append('}');
    }

    @Override
    public String toString() {
        return "(" + left + " " + type + " " + right + ')';
    }
}

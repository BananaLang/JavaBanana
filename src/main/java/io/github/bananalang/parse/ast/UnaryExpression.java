package io.github.bananalang.parse.ast;

public final class UnaryExpression extends ExpressionNode {
    public static enum UnaryOperator {
        ASSERT_NONNULL("", "!!"),
        POST_INCREMENT("", "++"), POST_DECREMENT("", "--"),
        PRE_INCREMENT("++", ""), PRE_DECREMENT("--", ""),
        PLUS("+", ""), NEGATE("-", ""), NOT("!", ""), BITWISE_INVERT("~", "");

        private final String pre, post;

        private UnaryOperator(String pre, String post) {
            this.pre = pre;
            this.post = post;
        }

        public String getPre() {
            return pre;
        }

        public String getPost() {
            return post;
        }
    }

    public final ExpressionNode value;
    public final UnaryOperator type;

    public UnaryExpression(ExpressionNode value, UnaryOperator type, int row, int column) {
        super(row, column);
        this.value = value;
        this.type = type;
    }

    public UnaryExpression(ExpressionNode value, UnaryOperator type) {
        this(value, type, 0, 0);
    }

    @Override
    protected void dump(StringBuilder output, int currentIndent, int indent) {
        output.append("UnaryExpression{\n")
              .append(getIndent(currentIndent + indent))
              .append("value=");
        value.dump(output, currentIndent + indent, indent);
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
        if (type == UnaryOperator.ASSERT_NONNULL) {
            return "" + value + "!!";
        }
        return "(" + type.getPre() + value + type.getPost() + ')';
    }
}

package io.github.bananalang.parse.ast;

public final class BooleanExpression extends ExpressionNode {
    public final boolean value;

    public BooleanExpression(boolean value, int row, int column) {
        super(row, column);
        this.value = value;
    }

    public BooleanExpression(boolean value) {
        this(value, 0, 0);
    }

    @Override
    protected void dump(StringBuilder output, int currentIndent, int indent) {
        output.append("BooleanExpression{value=")
              .append(value)
              .append('}');
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }
}

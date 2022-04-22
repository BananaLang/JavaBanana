package io.github.bananalang.parse.ast;

public final class DecimalExpression extends ExpressionNode {
    public final double value;

    public DecimalExpression(double value, int row, int column) {
        super(row, column);
        this.value = value;
    }

    public DecimalExpression(double value) {
        this(value, 0, 0);
    }

    @Override
    protected void dump(StringBuilder output, int currentIndent, int indent) {
        output.append("DecimalExpression{value=")
              .append(value)
              .append('}');
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }
}

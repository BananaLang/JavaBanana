package io.github.bananalang.parse.ast;

public final class StringExpression extends ExpressionNode {
    public final String value;

    public StringExpression(String value, int row, int column) {
        super(row, column);
        this.value = value;
    }

    public StringExpression(String value) {
        this(value, 0, 0);
    }

    @Override
    protected void dump(StringBuilder output, int currentIndent, int indent) {
        output.append("StringExpression{value=\"")
              .append(value)
              .append("\"}");
    }

    @Override
    public String toString() {
        return string()
               .add("value", value)
               .toString();
    }
}

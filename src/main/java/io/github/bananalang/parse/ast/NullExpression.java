package io.github.bananalang.parse.ast;

public class NullExpression extends ExpressionNode {
    public NullExpression(int row, int column) {
        super(row, column);
    }

    public NullExpression() {
        this(0, 0);
    }

    @Override
    protected void dump(StringBuilder output, int currentIndent, int indent) {
        output.append("NullExpression{}");
    }

    @Override
    public String toString() {
        return "null";
    }
}

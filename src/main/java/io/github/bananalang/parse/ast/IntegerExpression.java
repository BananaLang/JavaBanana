package io.github.bananalang.parse.ast;

import java.math.BigInteger;

public final class IntegerExpression extends ExpressionNode {
    public final BigInteger value;

    public IntegerExpression(BigInteger value, int row, int column) {
        super(row, column);
        this.value = value;
    }

    public IntegerExpression(BigInteger value) {
        this(value, 0, 0);
    }

    @Override
    protected void dump(StringBuilder output, int currentIndent, int indent) {
        output.append("IntegerExpression{value=")
              .append(value)
              .append('}');
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

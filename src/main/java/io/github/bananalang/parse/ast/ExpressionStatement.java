package io.github.bananalang.parse.ast;

public final class ExpressionStatement extends StatementNode {
    public final ExpressionNode expression;

    public ExpressionStatement(ExpressionNode expression, int row, int column) {
        super(row, column);
        this.expression = expression;
    }

    public ExpressionStatement(ExpressionNode expression) {
        this(expression, expression.row, expression.column);
    }

    @Override
    protected void dump(StringBuilder output, int currentIndent, int indent) {
        output.append("ExpressionStatement{\n")
              .append(getIndent(currentIndent + indent))
              .append("expression=");
        expression.dump(output, currentIndent + indent, indent);
        output.append('\n')
              .append(getIndent(currentIndent))
              .append('}');
    }

    @Override
    public String toString() {
        return expression.toString() + ';';
    }
}

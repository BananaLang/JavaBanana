package io.github.bananalang.parse.ast;

public final class ReturnStatement extends StatementNode {
    public final ExpressionNode value;

    public ReturnStatement(ExpressionNode value, int row, int column) {
        super(row, column);
        this.value = value;
    }

    public ReturnStatement(ExpressionNode expression) {
        this(expression, expression.row, expression.column);
    }

    @Override
    protected void dump(StringBuilder output, int currentIndent, int indent) {
        output.append("ReturnStatement{");
        if (value != null) {
            output.append('\n')
                  .append(getIndent(currentIndent + indent))
                  .append("value=");
            value.dump(output, currentIndent + indent, indent);
            output.append('\n')
                  .append(getIndent(currentIndent));
        }
        output.append('}');
    }

    @Override
    public String toString() {
        if (value == null) {
            return "return;";
        }
        String result = value.toString();
        if (result.startsWith("(") && result.endsWith(")")) {
            result = result.substring(1, result.length() - 1);
        }
        return "return " + result + ';';
    }
}

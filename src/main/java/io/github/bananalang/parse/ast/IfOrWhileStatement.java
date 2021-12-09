package io.github.bananalang.parse.ast;

public final class IfOrWhileStatement extends StatementNode {
    public final ExpressionNode condition;
    public final StatementNode body;
    public final boolean isWhile;

    public IfOrWhileStatement(ExpressionNode condition, StatementNode body, boolean isWhile, int row, int column) {
        super(row, column);
        this.condition = condition;
        this.body = body;
        this.isWhile = isWhile;
    }

    public IfOrWhileStatement(ExpressionNode condition, StatementNode body, boolean isWhile) {
        this(condition, body, isWhile, 0, 0);
    }

    @Override
    protected void dump(StringBuilder output, int currentIndent, int indent) {
        output.append("IfOrWhileStatement{\n")
              .append(getIndent(currentIndent + indent))
              .append("isWhile=")
              .append(isWhile)
              .append(",\n")
              .append(getIndent(currentIndent + indent))
              .append("condition=");
        condition.dump(output, currentIndent + indent, indent);
        output.append(",\n")
              .append(getIndent(currentIndent + indent))
              .append("body=");
        body.dump(output, currentIndent + indent, indent);
        output.append('\n')
              .append(getIndent(currentIndent))
              .append('}');
    }

    @Override
    public String toString() {
        return string()
               .add("condition", condition)
               .add("body", body)
               .add("isWhile", isWhile)
               .toString();
    }
}

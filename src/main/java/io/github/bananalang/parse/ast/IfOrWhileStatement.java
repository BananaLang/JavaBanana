package io.github.bananalang.parse.ast;

public final class IfOrWhileStatement extends StatementNode {
    public final ExpressionNode condition;
    public final StatementNode body;
    public final StatementNode elseBody;
    public final boolean isWhile;

    public IfOrWhileStatement(
        ExpressionNode condition,
        StatementNode body,
        StatementNode elseBody,
        boolean isWhile,
        int row, int column
    ) {
        super(row, column);
        this.condition = condition;
        this.body = body;
        this.elseBody = elseBody;
        this.isWhile = isWhile;
    }

    public IfOrWhileStatement(
        ExpressionNode condition,
        StatementNode body,
        StatementNode elseBody,
        boolean isWhile
    ) {
        this(condition, body, elseBody, isWhile, 0, 0);
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
        if (elseBody != null) {
            output.append(",\n")
                  .append(getIndent(currentIndent + indent))
                  .append("elseBody=");
            elseBody.dump(output, currentIndent + indent, indent);
        }
        output.append('\n')
              .append(getIndent(currentIndent))
              .append('}');
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(isWhile ? "while (" : "if (")
            .append(condition)
            .append(") {\n")
            .append(body)
            .append("\n}");
        if (elseBody != null) {
            if (elseBody instanceof IfOrWhileStatement && !((IfOrWhileStatement)elseBody).isWhile) {
                result.append(" else ")
                    .append(elseBody);
            } else {
                result.append(" else {\n")
                    .append(elseBody)
                    .append("\n}");
            }
        }
        return result.toString();
    }
}

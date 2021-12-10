package io.github.bananalang.parse.ast;

public final class IterationForStatement extends StatementNode {
    public final StatementNode variable;
    public final ExpressionNode iterable;
    public final StatementNode body;

    public IterationForStatement(StatementNode variable, ExpressionNode iterable, StatementNode body, int row, int column) {
        super(row, column);
        this.variable = variable;
        this.iterable = iterable;
        this.body = body;
    }

    public IterationForStatement(StatementNode variable, ExpressionNode iterable, StatementNode body) {
        this(variable, iterable, body, 0, 0);
    }

    @Override
    protected void dump(StringBuilder output, int currentIndent, int indent) {
        output.append("IterationForStatement{\n")
              .append(getIndent(currentIndent + indent))
              .append("variable=");
        variable.dump(output, currentIndent + indent, indent);
        output.append(",\n")
              .append(getIndent(currentIndent + indent))
              .append("iterable=");
        iterable.dump(output, currentIndent + indent, indent);
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
               .add("variable", variable)
               .add("iterable", iterable)
               .add("body", body)
               .toString();
    }
}

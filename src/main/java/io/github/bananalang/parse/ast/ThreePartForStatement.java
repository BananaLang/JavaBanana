package io.github.bananalang.parse.ast;

public final class ThreePartForStatement extends StatementNode {
    public final StatementNode initializer;
    public final ExpressionNode condition, increment;
    public final StatementNode body;

    public ThreePartForStatement(StatementNode initializer, ExpressionNode condition, ExpressionNode increment, StatementNode body, int row, int column) {
        super(row, column);
        this.initializer = initializer;
        this.condition = condition;
        this.increment = increment;
        this.body = body;
    }

    public ThreePartForStatement(StatementNode initializer, ExpressionNode condition, ExpressionNode increment, StatementNode body) {
        this(initializer, condition, increment, body, 0, 0);
    }

    @Override
    protected void dump(StringBuilder output, int currentIndent, int indent) {
        output.append("ThreePartForStatement{\n")
              .append(getIndent(currentIndent + indent))
              .append("initializer=");
        if (initializer == null) {
            output.append("empty");
        } else {
            initializer.dump(output, currentIndent + indent, indent);
        }
        output.append(",\n")
              .append(getIndent(currentIndent + indent))
              .append("condition=");
        if (condition == null) {
            output.append("empty");
        } else {
            condition.dump(output, currentIndent + indent, indent);
        }
        output.append(",\n")
              .append(getIndent(currentIndent + indent))
              .append("increment=");
        if (increment == null) {
            output.append("empty");
        } else {
            increment.dump(output, currentIndent + indent, indent);
        }
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
               .add("initializer", initializer)
               .add("condition", condition)
               .add("increment", increment)
               .add("body", body)
               .toString();
    }
}

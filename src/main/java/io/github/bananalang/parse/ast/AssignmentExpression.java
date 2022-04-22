package io.github.bananalang.parse.ast;

public final class AssignmentExpression extends ExpressionNode {
    public final ExpressionNode target, value;

    public AssignmentExpression(ExpressionNode target, ExpressionNode value, int row, int column) {
        super(row, column);
        this.target = target;
        this.value = value;
    }

    public AssignmentExpression(ExpressionNode target, ExpressionNode value) {
        this(target, value, 0, 0);
    }

    @Override
    protected void dump(StringBuilder output, int currentIndent, int indent) {
        output.append("AssignmentExpression{\n")
              .append(getIndent(currentIndent + indent))
              .append("target=");
        target.dump(output, currentIndent + indent, indent);
        output.append(",\n")
              .append(getIndent(currentIndent + indent))
              .append("value=");
        value.dump(output, currentIndent + indent, indent);
        output.append('\n')
              .append(getIndent(currentIndent))
              .append('}');
    }

    @Override
    public String toString() {
        return "(" + target + " = " + value + ')';
    }
}

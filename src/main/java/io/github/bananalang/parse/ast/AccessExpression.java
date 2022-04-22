package io.github.bananalang.parse.ast;

public final class AccessExpression extends ExpressionNode {
    public final ExpressionNode target;
    public final String name;

    public AccessExpression(ExpressionNode target, String name, int row, int column) {
        super(row, column);
        this.target = target;
        this.name = name;
    }

    public AccessExpression(ExpressionNode target, String name) {
        this(target, name, 0, 0);
    }

    @Override
    protected void dump(StringBuilder output, int currentIndent, int indent) {
        output.append("AccessExpression{\n")
              .append(getIndent(currentIndent + indent))
              .append("target=");
        target.dump(output, currentIndent + indent, indent);
        output.append(",\n")
              .append(getIndent(currentIndent + indent))
              .append("name=")
              .append(name)
              .append('\n')
              .append(getIndent(currentIndent))
              .append('}');
    }

    @Override
    public String toString() {
        return "(" + target + '.' + name + ')';
    }
}

package io.github.bananalang.parse.ast;

import io.github.bananalang.parse.ast.VariableDeclarationStatement.TypeReference;

public final class CastExpression extends ExpressionNode {
    public final ExpressionNode target;
    public final TypeReference type;

    public CastExpression(ExpressionNode target, TypeReference type, int row, int column) {
        super(row, column);
        this.target = target;
        this.type = type;
    }

    public CastExpression(ExpressionNode target, TypeReference type) {
        this(target, type, 0, 0);
    }

    @Override
    protected void dump(StringBuilder output, int currentIndent, int indent) {
        output.append("UnaryExpression{\n")
              .append(getIndent(currentIndent + indent))
              .append("target=");
        target.dump(output, currentIndent + indent, indent);
        output.append(",\n")
              .append(getIndent(currentIndent + indent))
              .append("type=")
              .append(type)
              .append('\n')
              .append(getIndent(currentIndent))
              .append('}');
    }

    @Override
    public String toString() {
        return "(" + type + ')' + target;
    }
}

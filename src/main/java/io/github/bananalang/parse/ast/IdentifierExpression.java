package io.github.bananalang.parse.ast;

public final class IdentifierExpression extends ExpressionNode {
    public final String identifier;

    public IdentifierExpression(String identifier, int row, int column) {
        super(row, column);
        this.identifier = identifier;
    }

    public IdentifierExpression(String identifier) {
        this(identifier, 0, 0);
    }

    @Override
    protected void dump(StringBuilder output, int currentIndent, int indent) {
        output.append("IdentifierExpression{identifier=")
              .append(identifier)
              .append('}');
    }

    @Override
    public String toString() {
        return string()
               .add("identifier", identifier)
               .toString();
    }
}

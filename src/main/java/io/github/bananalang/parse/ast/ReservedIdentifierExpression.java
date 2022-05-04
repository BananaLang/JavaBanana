package io.github.bananalang.parse.ast;

public class ReservedIdentifierExpression extends ExpressionNode {
    public static enum ReservedIdentifier {
        NULL("null"), THIS("this");

        public final String literal;

        private ReservedIdentifier(String literal) {
            this.literal = literal;
        }

        @Override
        public String toString() {
            return literal;
        }
    }

    public final ReservedIdentifier identifier;

    public ReservedIdentifierExpression(ReservedIdentifier identifier, int row, int column) {
        super(row, column);
        this.identifier = identifier;
    }

    public ReservedIdentifierExpression(ReservedIdentifier identifier) {
        this(identifier, 0, 0);
    }

    @Override
    protected void dump(StringBuilder output, int currentIndent, int indent) {
        output.append("ReservedIdentifierExpression{identifier=").append(this.identifier).append('}');
    }

    @Override
    public String toString() {
        return identifier.literal;
    }
}

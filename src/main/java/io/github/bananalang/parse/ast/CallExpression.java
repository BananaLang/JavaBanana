package io.github.bananalang.parse.ast;

public final class CallExpression extends ExpressionNode {
    public final ExpressionNode target;
    public final ExpressionNode[] args;

    public CallExpression(ExpressionNode target, ExpressionNode[] args, int row, int column) {
        super(row, column);
        this.target = target;
        this.args = args;
    }

    public CallExpression(ExpressionNode target, ExpressionNode[] args) {
        this(target, args, 0, 0);
    }

    @Override
    protected void dump(StringBuilder output, int currentIndent, int indent) {
        output.append("CallExpression{\n")
              .append(getIndent(currentIndent + indent))
              .append("target=");
        target.dump(output, currentIndent + indent, indent);
        output.append(",\n")
              .append(getIndent(currentIndent + indent))
              .append("args=[");
        if (args.length > 0) {
            output.append('\n')
                  .append(getIndent(currentIndent + 2 * indent));
            for (int i = 0; i < args.length; i++) {
                if (i > 0) {
                    output.append(',')
                        .append('\n')
                        .append(getIndent(currentIndent + 2 * indent));
                }
                args[i].dump(output, currentIndent + 2 * indent, indent);
            }
            output.append('\n')
                  .append(getIndent(currentIndent + indent));
        }
        output.append("]\n")
              .append(getIndent(currentIndent))
              .append('}');
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder()
            .append(target)
            .append('(');
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                result.append(", ");
            }
            result.append(args[i]);
        }
        return result.append(')').toString();
    }
}

package io.github.bananalang.parse.ast;

import io.github.bananalang.parse.ast.VariableDeclarationStatement.VariableDeclaration;

public final class LambdaExpression extends ExpressionNode {
    public final VariableDeclaration[] args;
    public final StatementList body;

    public LambdaExpression(VariableDeclaration[] args, StatementList body, int row, int column) {
        super(row, column);
        this.args = args;
        this.body = body;
    }

    public LambdaExpression(VariableDeclaration[] args, StatementList body) {
        this(args, body, 0, 0);
    }

    @Override
    protected void dump(StringBuilder output, int currentIndent, int indent) {
        // output.append("LambdaExpression{value=\"")
        //       .append(value)
        //       .append("\"}");
        output.append("LambdaExpression{\n")
              .append(getIndent(currentIndent + indent))
              .append("args=[");
        if (args.length > 0) {
            output.append('\n');
            for (int i = 0; i < args.length; i++) {
                if (i > 0) output.append(",\n");
                output.append(getIndent(currentIndent + 2 * indent));
                args[i].dump(output, currentIndent + 2 * indent, indent);
            }
            output.append('\n')
                  .append(getIndent(currentIndent + indent));
        }
        output.append("],\n")
              .append(getIndent(currentIndent + indent))
              .append("body=");
        body.dump(output, currentIndent + indent, indent);
        output.append('\n')
              .append(getIndent(currentIndent))
              .append('}');
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("{");
        if (args.length > 0) {
            result.append(' ');
            for (int i = 0; i < args.length; i++) {
                if (i > 0) {
                    result.append(", ");
                }
                result.append(args[i].name);
                if (args[i].type != null) {
                    result.append(": ").append(args[i].type);
                }
            }
            result.append(" ->");
        }
        if (body.children.size() == 0) {
            result.append(" }");
        } else if (body.children.size() == 1) {
            result.append(' ');
            if (body.children.get(0) instanceof ExpressionStatement) {
                result.append(((ExpressionStatement)body.children.get(0)).expression);
            } else {
                result.append(body.children.get(0));
            }
            result.append(" }");
        } else {
            for (StatementNode stmt : body.children) {
                result.append('\n').append(stmt);
            }
            result.append("\n}");
        }
        return result.toString();
    }
}

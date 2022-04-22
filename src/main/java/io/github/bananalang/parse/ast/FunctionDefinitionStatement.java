package io.github.bananalang.parse.ast;

import io.github.bananalang.parse.ast.VariableDeclarationStatement.VariableDeclaration;

public final class FunctionDefinitionStatement extends StatementNode {
    public final String returnType, name;
    public final VariableDeclaration[] args;
    public final StatementList body;

    public FunctionDefinitionStatement(String returnType, String name, VariableDeclaration[] args, StatementList body, int row, int column) {
        super(row, column);
        this.returnType = returnType;
        this.name = name;
        this.args = args;
        this.body = body;
    }

    public FunctionDefinitionStatement(String returnType, String name, VariableDeclaration[] args, StatementList body) {
        this(returnType, name, args, body, 0, 0);
    }

    @Override
    protected void dump(StringBuilder output, int currentIndent, int indent) {
        output.append("FunctionDefinitionStatement{\n")
              .append(getIndent(currentIndent + indent))
              .append("returnType=");
        if (returnType == null) {
            output.append("var,\n");
        } else {
            output.append('"').append(returnType).append("\",\n");
        }
        output.append(getIndent(currentIndent + indent))
              .append("name=\"")
              .append(name)
              .append("\"\n")
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
        StringBuilder result = new StringBuilder("def ")
            .append(returnType == null ? "var" : returnType)
            .append(' ')
            .append(name)
            .append('(');
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                result.append(", ");
            }
            result.append(args[i]);
        }
        return result.append(") {")
            .append(body)
            .append('}')
            .toString();
    }
}

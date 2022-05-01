package io.github.bananalang.parse.ast;

import java.util.Iterator;
import java.util.Set;

import io.github.bananalang.parse.SyntaxException;
import io.github.bananalang.parse.ast.VariableDeclarationStatement.Modifier;
import io.github.bananalang.parse.ast.VariableDeclarationStatement.TypeReference;
import io.github.bananalang.parse.ast.VariableDeclarationStatement.VariableDeclaration;

public final class FunctionDefinitionStatement extends StatementNode {
    public final Set<Modifier> modifiers;
    public final TypeReference returnType;
    public final String name;
    public final VariableDeclaration[] args;
    public final StatementList body;

    public FunctionDefinitionStatement(
        Set<Modifier> modifiers,
        TypeReference returnType,
        String name,
        VariableDeclaration[] args,
        StatementList body,
        int row, int column
    ) {
        super(row, column);
        this.modifiers = modifiers;
        this.returnType = returnType;
        this.name = name;
        this.args = args;
        this.body = body;
        for (Modifier modifier : modifiers) {
            if (!modifier.allowedFunction) {
                throw new SyntaxException("Modifier " + modifier + " not allowed on function", row, column);
            }
        }
    }

    public FunctionDefinitionStatement(
        Set<Modifier> modifiers,
        TypeReference returnType,
        String name,
        VariableDeclaration[] args,
        StatementList body
    ) {
        this(modifiers, returnType, name, args, body, 0, 0);
    }

    @Override
    protected void dump(StringBuilder output, int currentIndent, int indent) {
        output.append("FunctionDefinitionStatement{\n")
              .append(getIndent(currentIndent + indent))
              .append("modifiers=[");
        boolean first = true;
        for (Iterator<Modifier> modifierIterator = modifiers.iterator(); modifierIterator.hasNext();) {
            if (first) {
                first = false;
                output.append(", ");
            }
            output.append(modifierIterator.next());
        }
        output.append("],\n")
              .append(getIndent(currentIndent + indent))
              .append("returnType=");
        if (returnType == null) {
            output.append("var,\n");
        } else {
            output.append(returnType).append(",\n");
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
        StringBuilder result = new StringBuilder("def ");
        for (Modifier modifier : modifiers) {
            result.append(modifier).append(' ');
        }
        result.append(returnType == null ? "var" : returnType)
            .append(' ')
            .append(name)
            .append('(');
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                result.append(", ");
            }
            result.append(args[i]);
        }
        return result.append(") {\n")
            .append(body)
            .append("\n}")
            .toString();
    }
}

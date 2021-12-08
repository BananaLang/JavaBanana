package io.github.bananalang.parse.ast;

import java.util.Objects;

import io.github.bananalang.util.ToStringBuilder;

public final class VariableDeclarationStatement extends StatementNode {
    public static final class VariableDeclaration {
        /** The declared type, as a string, or {@code null} to indicate type inference. */
        public final String type;
        /** The variable name. */
        public final String name;
        /** The value expression, or null if not assigned to. */
        public final ExpressionNode value;

        public VariableDeclaration(String type, String name) {
            this(type, name, null);
        }

        public VariableDeclaration(String name, ExpressionNode value) {
            this(null, name, value);
        }

        public VariableDeclaration(String type, String name, ExpressionNode value) {
            this.type = value != null ? type : Objects.requireNonNull(type, "type and value cannot both be null");
            this.name = Objects.requireNonNull(name);
            this.value = value;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                       .addIf(type != null, "type", type)
                       .addIf(type == null, "type", (Object)"var")
                       .add("name", name)
                       .add("value", value)
                       .toString();
        }

        public void dump(StringBuilder output, int currentIndent, int indent) {
            output.append("VariableDeclaration{\n")
                  .append(getIndent(currentIndent + indent))
                  .append("type=");
            if (type == null) {
                output.append("var,\n");
            } else {
                output.append('"').append(type).append("\",\n");
            }
            output.append(getIndent(currentIndent + indent))
                  .append("name=\"")
                  .append(name)
                  .append('"');
            if (value != null) {
                output.append(",\n")
                      .append(getIndent(currentIndent + indent))
                      .append("value=");
                value.dump(output, currentIndent + indent, indent);
            }
            output.append('\n')
                  .append(getIndent(currentIndent))
                  .append('}');
        }
    }

    public final VariableDeclaration[] declarations;

    public VariableDeclarationStatement(VariableDeclaration[] declarations, int row, int column) {
        super(row, column);
        this.declarations = declarations;
    }

    public VariableDeclarationStatement(VariableDeclaration[] declarations) {
        this(declarations, 0, 0);
    }

    @Override
    protected void dump(StringBuilder output, int currentIndent, int indent) {
        output.append("VariableDeclarationStatement{declarations=[");
        if (declarations.length > 0) {
            output.append('\n');
            for (int i = 0; i < declarations.length; i++) {
                if (i > 0) output.append(",\n");
                output.append(getIndent(currentIndent + indent));
                declarations[i].dump(output, currentIndent + indent, indent);
            }
            output.append('\n')
                  .append(getIndent(currentIndent));
        }
        output.append("]}");
    }

    @Override
    public String toString() {
        return string()
               .add("declarations", declarations)
               .toString();
    }
}

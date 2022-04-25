package io.github.bananalang.parse.ast;

import java.util.Objects;

public final class VariableDeclarationStatement extends StatementNode {
    public static final class TypeReference {
        public final String name;
        public final boolean nullable;

        public TypeReference(String name) {
            this(name, false);
        }

        public TypeReference(String name, boolean nullable) {
            this.name = name;
            this.nullable = nullable;
        }

        @Override
        public String toString() {
            if (nullable) {
                return name + '?';
            }
            return name;
        }

        public void dump(StringBuilder output, int currentIndent, int indent) {
            output.append("VariableDeclaration{\n")
                  .append(getIndent(currentIndent + indent))
                  .append("name=")
                  .append(name)
                  .append(",\n")
                  .append(getIndent(currentIndent + indent))
                  .append("nullable=")
                  .append(nullable)
                  .append('\n')
                  .append(getIndent(currentIndent))
                  .append('}');
        }
    }

    public static final class VariableDeclaration {
        /** The declared type, as a string, or {@code null} to indicate type inference. */
        public final TypeReference type;
        /** The variable name. */
        public final String name;
        /** The value expression, or null if not assigned to. */
        public final ExpressionNode value;

        public VariableDeclaration(TypeReference type, String name) {
            this(type, name, null);
        }

        public VariableDeclaration(String name, ExpressionNode value) {
            this(null, name, value);
        }

        public VariableDeclaration(TypeReference type, String name, ExpressionNode value) {
            this.type = type;
            this.name = Objects.requireNonNull(name);
            this.value = value;
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder(type == null ? "var" : type.toString())
                .append(' ')
                .append(name);
            if (value != null) {
                result.append(" = ");
                String valueStr = value.toString();
                if (valueStr.startsWith("(") && valueStr.endsWith(")")) {
                    result.append(valueStr, 1, valueStr.length() - 1);
                } else {
                    result.append(valueStr);
                }
            }
            return result.toString();
        }

        public void dump(StringBuilder output, int currentIndent, int indent) {
            output.append("VariableDeclaration{\n")
                  .append(getIndent(currentIndent + indent))
                  .append("type=");
            if (type == null) {
                output.append("var,\n");
            } else {
                output.append(type).append(",\n");
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
        StringBuilder result = new StringBuilder("def ");
        for (int i = 0; i < declarations.length; i++) {
            if (i > 0) {
                result.append(", ");
            }
            result.append(declarations[i]);
        }
        return result.append(';').toString();
    }
}

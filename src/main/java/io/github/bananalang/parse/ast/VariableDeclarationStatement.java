package io.github.bananalang.parse.ast;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import io.github.bananalang.parse.SyntaxException;

public final class VariableDeclarationStatement extends StatementNode {
    public static enum Modifier {
        PUBLIC(true, true, "public"),
        GLOBAL(true, false, "global"),
        LAZY(true, false, "lazy"),
        EXTENSION(false, true, "extension");

        private static Map<String, Modifier> nameToModifier;

        public final boolean allowedVariable, allowedFunction;
        public final String name;

        private Modifier(boolean allowedVariable, boolean allowedFunction, String name) {
            this.allowedVariable = allowedVariable;
            this.allowedFunction = allowedFunction;
            this.name = name.intern();
            postInit();
        }

        private void postInit() {
            if (nameToModifier == null) {
                nameToModifier = new HashMap<>();
            }
            nameToModifier.put(name, this);
        }

        @Override
        public String toString() {
            return name;
        }

        public boolean isIncompatibleWith(Modifier other) {
            switch (this) {
                case PUBLIC: switch (other) {
                    case PUBLIC:
                    case GLOBAL:
                    case LAZY:
                        return true;
                    case EXTENSION:
                        return false;
                }
                case GLOBAL: switch (other) {
                    case PUBLIC:
                    case GLOBAL:
                    case LAZY:
                    case EXTENSION:
                        return true;
                }
                case LAZY: switch (other) {
                    case PUBLIC:
                    case GLOBAL:
                    case LAZY:
                    case EXTENSION:
                        return true;
                }
                case EXTENSION: switch (other) {
                    case GLOBAL:
                    case LAZY:
                    case EXTENSION:
                        return true;
                    case PUBLIC:
                        return false;
                }
            }
            throw new AssertionError();
        }

        public Modifier incompatibleWith(Set<Modifier> others) {
            for (Modifier other : others) {
                if (isIncompatibleWith(other))  {
                    return other;
                }
            }
            return null;
        }

        public static Modifier fromName(String name) {
            return nameToModifier.get(name);
        }
    }

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
    public final Set<Modifier> modifiers;

    public VariableDeclarationStatement(
        VariableDeclaration[] declarations,
        Set<Modifier> modifiers,
        int row, int column
    ) {
        super(row, column);
        this.declarations = declarations;
        this.modifiers = modifiers;
        for (Modifier modifier : modifiers) {
            if (!modifier.allowedVariable) {
                throw new SyntaxException("Modifier " + modifier + " not allowed on variable", row, column);
            }
        }
    }

    public VariableDeclarationStatement(VariableDeclaration[] declarations, Set<Modifier> modifiers) {
        this(declarations, modifiers, 0, 0);
    }

    @Override
    protected void dump(StringBuilder output, int currentIndent, int indent) {
        output.append("VariableDeclarationStatement{\n")
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
              .append("declarations=[");
        if (declarations.length > 0) {
            output.append('\n');
            for (int i = 0; i < declarations.length; i++) {
                if (i > 0) output.append(",\n");
                output.append(getIndent(currentIndent + indent + indent));
                declarations[i].dump(output, currentIndent + indent + indent, indent);
            }
            output.append('\n')
                  .append(getIndent(currentIndent + indent));
        }
        output.append("]\n")
              .append(getIndent(currentIndent))
              .append('}');
    }

    public boolean isGlobalVariableDef() {
        return
            this.modifiers.contains(Modifier.GLOBAL) ||
            this.modifiers.contains(Modifier.PUBLIC) ||
            this.modifiers.contains(Modifier.LAZY);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("def ");
        for (Modifier modifier : modifiers) {
            result.append(modifier).append(' ');
        }
        for (int i = 0; i < declarations.length; i++) {
            if (i > 0) {
                result.append(", ");
            }
            result.append(declarations[i]);
        }
        return result.append(';').toString();
    }
}

package io.github.bananalang.parse.ast;

public final class ImportStatement extends StatementNode {
    public final String module, name;

    public ImportStatement(String module, String name, int row, int column) {
        super(row, column);
        this.module = module;
        this.name = name;
    }

    public ImportStatement(String module, String name) {
        this(module, name, 0, 0);
    }

    @Override
    protected void dump(StringBuilder output, int currentIndent, int indent) {
        output.append("ImportStatement{\n")
              .append(getIndent(currentIndent + indent))
              .append("module=\"")
              .append(module)
              .append("\",\n")
              .append(getIndent(currentIndent + indent))
              .append("name=\"")
              .append(name)
              .append("\"\n")
              .append(getIndent(indent))
              .append('}');
    }

    @Override
    public String toString() {
        return "import "
            + module.replace('/', '.')
            + '.'
            + name
            + ';';
    }
}

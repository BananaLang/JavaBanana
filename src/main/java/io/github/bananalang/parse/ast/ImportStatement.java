package io.github.bananalang.parse.ast;

public final class ImportStatement extends ASTNode {
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
        output.append(getIndent(currentIndent))
              .append("ImportStatement{module=\"")
              .append(module)
              .append("\", name=\"")
              .append(name)
              .append("\"}");
    }

    @Override
    public String toString() {
        return string()
               .add("module", module)
               .add("name", name)
               .toString();
    }
}

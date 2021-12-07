package io.github.bananalang.parse.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class StatementList extends ASTNode {
    public final List<ASTNode> children;

    public StatementList(int row, int column) {
        super(row, column);
        this.children = new ArrayList<>();
    }

    public StatementList() {
        this(0, 0);
    }

    public StatementList(Collection<ASTNode> children, int row, int column) {
        super(row, column);
        this.children = new ArrayList<>(children);
    }

    public StatementList(Collection<ASTNode> children) {
        this(children, 0, 0);
    }

    @Override
    public Iterable<? extends ASTNode> children() {
        return children;
    }

    @Override
    protected void dump(StringBuilder output, int currentIndent, int indent) {
        output.append(getIndent(currentIndent))
              .append("StatementList{children=[");
        if (children.size() > 0) {
            output.append('\n');
            for (int i = 0; i < children.size(); i++) {
                if (i > 0) output.append(",\n");
                children.get(i).dump(output, currentIndent + indent, indent);
            }
            output.append('\n')
                  .append(getIndent(currentIndent));
        }
        output.append("]}");
    }

    @Override
    public String toString() {
        return string()
               .add("children", children)
               .toString();
    }
}

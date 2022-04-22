package io.github.bananalang.parse.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class StatementList extends StatementNode {
    public final List<StatementNode> children;

    public StatementList(int row, int column) {
        super(row, column);
        this.children = new ArrayList<>();
    }

    public StatementList() {
        this(0, 0);
    }

    public StatementList(Collection<StatementNode> children, int row, int column) {
        super(row, column);
        this.children = new ArrayList<>(children);
    }

    public StatementList(Collection<StatementNode> children) {
        this(children, 0, 0);
    }

    @Override
    public Iterable<? extends ASTNode> children() {
        return children;
    }

    @Override
    protected void dump(StringBuilder output, int currentIndent, int indent) {
        output.append("StatementList{children=[");
        if (children.size() > 0) {
            output.append('\n');
            for (int i = 0; i < children.size(); i++) {
                if (i > 0) output.append(",\n");
                output.append(getIndent(currentIndent + indent));
                children.get(i).dump(output, currentIndent + indent, indent);
            }
            output.append('\n')
                  .append(getIndent(currentIndent));
        }
        output.append("]}");
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < children.size(); i++) {
            if (i > 0) {
                result.append('\n');
            }
            StatementNode child = children.get(i);
            if (child instanceof StatementList) {
                result.append('{');
            }
            result.append(child);
            if (child instanceof StatementList) {
                result.append('}');
            }
        }
        return result.toString();
    }
}

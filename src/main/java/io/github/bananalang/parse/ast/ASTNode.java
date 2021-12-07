package io.github.bananalang.parse.ast;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

import io.github.bananalang.util.ToStringBuilder;

public abstract class ASTNode implements java.io.Serializable {
    public static final long serialVersionUID = -2269867982590452423L;
    public final int row, column;

    protected ASTNode() {
        row = 0;
        column = 0;
    }

    protected ASTNode(int row, int column) {
        this.row = row;
        this.column = column;
    }

    protected abstract void dump(StringBuilder output, int currentIndent, int indent);

    public Iterable<? extends ASTNode> children() {
        return () -> new Iterator<ASTNode>() {
            @Override
            public boolean hasNext() {
                return false;
            }
            @Override
            public ASTNode next() {
                throw new NoSuchElementException();
            }
        };
    }

    public void visit(Predicate<? super ASTNode> predicate) {
        if (!predicate.test(this)) return;
        for (ASTNode child : children()) {
            child.visit(predicate);
        }
    }

    protected ToStringBuilder string() {
        return new ToStringBuilder(this)
                   .addIf(row != 0, "row", row)
                   .addIf(column != 0, "column", column);
    }

    @Override
    public String toString() {
        return string().toString();
    }

    public static String dump(ASTNode node) {
        return dump(node, 3);
    }

    public static String dump(ASTNode node, int indent) {
        StringBuilder result = new StringBuilder();
        node.dump(result, 0, indent);
        return result.toString();
    }

    protected static char[] getIndent(int indent) {
        char[] a = new char[indent];
        Arrays.fill(a, ' ');
        return a;
    }
}

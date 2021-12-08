package io.github.bananalang.parse.ast;

public abstract class StatementNode extends ASTNode {
    protected StatementNode() {
        super();
    }

    protected StatementNode(int row, int column) {
        super(row, column);
    }
}

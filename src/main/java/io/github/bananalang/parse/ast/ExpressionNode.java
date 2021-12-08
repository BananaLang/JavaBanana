package io.github.bananalang.parse.ast;

public abstract class ExpressionNode extends ASTNode {
    protected ExpressionNode() {
        super();
    }

    protected ExpressionNode(int row, int column) {
        super(row, column);
    }
}

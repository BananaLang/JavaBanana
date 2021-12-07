package io.github.bananalang.parse.ast;

import java.util.function.Predicate;

public interface ASTNode extends java.io.Serializable {
    public static final long serialVersionUID = -2269867982590452423L;

    public void visit(Predicate<? super ASTNode> predicate);
}

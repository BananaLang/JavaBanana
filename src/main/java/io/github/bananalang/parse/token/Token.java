package io.github.bananalang.parse.token;

public abstract class Token implements java.io.Serializable {
    public static final long serialVersionUID = -2236162400090034729L;
    public final int row, column;

    protected Token() {
        row = 0;
        column = 0;
    }

    protected Token(int row, int column) {
        this.row = row;
        this.column = column;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}

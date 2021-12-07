package io.github.bananalang.parse.token;

public final class StringToken extends Token {
    public static final long serialVersionUID = -2689618039594110841L;

    public final String value;

    public StringToken(String value, int row, int column) {
        super(row, column);
        this.value = value;
    }

    public StringToken(String value) {
        this(value, 0, 0);
    }

    @Override
    public String toString() {
        return string()
               .add("value", value)
               .toString();
    }
}

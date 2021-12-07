package io.github.bananalang.parse.token;

public final class DecimalToken extends Token {
    public static final long serialVersionUID = 8565953003158922696L;

    public final double value;

    public DecimalToken(String value, int row, int column) {
        super(row, column);
        this.value = Double.parseDouble(value);
    }

    public DecimalToken(String value) {
        this(value, 0, 0);
    }

    @Override
    public String toString() {
        return string()
               .add("value", value)
               .toString();
    }
}

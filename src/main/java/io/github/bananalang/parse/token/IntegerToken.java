package io.github.bananalang.parse.token;

import java.math.BigInteger;

import io.github.bananalang.util.ToStringBuilder;

public final class IntegerToken extends Token {
    public static final long serialVersionUID = 8565953003158922696L;

    public final BigInteger value;

    public IntegerToken(String value, int row, int column) {
        super(row, column);
        this.value = new BigInteger(value);
    }

    public IntegerToken(String value) {
        this(value, 0, 0);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                   .add("value", value)
                   .toString();
    }
}

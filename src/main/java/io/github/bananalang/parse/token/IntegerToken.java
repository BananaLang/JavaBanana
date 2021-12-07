package io.github.bananalang.parse.token;

import java.math.BigInteger;

import io.github.bananalang.util.ToStringBuilder;

public final class IntegerToken extends Token {
    public static final long serialVersionUID = 8565953003158922696L;

    public final BigInteger value;

    public IntegerToken(String value) {
        this.value = new BigInteger(value);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                   .add("value", value)
                   .toString();
    }
}

package io.github.bananalang.parse.token;

import io.github.bananalang.util.ToStringBuilder;

public final class DecimalToken extends Token {
    public static final long serialVersionUID = 8565953003158922696L;

    public final double value;

    public DecimalToken(String value) {
        this.value = Double.parseDouble(value);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                   .add("value", value)
                   .toString();
    }
}

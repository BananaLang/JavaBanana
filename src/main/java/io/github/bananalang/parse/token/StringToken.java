package io.github.bananalang.parse.token;

import io.github.bananalang.util.ToStringBuilder;

public final class StringToken extends Token {
    public static final long serialVersionUID = -2689618039594110841L;

    public final String value;

    public StringToken(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                   .add("value", value)
                   .toString();
    }
}

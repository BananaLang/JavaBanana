package io.github.bananalang.parse.token;

import java.util.Map;

import io.github.bananalang.parse.CharData;

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
        return escape(value);
    }

    public static String escape(String value) {
        for (Map.Entry<Character, Character> escape : CharData.CONTROL_CODES.entrySet()) {
            value = value.replace(escape.getValue().toString(), "\\" + escape.getKey());
        }
        return value;
    }
}

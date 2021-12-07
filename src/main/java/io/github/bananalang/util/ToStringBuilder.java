package io.github.bananalang.util;

public final class ToStringBuilder {
    private final StringBuilder builder;
    private int elements;

    public ToStringBuilder(Object o) {
        builder = new StringBuilder(o.getClass().getSimpleName());
        elements = 0;
    }

    public ToStringBuilder add(String name, Object o) {
        builder.append(elements++ > 0 ? ", " : "{").append(name).append('=').append(o);
        return this;
    }

    public String toString() {
        String result = builder.append("}").toString();
        builder.setLength(builder.length() - 1);
        return result;
    }
}

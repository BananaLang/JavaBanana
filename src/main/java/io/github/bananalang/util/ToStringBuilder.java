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

    public ToStringBuilder add(String name, char c) {
        builder.append(elements++ > 0 ? ", " : "{").append(name).append('=').append('\'').append(c).append('\'');
        return this;
    }

    public ToStringBuilder add(String name, String s) {
        builder.append(elements++ > 0 ? ", " : "{").append(name).append('=').append('"').append(s).append('"');
        return this;
    }

    public ToStringBuilder addIf(boolean add, String name, Object o) {
        return add ? this.add(name, o) : this;
    }

    public ToStringBuilder addIf(boolean add, String name, char c) {
        return add ? this.add(name, c) : this;
    }

    public ToStringBuilder addIf(boolean add, String name, String s) {
        return add ? this.add(name, s) : this;
    }

    public String toString() {
        String result = builder.append("}").toString();
        builder.setLength(builder.length() - 1);
        return result;
    }
}

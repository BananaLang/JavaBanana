package io.github.bananalang.parse.token;

public abstract class Token implements java.io.Serializable {
    public static final long serialVersionUID = -2236162400090034729L;

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}

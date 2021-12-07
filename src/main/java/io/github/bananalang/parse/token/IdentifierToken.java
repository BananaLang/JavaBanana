package io.github.bananalang.parse.token;

import io.github.bananalang.util.ToStringBuilder;

public final class IdentifierToken extends Token {
    public static final long serialVersionUID = -6522248973930551971L;

    public final String identifier;

    public IdentifierToken(String identifier, int row, int column) {
        super(row, column);
        this.identifier = identifier;
    }

    public IdentifierToken(String identifier) {
        this(identifier, 0, 0);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                   .add("identifier", identifier)
                   .toString();
    }

    public static Token identifierOrReserved(String identifier, int row, int column) {
        return ReservedToken.RESERVED_WORDS.contains(identifier) ?
                   new ReservedToken(identifier, row, column) :
                   new IdentifierToken(identifier, row, column);
    }

    public static Token identifierOrReserved(String identifier) {
        return identifierOrReserved(identifier, 0, 0);
    }
}

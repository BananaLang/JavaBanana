package io.github.bananalang.parse.token;

import io.github.bananalang.util.ToStringBuilder;

public final class IdentifierToken extends Token {
    public static final long serialVersionUID = -6522248973930551971L;

    public final String identifier;

    public IdentifierToken(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                   .add("identifier", identifier)
                   .toString();
    }

    public static Token identifierOrReserved(String identifier) {
        return ReservedToken.RESERVED_WORDS.contains(identifier) ? new ReservedToken(identifier) : new IdentifierToken(identifier);
    }
}

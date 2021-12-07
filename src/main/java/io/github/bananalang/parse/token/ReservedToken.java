package io.github.bananalang.parse.token;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import io.github.bananalang.util.ToStringBuilder;

public final class ReservedToken extends Token {
    public static final long serialVersionUID = -7739649703677026647L;

    public static final Set<String> RESERVED_WORDS = Collections.unmodifiableSet(new HashSet<>(
        Arrays.asList("if", "else", "for", "while", "break", "continue", "def", "this")
    ));

    public final String word;

    public ReservedToken(String word) {
        if (!RESERVED_WORDS.contains(word)) {
            throw new IllegalArgumentException("ReservedToken argument not a reserved word!");
        }
        this.word = word;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                   .add("word", word)
                   .toString();
    }

    public static boolean matchReservedWord(Token tok, String word) {
        return tok instanceof ReservedToken && ((ReservedToken)tok).word.equals(word);
    }
}

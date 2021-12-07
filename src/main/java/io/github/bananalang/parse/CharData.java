package io.github.bananalang.parse;

import java.util.HashMap;
import java.util.Map;

public class CharData {
    public static final String IDENTIFIER_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_";
    public static final String DIGITS = "0123456789";
    public static final Map<Character, Character> CONTROL_CODES = new HashMap<Character, Character>() {{
        put('"', '"');
        put('\\', '\\');
        put('/', '/');
        put('b', '\b');
        put('f', '\f');
        put('n', '\n');
        put('r', '\r');
        put('t', '\t');
    }};

    public static boolean isValidIdentifierStart(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    public static boolean isValidIdentifierMid(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '_';
    }

    public static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }
}

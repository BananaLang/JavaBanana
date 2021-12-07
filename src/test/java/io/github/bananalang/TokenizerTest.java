package io.github.bananalang;

import java.io.IOException;

import io.github.bananalang.parse.Tokenizer;

public class TokenizerTest {
    public static void main(String[] args) throws IOException {
        System.out.println(new Tokenizer("a += -5e-2;").tokenize());
    }
}

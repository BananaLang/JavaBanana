package io.github.bananalang;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import io.github.bananalang.parse.Tokenizer;

public class TokenizerTest {
    public static void main(String[] args) throws IOException {
        System.out.println(new Tokenizer("a += 2;").tokenize());
    }
}

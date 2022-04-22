package io.github.bananalang;

import java.io.IOException;
import java.util.List;

import io.github.bananalang.parse.Parser;
import io.github.bananalang.parse.Tokenizer;
import io.github.bananalang.parse.ast.ASTNode;
import io.github.bananalang.parse.token.Token;

public class ParserTest {
    public static void main(String[] args) throws IOException {
        List<Token> tokens = new Tokenizer(
            "import java.lang.Class.forName;" +
            "println(forName(\"java.util.HashMap\").getDeclaredMethod(" +
                "\"computeIfAbsent\", forName(\"java.lang.Object\"), forName(\"java.util.function.Supplier\")" +
            "));"
        ).tokenize();
        System.out.println(tokens);

        System.out.println();

        ASTNode root = new Parser(tokens).parse();
        System.out.println(ASTNode.dump(root));
        System.out.println();
        System.out.println(root);
    }
}

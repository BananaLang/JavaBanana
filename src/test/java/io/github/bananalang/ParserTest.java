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
            "def String? join(String? a, String? b) {" +
                "return (a ?? \"null\").concat(b ?? \"null\");" +
            "}" +
            "def String a = \"hello\";"
        ).tokenize();
        System.out.println(tokens);

        System.out.println();

        ASTNode root = new Parser(tokens).parse();
        System.out.println(ASTNode.dump(root));
        System.out.println();
        System.out.println(root);
    }
}

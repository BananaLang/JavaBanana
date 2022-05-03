package io.github.bananalang;

import java.io.IOException;
import java.util.List;

import io.github.bananalang.compilecommon.problems.GenericCompilationFailureException;
import io.github.bananalang.compilecommon.problems.ProblemCollector;
import io.github.bananalang.parse.Parser;
import io.github.bananalang.parse.Tokenizer;
import io.github.bananalang.parse.ast.ASTNode;
import io.github.bananalang.parse.token.Token;

public class ParserTest {
    public static void main(String[] args) throws IOException {
        ProblemCollector problemCollector = new ProblemCollector();
        List<Token> tokens = new Tokenizer(
            "def global public var test() {\n" +
                "println(hello);\n" +
            "}\n" +
            "def global var hello = \"Hello world!\";\n" +
            "test();\n", problemCollector
        ).tokenize();
        System.out.println(tokens);

        System.out.println();

        try {
            ASTNode root = new Parser(tokens, problemCollector).parse();
            System.out.println(ASTNode.dump(root));
            System.out.println();
            System.out.println(root);
        } catch (GenericCompilationFailureException e) {
            System.out.println(e.getProblemCollector().ansiFormattedString());
        }
    }
}

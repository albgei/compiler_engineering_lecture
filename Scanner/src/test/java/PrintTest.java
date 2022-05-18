

import parser.*;
import parser.stmts.*;
import scanner.*;
import traversal.*;

import org.junit.jupiter.api.Test;

import java.util.List;

public class PrintTest {
    static final String program = """
            fun printSum(a,b) {
            print a+b;
            }
            printSum(1,2);
            """;

    //Just succeeds
    @Test
    void printTest() {
        LoxScanner scanner = new LoxScanner(program);
        List<Token> actual = scanner.scan();
        Parser parser = new Parser(actual);
        List<Stmt> statements = parser.parse();
        AstPrinter printer = new AstPrinter();
        statements.stream().map(printer::print).forEach(System.out::println);
    }
}

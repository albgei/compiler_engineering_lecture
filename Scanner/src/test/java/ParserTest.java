import parser.*;
import parser.stmts.*;
import scanner.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParserTest {
    static final String program = """
            fun printSum(a,b) {
            print a+b;
            }
            print 25+60;
            """;

    @Test
    void parseTest() {
        LoxScanner scanner = new LoxScanner(program);
        List<Token> actual = scanner.scan();
        Parser parser = new Parser(actual);
        List<Stmt> statements = parser.parse();
        assertTrue(statements.get(0) instanceof Function, "Expected Type Function got " + actual.get(0).getClass().getName());
        assertTrue(statements.get(1) instanceof Print, "Expected Type Print got " + actual.get(0).getClass().getName());
        assertTrue(((Function) statements.get(0)).body.get(0) instanceof Print, "Expected Type Print in function");
        assertEquals(((Function) statements.get(0)).parameters.get(0).type, TokenType.IDENTIFIER, "Expected first function parameter to be identifier");

    }
}

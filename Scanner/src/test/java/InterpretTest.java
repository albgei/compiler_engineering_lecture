
import parser.*;
import parser.stmts.*;
import scanner.*;
import traversal.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterpretTest {
    private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private static final PrintStream originalOut = System.out;

    @BeforeAll
    public static void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterAll
    public static void restoreStreams() {
        System.setOut(originalOut);
    }

    static final String program = """
            fun printSum(a,b) {
            print a+b;
            print clock();
            }
            printSum(5,2);
            """;
    static final String arithmetic1 = """
            fun printSum(a,b) {
            return a+b;
            }
            print printSum(5,2);
            """;
    static final String arithmetic2 = """
            fun printSum(a,b,c,d) {
            return a+b*c+d;
            }
            print printSum(5,2,2,4);
            """;
    static final String conditionaltest1 = """
            fun printSum(a,b,c,d) {
                if(b>a) {
                    return b+a;
                } else {
                    return c+d;
                }
            }
            print printSum(5,2,2,4);
            """;
    static final String looptest = """
            fun printSum(a,d) {
                while(a < d){
                    a = a+1;
                }
                return a;
            }
            print printSum(2,5);
            
            """;
    static final String looptest2 = """
            fun printSum(a,d) {
                for(var i = 0; a < d; i = i+1){
                    a = a+1;
                }
                return a;
            }
            print printSum(2,5);
            
            """;
    @Test
    void loopTest2() {
        LoxScanner scanner = new LoxScanner(looptest2);
        List<Token> actual = scanner.scan();
        Parser parser = new Parser(actual);
        Interpreter interpreter = new Interpreter();
        List<Stmt> statements = parser.parse();
        interpreter.interpret(statements);
        assertEquals("5.0", outContent.toString().trim(), "Loop test 1 should eval to 5");
        outContent.reset();
    }
    @Test
    void loopTest() {
        LoxScanner scanner = new LoxScanner(looptest);
        List<Token> actual = scanner.scan();
        Parser parser = new Parser(actual);
        Interpreter interpreter = new Interpreter();
        List<Stmt> statements = parser.parse();
        interpreter.interpret(statements);
        assertEquals("5.0", outContent.toString().trim(), "Loop test 1 should eval to 5");
        outContent.reset();
    }
    @Test
    void conditionalTest() {
        LoxScanner scanner = new LoxScanner(conditionaltest1);
        List<Token> actual = scanner.scan();
        Parser parser = new Parser(actual);
        Interpreter interpreter = new Interpreter();
        List<Stmt> statements = parser.parse();
        interpreter.interpret(statements);
        assertEquals("6.0", outContent.toString().trim(), "Conditional test 1 should eval to 6");
        outContent.reset();
    }
    @Test
    void arithmeticTest2() {
        LoxScanner scanner = new LoxScanner(arithmetic2);
        List<Token> actual = scanner.scan();
        Parser parser = new Parser(actual);
        Interpreter interpreter = new Interpreter();
        List<Stmt> statements = parser.parse();
        interpreter.interpret(statements);
        assertEquals("13.0", outContent.toString().trim(), "Arithmetic test 2 should eval to 13");
        outContent.reset();
    }

    @Test
    void arithmeticTest() {

        outContent.reset();
        LoxScanner scanner = new LoxScanner(arithmetic1);
        List<Token> actual = scanner.scan();
        Parser parser = new Parser(actual);
        Interpreter interpreter = new Interpreter();
        List<Stmt> statements = parser.parse();
        interpreter.interpret(statements);
        assertEquals("7.0", outContent.toString().trim(), "Arithmetic test 1 should eval to 7");
        outContent.reset();
    }

    @Test
    void noFailTest() {
        LoxScanner scanner = new LoxScanner(program);
        List<Token> actual = scanner.scan();
        Parser parser = new Parser(actual);
        Interpreter interpreter = new Interpreter();
        List<Stmt> statements = parser.parse();
        interpreter.interpret(statements);
        outContent.reset();
    }
}

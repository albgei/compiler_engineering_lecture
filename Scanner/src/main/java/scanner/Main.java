package scanner;

import parser.Parser;
import parser.stmts.Stmt;
import traversal.Interpreter;

import java.util.List;

public class Main {

    static final String program = """
            fun printSum(a,b) {
            print a+b;
            }
            
            printSum(5,2);
            """;

    public static void main(String[] args) {
        LoxScanner scanner = new LoxScanner(program);
        scanner.scan();
        LoxScanner scanner1 = new LoxScanner("print \"Hello World\";");
        scanner1.scan();
        LoxScanner scanner2 = new LoxScanner("//Kommentar ohne Sinn + - * / oops@ kp");
        scanner2.scan();

        LoxScanner loxScanner = new LoxScanner(program);
        List<Token> tokens = loxScanner.scan();

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        Interpreter interpreter = new Interpreter();
        interpreter.interpret(statements);
    }
}

package scanner;

import parser.Parser;

import java.util.List;

public class Main {

    static final String program = """
            fun printSum(a,b) {
            print a+b;
            }
            print 25+60;
            """;

    public static void main(String[] args) {
        LoxScanner scanner = new LoxScanner(program);
        scanner.scan();
        LoxScanner scanner1 = new LoxScanner("print \"Hello World\";");
        scanner1.scan();
        LoxScanner scanner2 = new LoxScanner("12.45");
        scanner2.scan();
        LoxScanner scanner3 = new LoxScanner("//Kommentar ohne Sinn + - * / oops@ kp");
        scanner3.scan();

        LoxScanner loxScanner = new LoxScanner(program);
        List<Token> tokens = loxScanner.scan();

        Parser parser = new Parser(tokens);
        parser.parse();
    }
}

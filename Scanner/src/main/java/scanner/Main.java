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
        /*
        long start = System.nanoTime();
        LoxScanner scanner = new LoxScanner(program);
        for (int i = 0; i < 10; i++) {
            System.out.println("Zeit " + i + ": " + (System.nanoTime() - start));
            start = System.nanoTime();
            scanner.scan();
        }
        System.out.println("Zeit: " + (System.nanoTime() - start));
        start = System.nanoTime();
        LoxScanner scanner1 = new LoxScanner("print \"Hello World\";");
        scanner1.scan();
        System.out.println("Zeit: " + (System.nanoTime() - start));
        start = System.nanoTime();
        LoxScanner scanner2 = new LoxScanner("12.45");
        scanner2.scan();
        System.out.println("Zeit: " + (System.nanoTime() - start));
        start = System.nanoTime();
        LoxScanner scanner3 = new LoxScanner("//Kommentar ohne Sinn + - * / oops@ kp");
        scanner3.scan();
        System.out.println("Zeit: " + (System.nanoTime() - start));
        */
        LoxScanner loxScanner = new LoxScanner(program);
        List<Token> tokens = loxScanner.scan();

        Parser parser = new Parser(tokens);
        parser.parse();
    }
}

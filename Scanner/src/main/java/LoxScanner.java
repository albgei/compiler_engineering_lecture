import java.util.*;

public class LoxScanner {


    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private Deque<Character> charsQueue = new ArrayDeque<>();


    public LoxScanner(String source) {
        this.source = source;
    }


    public List<Token> scanLine(String line, int lineNumber) {
        List<Token> returnToken = new ArrayList<>();

        char[] charsLine = line.toCharArray();

        for (char c : charsLine) {
            charsQueue.addLast(c);
        }

        long time = System.currentTimeMillis();

        while (!charsQueue.isEmpty()) {
            if (charsQueue.getFirst() == '/') {
                charsQueue.removeFirst();
                if (charsQueue.getFirst() == '/') {
                    charsQueue.removeFirst();
                    StringBuilder stringBuilder = new StringBuilder();
                    while (!charsQueue.isEmpty()) {
                        stringBuilder.append(charsQueue.getFirst());
                        charsQueue.removeFirst();
                    }
                    returnToken.add(new Token(TokenType.COMMENT, stringBuilder.toString(), stringBuilder.toString(), lineNumber));
                    break;
                } else {
                    charsQueue.addFirst('/');
                }
            }
            if (charsQueue.getFirst() == ' ')
                charsQueue.removeFirst();
            if (!charsQueue.isEmpty())
                returnToken.add(checkStaticCharacterTokens(lineNumber));
            if (!charsQueue.isEmpty())
                returnToken.add(checkFlexibleCharacterToken(lineNumber));
            if (System.currentTimeMillis() - time > 10000) {
                System.out.println("Zeitüberschreitung beim lesen von Zeile:" + lineNumber);
                return new ArrayList<>();
            }
        }

        List<Token> temp = new ArrayList<>();
        for (Token t : returnToken) {
            if (t != null) {
                temp.add(t);
            }
        }

        return temp;
    }

    public List<Token> scan() {
        String[] lines = source.split("\n");
        for (int i = 0; i < lines.length; i++) {
            tokens.addAll(scanLine(lines[i], i));
        }
        tokens.add(new Token(TokenType.EOF, "", "", lines.length));
        return tokens;
    }
    
    private Token generateToken(String tString, int lineNumber) {
        return switch (tString) {
            case "{" -> new Token(TokenType.LEFT_BRACE, tString, tString, lineNumber);
            case "}" -> new Token(TokenType.RIGHT_BRACE, tString, tString, lineNumber);
            case "(" -> new Token(TokenType.LEFT_PAREN, tString, tString, lineNumber);
            case ")" -> new Token(TokenType.RIGHT_PAREN, tString, tString, lineNumber);
            case "," -> new Token(TokenType.COMMA, tString, tString, lineNumber);
            case "." -> new Token(TokenType.DOT, tString, tString, lineNumber);
            case "-" -> new Token(TokenType.MINUS, tString, tString, lineNumber);
            case "+" -> new Token(TokenType.PLUS, tString, tString, lineNumber);
            case ";" -> new Token(TokenType.SEMICOLON, tString, tString, lineNumber);
            case "/" -> new Token(TokenType.SLASH, tString, tString, lineNumber);
            case "*" -> new Token(TokenType.STAR, tString, tString, lineNumber);
            case "!" -> new Token(TokenType.BANG, tString, tString, lineNumber);
            case "=" -> new Token(TokenType.EQUAL, tString, tString, lineNumber);
            case ">" -> new Token(TokenType.GREATER, tString, tString, lineNumber);
            case "<" -> new Token(TokenType.LESS, tString, tString, lineNumber);
            case "!=" -> new Token(TokenType.BANG_EQUAL, tString, tString, lineNumber);
            case "==" -> new Token(TokenType.EQUAL_EQUAL, tString, tString, lineNumber);
            case ">=" -> new Token(TokenType.GREATER_EQUAL, tString, tString, lineNumber);
            case "<=" -> new Token(TokenType.LESS_EQUAL, tString, tString, lineNumber);
            case "and" -> new Token(TokenType.AND, tString, tString, lineNumber);
            case "else" -> new Token(TokenType.ELSE, tString, tString, lineNumber);
            case "false" -> new Token(TokenType.FALSE, tString, tString, lineNumber);
            case "fun" -> new Token(TokenType.FUN, tString, tString, lineNumber);
            case "for" -> new Token(TokenType.FOR, tString, tString, lineNumber);
            case "if" -> new Token(TokenType.IF, tString, tString, lineNumber);
            case "nil" -> new Token(TokenType.NIL, tString, tString, lineNumber);
            case "or" -> new Token(TokenType.OR, tString, tString, lineNumber);
            case "print " -> new Token(TokenType.PRINT, tString, tString, lineNumber);
            case "return" -> new Token(TokenType.RETURN, tString, tString, lineNumber);
            case "true" -> new Token(TokenType.TRUE, tString, tString, lineNumber);
            case "var" -> new Token(TokenType.VAR, tString, tString, lineNumber);
            case "while" -> new Token(TokenType.WHILE, tString, tString, lineNumber);
            default -> null;
        };
    }


    private Token checkStaticCharacterTokens(int lineNumber) {
        String[] doubleTokens = new String[]{"!=", "==", ">=", "<=", "and", "else", "false", "fun", "for", "if", "nil", "or", "print ", "return", "true", "var", "while", "{", "}", "(", ")", ",", ".", "-", "+", ";", "/", "*", "!", "=", ">", "<"};
        for (String multiple : doubleTokens) {
            if (compare(multiple)) {
                return generateToken(multiple, lineNumber);
            }
        }
        return null;
    }

    private boolean compare(String multiple) {
        char[] multipleChars = multiple.toCharArray();
        char[] charsToCheck = new char[multipleChars.length];

        if (charsQueue.size() < multipleChars.length)
            return false;

        for (int i = 0; i < multipleChars.length; i++) {
            charsToCheck[i] = charsQueue.getFirst();
            charsQueue.removeFirst();
        }

        for (int i = 0; i < multipleChars.length; i++) {
            if (charsToCheck[i] != multipleChars[i]) {
                charsToCheck = reverseCharArray(charsToCheck);
                for (char c : charsToCheck) {
                    charsQueue.addFirst(c);
                }
                return false;
            }
        }
        return true;
    }

    private char[] reverseCharArray(char[] cArr) {
        char[] reversed = new char[cArr.length];
        int j = cArr.length - 1;
        for (char c : cArr) {
            reversed[j--] = c;
        }
        return reversed;
    }

    private Token checkFlexibleCharacterToken(int lineNumber) {
        if (charsQueue.getFirst() == '"') {
            StringBuilder stringBuilder = new StringBuilder();
            charsQueue.removeFirst();
            while (charsQueue.getFirst() != '"') {
                stringBuilder.append(charsQueue.getFirst());
                charsQueue.removeFirst();
            }
            charsQueue.removeFirst();
            return new Token(TokenType.STRING, stringBuilder.toString(), stringBuilder.toString(), lineNumber);
        }

        if ((charsQueue.getFirst() <= 57 && charsQueue.getFirst() >= 48) || (charsQueue.getFirst() == '.')) {
            StringBuilder stringBuilder = new StringBuilder();
            while (!charsQueue.isEmpty() && ((charsQueue.getFirst() <= 57 && charsQueue.getFirst() >= 48) || (charsQueue.getFirst() == '.'))) {
                stringBuilder.append(charsQueue.getFirst());
                charsQueue.removeFirst();
            }
            return generateTokenNumber(stringBuilder.toString(), lineNumber);
        }

        if ((charsQueue.getFirst() <= 89 && charsQueue.getFirst() >= 65) || (charsQueue.getFirst() <= 122 && charsQueue.getFirst() >= 97)) {
            StringBuilder stringBuilder = new StringBuilder();
            while (!charsQueue.isEmpty() && ((charsQueue.getFirst() <= 89 && charsQueue.getFirst() >= 65) || (charsQueue.getFirst() <= 122 && charsQueue.getFirst() >= 97))) {
                stringBuilder.append(charsQueue.getFirst());
                charsQueue.removeFirst();
            }
            return new Token(TokenType.IDENTIFIER, stringBuilder.toString(), stringBuilder.toString(), lineNumber);
        }
        return null;
    }

    private Token generateTokenNumber(String str, int lineNumber) {
        if (str.contains(".")) {
            return new Token(TokenType.NUMBER, str, Double.valueOf(str), lineNumber);
        } else {
            return new Token(TokenType.NUMBER, str, Integer.valueOf(str), lineNumber);
        }
    }
}

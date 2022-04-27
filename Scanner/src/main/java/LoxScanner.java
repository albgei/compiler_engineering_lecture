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

        while (!charsQueue.isEmpty()) {
            if (charsQueue.getFirst() == ' ')
                charsQueue.removeFirst();
            if (!charsQueue.isEmpty())
                returnToken.add(checkSingleCharacterTokens(lineNumber));
            if (!charsQueue.isEmpty())
                returnToken.add(checkMultipleCharacterTokens(lineNumber));
            if (!charsQueue.isEmpty())
                returnToken.add(checkNumerousToken(lineNumber));
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

    private Token checkSingleCharacterTokens(int lineNumber) {
        char[] singleTokens = new char[]{'{', '}', '(', ')', ',', '.', '-', '+', ';', '/', '*', '!', '=', '>', '<'};
        for (char single : singleTokens) {
            if (charsQueue.getFirst() == single) {
                charsQueue.removeFirst();
                return generateTokenSingle(String.valueOf(single), lineNumber);
            }
        }
        return null;
    }

    private Token generateTokenSingle(String tString, int lineNumber) {
        TokenType type = null;

        switch (tString) {
            case "{":
                type = TokenType.LEFT_BRACE;
                break;
            case "}":
                type = TokenType.RIGHT_BRACE;
                break;
            case "(":
                type = TokenType.LEFT_PAREN;
                break;
            case ")":
                type = TokenType.RIGHT_PAREN;
                break;
            case ",":
                type = TokenType.COMMA;
                break;
            case ".":
                type = TokenType.DOT;
                break;
            case "-":
                type = TokenType.MINUS;
                break;
            case "+":
                type = TokenType.PLUS;
                break;
            case ";":
                type = TokenType.SEMICOLON;
                break;
            case "/":
                type = TokenType.SLASH;
                break;
            case "*":
                type = TokenType.STAR;
                break;
            case "!":
                type = TokenType.BANG;
                break;
            case "=":
                type = TokenType.EQUAL;
                break;
            case ">":
                type = TokenType.GREATER;
                break;
            case "<":
                type = TokenType.LESS;
                break;
        }

        if (type != null)
            return new Token(type, tString, tString, lineNumber);
        return null;
    }


    private Token checkMultipleCharacterTokens(int lineNumber) {
        String[] doubleTokens = new String[]{"!=", "==", ">=", "<=", "and", "else", "false", "fun", "for", "if", "nil", "or", "print ", "return", "true", "var", "while"};
        for (String multiple : doubleTokens) {
            if (compareMultiple(multiple)) {
                return generateTokenMultiple(multiple, lineNumber);
            }
        }
        return null;
    }

    private boolean compareMultiple(String multiple) {
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
        for (int i = 0; i < cArr.length; i++) {
            reversed[j] = cArr[i];
            j--;
        }
        return reversed;
    }

    private Token generateTokenMultiple(String tString, int lineNumber) {
        TokenType type = null;

        switch (tString) {
            case "!=":
                type = TokenType.BANG_EQUAL;
                break;
            case "==":
                type = TokenType.EQUAL_EQUAL;
                break;
            case ">=":
                type = TokenType.GREATER_EQUAL;
                break;
            case "<=":
                type = TokenType.LESS_EQUAL;
                break;
            case "and":
                type = TokenType.AND;
                break;
            case "else":
                type = TokenType.ELSE;
                break;
            case "false":
                type = TokenType.FALSE;
                break;
            case "fun":
                type = TokenType.FUN;
                break;
            case "for":
                type = TokenType.FOR;
                break;
            case "if":
                type = TokenType.IF;
                break;
            case "nil":
                type = TokenType.NIL;
                break;
            case "or":
                type = TokenType.OR;
                break;
            case "print ":
                type = TokenType.PRINT;
                break;
            case "return":
                type = TokenType.RETURN;
                break;
            case "true":
                type = TokenType.TRUE;
                break;
            case "var":
                type = TokenType.VAR;
                break;
            case "while":
                type = TokenType.WHILE;
                break;
        }
        if (type != null)
            return new Token(type, tString, tString, lineNumber);
        return null;
    }


    private Token checkNumerousToken(int lineNumber) {
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

        if (isNumber(charsQueue.getFirst())) {
            StringBuilder stringBuilder = new StringBuilder();
            while (!charsQueue.isEmpty() && isNumber(charsQueue.getFirst())) {
                stringBuilder.append(charsQueue.getFirst());
                charsQueue.removeFirst();
            }
            return generateTokenNumber(stringBuilder.toString(), lineNumber);
        }

        if (isCharacter(charsQueue.getFirst())) {
            StringBuilder stringBuilder = new StringBuilder();
            while (!charsQueue.isEmpty() && isCharacter(charsQueue.getFirst())) {
                stringBuilder.append(charsQueue.getFirst());
                charsQueue.removeFirst();
            }
            return new Token(TokenType.IDENTIFIER, stringBuilder.toString(), stringBuilder.toString(), lineNumber);
        }


        return null;
    }

    private boolean isNumber(char c) {
        return (c <= 57 && c >= 48) || (c == '.');
    }

    private boolean isCharacter(char c) {
        return (c <= 89 && c >= 65) || (c <= 122 && c >= 97);
    }

    private Token generateTokenNumber(String str, int lineNumber) {
        if (str.contains(".")) {
            return new Token(TokenType.NUMBER, str, Double.valueOf(str), lineNumber);
        } else {
            return new Token(TokenType.NUMBER, str, Integer.valueOf(str), lineNumber);
        }
    }
}

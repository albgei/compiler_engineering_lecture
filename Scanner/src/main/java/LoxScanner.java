import java.util.*;

public class LoxScanner {

    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private final Deque<Character> charsQueue = new ArrayDeque<>();


    public LoxScanner(String source) {
        this.source = source;
    }


    public List<Token> scanLine(String line, int lineNumber) {
        List<Token> returnToken = new ArrayList<>();

        for (char c : line.toCharArray()) {
            charsQueue.addLast(c);
        }

        long time = System.currentTimeMillis();

        while (!charsQueue.isEmpty()) {
            if (charsQueue.getFirst() == ' ')
                charsQueue.removeFirst();

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

            if (charsQueue.isEmpty())
                break;
            returnToken.add(checkStaticCharacterTokens(lineNumber));

            if (System.currentTimeMillis() - time > 10000) {
                System.out.println("Zeitüberschreitung beim lesen von Zeile:" + lineNumber);
                return new ArrayList<>();
            }
        }

        if (returnToken.contains(null)) {
            List<Token> temp = new ArrayList<>(returnToken);
            returnToken.clear();
            for (Token t : temp) {
                if (t != null) {
                    returnToken.add(t);
                }
            }
        }

        return returnToken;
    }

    public List<Token> scan() {
        String[] lines = source.split("\n");
        int i = 0;
        for (String line : lines) {
            tokens.addAll(scanLine(line, i++));
        }
        tokens.add(new Token(TokenType.EOF, "", "", lines.length));
        return tokens;
    }

    private Token checkStaticCharacterTokens(int lineNumber) {
        for (TokenType tokenType : TokenType.values()) {
            if (tokenType != TokenType.COMMENT && tokenType != TokenType.EOF && tokenType != TokenType.IDENTIFIER && tokenType != TokenType.NUMBER && tokenType != TokenType.STRING && compare(tokenType)) {
                return new Token(tokenType, tokenType.getRegex(), tokenType.getRegex(), lineNumber);
            }
        }

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

    private boolean compare(TokenType tokenType) {
        char[] multipleChars = tokenType.getRegex().toCharArray();
        char[] charsToCheck = new char[multipleChars.length];

        if (charsQueue.size() < multipleChars.length)
            return false;

        for (int i = 0; i < multipleChars.length; i++) {
            charsToCheck[i] = charsQueue.getFirst();
            charsQueue.removeFirst();
        }
        int i = 0;
        for (char c : charsToCheck) {
            if (c != multipleChars[i++]) {
                for (char cs : reverseCharArray(charsToCheck)) {
                    charsQueue.addFirst(cs);
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

    private Token generateTokenNumber(String str, int lineNumber) {
        if (str.contains(".")) {
            return new Token(TokenType.NUMBER, str, Double.valueOf(str), lineNumber);
        } else {
            return new Token(TokenType.NUMBER, str, Integer.valueOf(str), lineNumber);
        }
    }
}

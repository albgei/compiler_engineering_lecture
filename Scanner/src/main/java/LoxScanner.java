import java.util.*;

public class LoxScanner {


    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private Queue<char> charsQueue = new Queue<>();


    public LoxScanner(String source) {
        this.source = source;
    }


    public List<Token> scanLine(String line, int lineNumber) {
        List<Token> returnToken = new ArrayList<>();

        char[] charsLine = line.toCharArray();

        for (char c : charsLine) {
        charsQueue.
        }

        return returnToken;
    }

    public List<Token> scan() {
        String[] lines = source.split("\n");
        for (int i = 0; i < lines.length; i++) {
            tokens.addAll(scanLine(lines[i], i));
        }
        tokens.add(new Token(EOF, "", "", lines.length));
        return tokens;
    }

    private boolean checkFunktion(char c) {
        if (c == 'f')
            return true;
        return false;
    }
}

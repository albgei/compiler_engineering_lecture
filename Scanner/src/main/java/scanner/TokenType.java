package scanner;

public enum TokenType {

    // Single-character tokens.
    LEFT_PAREN ("("), RIGHT_PAREN (")"), LEFT_BRACE ("{"), RIGHT_BRACE ("}"),
    COMMA (","), DOT ("."), MINUS ("-"), PLUS ("+"), SEMICOLON (";"), SLASH ("/"), STAR ("*"),

    // One or two character tokens.
    BANG_EQUAL ("!="), BANG ("!"),
    EQUAL_EQUAL ("=="), EQUAL ("="),
    GREATER_EQUAL (">="), GREATER (">"),
    LESS_EQUAL ("<="), LESS ("<"),


    // Keywords.
    AND ("and"), ELSE ("else"), FALSE ("false"), FUN ("fun"), FOR ("for"), IF ("if"), NIL ("nil"), OR ("or"),
    PRINT ("print "), RETURN ("return"), TRUE ("true"), VAR ("var"), WHILE ("while"),

    // Literals.
    IDENTIFIER ("([a-z]*[A-Z]*)*"), STRING ("\".*\""), NUMBER ("[0-9]|.[0-9]"),
    EOF (""), COMMENT ("//.*");

    private final String regex;
    TokenType(String regex) {
        this.regex = regex;
    }
    public String getRegex() {
        return this.regex;
    }
}
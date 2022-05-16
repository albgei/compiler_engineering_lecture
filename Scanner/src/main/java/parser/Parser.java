package parser;

import scanner.*;
import parser.stmts.*;
import parser.expr.*;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private static class ParseError extends RuntimeException {
    }

    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Stmt> parse() {

        /*
        Thread max_wait = new Thread(() -> {
        long time = System.currentTimeMillis();

        if (System.currentTimeMillis() - time > 10000) {
            System.out.println("Zeitüberschreitung beim Parsen.");
            return;
        }
        });
        max_wait.start();
        */

        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(declaration());

        }

        return statements;
    }

    private Expr expression() {
        return assignment();
    }

    private Stmt declaration() {
        try {
            if (match(TokenType.FUN)) return function("function");
            if (match(TokenType.VAR)) return varDeclaration();

            return statement();
        } catch (ParseError error) {
            return null;
        }
    }

    private Stmt statement() {
        if (match(TokenType.FOR)) return forStatement();
        if (match(TokenType.IF)) return ifStatement();
        if (match(TokenType.PRINT)) return printStatement();
        if (match(TokenType.RETURN)) return returnStatement();
        if (match(TokenType.WHILE)) return whileStatement();
        if (match(TokenType.LEFT_BRACE)) return new Block(block());

        return expressionStatement();
    }

    private Stmt forStatement() {
        ArrayList<Stmt> block = new ArrayList<>();
        Expr increment;
        Expr condition;

        consume(TokenType.LEFT_PAREN, "Expect '(' at the beginning of for head.");
        if (match(TokenType.VAR)) {
            block.add(varDeclaration());
        } else if (!match(TokenType.SEMICOLON)) {
            block.add(expressionStatement());
        } else {
            consume(TokenType.SEMICOLON, "Expect ';' in for statement if not starts with variable");
        }

        increment = expression();
        consume(TokenType.SEMICOLON, "Expect ';' after increment expression.");


        condition = expression();
        consume(TokenType.RIGHT_PAREN, "Expect ')' at the end of for head.");

        List<Stmt> body = new ArrayList<>();
        body.add(statement());
        body.add(0, new Expression(increment));
        Stmt stmt = new Block(body);
        While whileStmt = new While(condition, stmt);

        block.add(whileStmt);
        return new Block(block);

    }

    private Stmt ifStatement() {
        consume(TokenType.LEFT_PAREN, "Expect '(' after 'if'.");
        Expr condition = expression();
        consume(TokenType.RIGHT_PAREN, "Expect ')' after if condition.");

        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if (match(TokenType.ELSE)) {
            elseBranch = statement();
        }

        return new If(condition, thenBranch, elseBranch);
    }

    private Stmt printStatement() {
        Expr expr = expression();
        consume(TokenType.SEMICOLON, "Expect ';' after print statement.");
        return new Print(expr);
    }

    private Stmt returnStatement() {
        int old = current - 1;
        if (match(TokenType.SEMICOLON))
            return new Return(tokens.get(old), null);
        Expr expr = expression();
        consume(TokenType.SEMICOLON, "Expect ';' after return statement.");
        return new Return(tokens.get(old), expr);
    }

    private Stmt varDeclaration() {
        Expr init = null;
        consume(TokenType.IDENTIFIER, "Expect identifier after 'var'.");
        Token name = previous();
        if (match(TokenType.EQUAL))
            init = expression();
        consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.");
        return new Var(name, init);
    }

    private Stmt whileStatement() {
        consume(TokenType.LEFT_PAREN, "Expect '(' after 'while'.");
        Expr condition = expression();
        consume(TokenType.RIGHT_PAREN, "Expect ')' after while expression.");
        Stmt body = statement();
        return new While(condition, body);
    }

    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(TokenType.SEMICOLON, "Expect ';' after Expression");
        return new Expression(expr);
    }

    private Function function(String kind) {
        Token name;
        List<Stmt> body;
        consume(TokenType.IDENTIFIER, "Expect identifier after 'fun'.");
        name = previous();
        consume(TokenType.LEFT_PAREN, "Expect '(' after function identifier.");
        List<Token> parameters = parameters();
        consume(TokenType.RIGHT_PAREN, "Expect ')' after function parameter list.");
        consume(TokenType.LEFT_BRACE, "Expect '{' at the beginning of a code block.");
        body = block();
        return new Function(name, parameters, body);
    }

    private List<Token> parameters() {
        ArrayList<Token> parameters = new ArrayList<>();
        while (check(TokenType.IDENTIFIER) || match(TokenType.COMMA)) {
            if (match(TokenType.IDENTIFIER)) {
                parameters.add(previous());
            }
        }
        return parameters;
    }

    private List<Stmt> block() {
        List<Stmt> block = new ArrayList<>();

        while (!check(TokenType.RIGHT_BRACE)) {
            block.add(declaration());
        }
        consume(TokenType.RIGHT_BRACE, "Expect '}' at the end of a block.");
        return block;
    }

    private Expr assignment() {
        Token name = null;
        Expr value;
        if (false &&(check(TokenType.IDENTIFIER) || check(TokenType.STRING))) {
            call();
            name = previous();
            match(TokenType.EQUAL);
            value = assignment();
        } else
            value = or();
        return new Assign(name, value);
    }

    private Expr or() {
        Expr expr = and();

        while (match(TokenType.OR)) {
            Token operator = previous();
            Expr right = and();
            expr = new Logical(expr, operator, right);
        }

        return expr;
    }

    private Expr and() {
        Expr expr = equality();

        while (match(TokenType.AND)) {
            Token operator = previous();
            Expr right = equality();
            expr = new Logical(expr, operator, right);
        }

        return expr;
    }

    private Expr equality() {
        Expr expr = comparison();

        while (match(TokenType.EQUAL_EQUAL) || match(TokenType.BANG_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Logical(expr, operator, right);
        }

        return expr;
    }

    private Expr comparison() {
        Expr expr = addition();

        while (match(TokenType.GREATER) || match(TokenType.GREATER_EQUAL) || match(TokenType.LESS) || match(TokenType.LESS_EQUAL)) {
            Token operator = previous();
            Expr right = addition();
            expr = new Logical(expr, operator, right);
        }

        return expr;
    }

    private Expr addition() {
        Expr expr = multiplication();

        while (match(TokenType.MINUS) || match(TokenType.PLUS)) {
            Token operator = previous();
            Expr right = multiplication();
            expr = new Binary(expr, operator, right);
        }

        return expr;
    }


    private Expr multiplication() {
        Expr expr = unary();

        while (match(TokenType.SLASH) || match(TokenType.STAR)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr unary() {
        if (match(TokenType.BANG) || match(TokenType.MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Unary(operator, right);
        }
        return call();
    }

    private Expr finishCall(Expr callee) {
        return null;
    }

    private Expr call() {
        List<Expr> arguments = new ArrayList<>();
        Token paren = previous();                               //wahrscheinlich falsch aber egal
        Expr callee = primary();

        while (check(TokenType.RIGHT_PAREN) || check(TokenType.DOT)) {
            if (match(TokenType.RIGHT_PAREN))
                arguments.addAll(arguments());
            else {
                consume(TokenType.DOT, "Expect '.' after primary/argument list in call.");
                consume(TokenType.IDENTIFIER, "Expect identifier after '.' in call.");
            }
        }

        return new Call(callee, paren, arguments);

    }


    private List<Expr> arguments() {
        ArrayList<Expr> arguments = new ArrayList<>();
        arguments.add(expression());
        while (!match(TokenType.RIGHT_PAREN)) {
            consume(TokenType.COMMA, "Expect ',' or '(' after expression in arguments");
            arguments.add(expression());
        }
        return arguments;
    }

    private Expr primary() {
        switch (peek().type) {
            case STRING:
                if (peek().lexeme.equals("super")) {
                    advance();
                    consume(TokenType.DOT, "Expect '.' after super keyword.");
                    consume(TokenType.IDENTIFIER, "Expect identifier after '.' with call to super");
                    return new Variable(advance());
                }
                return new Variable(advance());
            case LEFT_PAREN:
                advance();
                Grouping grouping = new Grouping(expression());
                consume(TokenType.RIGHT_PAREN, "Grouping has to end with ')'");
                return grouping;
            case TRUE:
            case FALSE:
            case NIL:
            case NUMBER:
            case IDENTIFIER:
                return new Variable(advance());
            default:
                return null;
        }
    }

    private boolean match(TokenType... types) {
            for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();

        throw error(peek(), message);
    }

    private boolean check(TokenType tokenType) {
        if (isAtEnd()) return false;
        return peek().type == tokenType;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private ParseError error(Token token, String message) {
        ParserError.error(token, message);
        return new ParseError();
    }


}

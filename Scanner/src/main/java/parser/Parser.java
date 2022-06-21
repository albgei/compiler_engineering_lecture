package parser;

import scanner.*;
import parser.stmts.*;
import parser.expr.*;

import java.util.ArrayList;
import java.util.List;

import static scanner.TokenType.*;

public class Parser {

    private static class ParseError extends RuntimeException {
    }

    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Stmt> parse() {

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
            if (match(FUN)) return function();
            if (match(VAR)) return varDeclaration();

            return statement();
        } catch (ParseError error) {
            return null;
        }
    }

    private Stmt statement() {
        if (match(FOR)) return forStatement();
        if (match(IF)) return ifStatement();
        if (match(PRINT)) return printStatement();
        if (match(RETURN)) return returnStatement();
        if (match(WHILE)) return whileStatement();
        if (match(LEFT_BRACE)) return new Block(block());

        return expressionStatement();
    }

    private Stmt forStatement() {
        ArrayList<Stmt> block = new ArrayList<>();
        Expr increment;
        Expr condition;

        consume(LEFT_PAREN, "Expect '(' at the beginning of for head.");
        if (match(VAR)) {
            block.add(varDeclaration());
        } else if (!match(SEMICOLON)) {
            block.add(expressionStatement());
        } else {
            consume(SEMICOLON, "Expect ';' in for statement if not starts with variable");
        }

        condition = expression();
        consume(SEMICOLON, "Expect ';' after increment expression.");


        increment = expression();
        consume(RIGHT_PAREN, "Expect ')' at the end of for head.");

        List<Stmt> body = new ArrayList<>();
        body.add(statement());
        body.add(0, new Expression(increment));
        Stmt stmt = new Block(body);
        While whileStmt = new While(condition, stmt);

        block.add(whileStmt);
        return new Block(block);

    }

    private Stmt ifStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'if'.");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after if condition.");

        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if (match(ELSE)) {
            elseBranch = statement();
        }

        return new If(condition, thenBranch, elseBranch);
    }

    private Stmt printStatement() {
        Expr expr = expression();
        consume(SEMICOLON, "Expect ';' after print statement.");
        return new Print(expr);
    }

    private Stmt returnStatement() {
        if (match(SEMICOLON))
            return new Return(null);
        Expr expr = expression();
        consume(SEMICOLON, "Expect ';' after return statement.");
        return new Return(expr);
    }

    private Stmt varDeclaration() {
        Expr init = null;
        consume(IDENTIFIER, "Expect identifier after 'var'.");
        Token name = previous();
        if (match(EQUAL))
            init = expression();
        consume(SEMICOLON, "Expect ';' after variable declaration.");
        return new Var(name, init);
    }

    private Stmt whileStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'while'.");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after while expression.");
        Stmt body = statement();
        return new While(condition, body);
    }

    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(SEMICOLON, "Expect ';' after Expression");
        return new Expression(expr);
    }

    private Function function() {
        Token name;
        List<Stmt> body;
        consume(IDENTIFIER, "Expect identifier after 'fun'.");
        name = previous();
        consume(LEFT_PAREN, "Expect '(' after function identifier.");
        List<Token> parameters = parameters();
        consume(RIGHT_PAREN, "Expect ')' after function parameter list.");
        consume(LEFT_BRACE, "Expect '{' at the beginning of a code block.");
        body = block();
        return new Function(name, parameters, body);
    }

    private List<Token> parameters() {
        ArrayList<Token> parameters = new ArrayList<>();
        while (check(IDENTIFIER) || match(COMMA)) {
            if (match(IDENTIFIER)) {
                parameters.add(previous());
            }
        }
        return parameters;
    }

    private List<Stmt> block() {
        List<Stmt> block = new ArrayList<>();

        while (!check(RIGHT_BRACE)) {
            block.add(declaration());
        }
        consume(RIGHT_BRACE, "Expect '}' at the end of a block.");
        return block;
    }

    private Expr assignment() {
        Expr expr = or();
        if (match(EQUAL)) {
            Token equals = previous();
            Expr value = assignment();

            if (expr instanceof Variable) {
                Token name = ((Variable) expr).name;
                return new Assign(name, value);
            }
            ParserError.error(equals, "");
        }
        return expr;
    }

    private Expr or() {
        Expr expr = and();

        while (match(OR)) {
            Token operator = previous();
            Expr right = and();
            expr = new Logical(expr, operator, right);
        }

        return expr;
    }

    private Expr and() {
        Expr expr = equality();

        while (match(AND)) {
            Token operator = previous();
            Expr right = equality();
            expr = new Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr equality() {
        Expr expr = comparison();

        while (match(EQUAL_EQUAL) || match(BANG_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr comparison() {
        Expr expr = addition();

        while (match(GREATER) || match(GREATER_EQUAL) || match(LESS) || match(LESS_EQUAL)) {
            Token operator = previous();
            Expr right = addition();
            expr = new Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr addition() {
        Expr expr = multiplication();

        while (match(MINUS) || match(PLUS)) {
            Token operator = previous();
            Expr right = multiplication();
            expr = new Binary(expr, operator, right);
        }

        return expr;
    }


    private Expr multiplication() {
        Expr expr = unary();

        while (match(SLASH) || match(STAR)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr unary() {
        if (match(BANG) || match(MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Unary(operator, right);
        }
        return call();
    }

    private Expr call() {
        List<Expr> arguments;
        Expr callee = primary();
        if (match((LEFT_PAREN))) {
            arguments = arguments();
            consume(RIGHT_PAREN, "");
            return new Call(callee, arguments);
        }
        return callee;
    }


    private List<Expr> arguments() {
        ArrayList<Expr> arguments = new ArrayList<>();
        arguments.add(expression());
        while (!check(RIGHT_PAREN)) {
            consume(COMMA, "Expect ',' or '(' after expression in arguments");
            arguments.add(expression());
        }
        return arguments;
    }

    private Expr primary() {
        switch (peek().type) {
            case LEFT_PAREN:
                advance();
                Grouping grouping = new Grouping(expression());
                consume(RIGHT_PAREN, "Grouping has to end with ')'");
                return grouping;
            case TRUE:
            case FALSE:
            case NIL:
            case STRING:
            case NUMBER:
                return new Literal(advance().literal);
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
        return peek().type == EOF;
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

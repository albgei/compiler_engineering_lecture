package parser.expr;

import scanner.Token;

public class Unary extends Expr {
    public Unary(Token operator, Expr right) {
        this.operator = operator;
        this.right = right;
    }


    final Token operator;
    final Expr right;

    @Override
    public String print() {
        return "%s%s".formatted(operator.lexeme, right.print());
    }
}

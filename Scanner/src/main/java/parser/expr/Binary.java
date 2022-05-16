package parser.expr;

import scanner.Token;

public class Binary extends Expr {
    public Binary(Expr left, Token operator, Expr right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }


    final Expr left;
    final Token operator;
    final Expr right;

    @Override
    public String print() {
        return "(%s %s %s)".formatted(left.print(), operator.lexeme, right.print());
    }
}

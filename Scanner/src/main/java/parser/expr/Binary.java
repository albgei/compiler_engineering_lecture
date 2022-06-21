package parser.expr;

import scanner.Token;

public class Binary extends Expr {
    public Binary(Expr left, Token operator, Expr right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }


    public final Expr left;
    public final Token operator;
    public final Expr right;

    @Override
    public String print() {
        return "(%s %s %s)".formatted(left.print(), operator.lexeme, right.print());
    }

    @Override
    public <R> R accept(ExprVisitor<R> exprVisitor) {
        return exprVisitor.visitBinaryExpr(this);
    }
}

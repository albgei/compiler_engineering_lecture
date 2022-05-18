package parser.expr;

import scanner.Token;

public class Variable extends Expr {
    public Variable(Token name) {
        this.name = name;
    }


    public final Token name;

    @Override
    public String print() {
        return name.lexeme;
    }

    @Override
    public <R> R accept(ExprVisitor<R> exprVisitor) {
        return null;
    }
}

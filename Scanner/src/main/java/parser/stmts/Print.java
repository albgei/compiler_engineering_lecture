package parser.stmts;

import parser.expr.Expr;

public class Print extends Stmt {
    public Print(Expr expression) {
        this.expression = expression;
    }


    final Expr expression;

    @Override
    public String print() {
        return "(print %s)".formatted(expression.print());
    }
}

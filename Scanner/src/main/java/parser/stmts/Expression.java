package parser.stmts;


import parser.expr.Expr;

public class Expression extends Stmt {
    public Expression(Expr expression) {
        this.expression = expression;
    }

    final Expr expression;

    @Override
    public String print() {
        return expression.print();
    }
}

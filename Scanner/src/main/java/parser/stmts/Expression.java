package parser.stmts;


import parser.expr.Expr;

public class Expression extends Stmt {
    public Expression(Expr expression) {
        this.expression = expression;
    }

    public final Expr expression;

    @Override
    public String print() {
        return expression.print();
    }

    @Override
    public <R> R accept(StmtVisitor<R> stmtVisitor) {
        return null;
    }
}

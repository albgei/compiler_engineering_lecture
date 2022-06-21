package parser.stmts;

import parser.expr.Expr;

public class Print extends Stmt {
    public Print(Expr expression) {
        this.expression = expression;
    }


    public final Expr expression;

    @Override
    public String print() {
        return "(print %s)".formatted(expression.print());
    }

    @Override
    public <R> R accept(StmtVisitor<R> stmtVisitor) {
        return stmtVisitor.visitPrintStmt(this);
    }
}

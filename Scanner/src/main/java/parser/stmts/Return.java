package parser.stmts;

import parser.expr.Expr;

public class Return extends Stmt {
    public Return(Expr value) {
        this.value = value;
    }

    final Expr value;

    @Override
    public String print() {
        return "(return %s)".formatted(value.print());
    }

    @Override
    public <R> R accept(StmtVisitor<R> stmtVisitor) {
        return null;
    }
}

package parser.stmts;

import parser.expr.Expr;

public class While extends Stmt {
    public While(Expr condition, Stmt body) {
        this.condition = condition;
        this.body = body;
    }


    final Expr condition;
    final Stmt body;

    @Override
    public String print() {
        return "(while %s %s)".formatted(condition.print(), body.print());
    }
}

package parser.stmts;


import parser.expr.Expr;

public class If extends Stmt {
    public If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }


    final Expr condition;
    final Stmt thenBranch;
    final Stmt elseBranch;

    @Override
    public String print() {
        return "(if %s %s %s)".formatted(condition.print(), thenBranch.print(), elseBranch.print());
    }
}

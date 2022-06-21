package parser.stmts;

import parser.expr.Expr;
import scanner.Token;

public class Var extends Stmt {
    public Var(Token name, Expr initializer) {
        this.name = name;
        this.initializer = initializer;
    }

    public final Token name;
    public final Expr initializer;

    @Override
    public String print() {
        return "(= %s %s)".formatted(name.lexeme, initializer.print());
    }

    @Override
    public <R> R accept(StmtVisitor<R> stmtVisitor) {
        return stmtVisitor.visitVarStmt(this);
    }
}

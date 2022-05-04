package parser.stmts;

import parser.expr.Expr;
import scanner.Token;

public class Var extends Stmt {
    public Var(Token name, Expr initializer) {
        this.name = name;
        this.initializer = initializer;
    }

    final Token name;
    final Expr initializer;

    @Override
    public String print() {
        return "(= %s %s)".formatted(name.lexeme, initializer.print());
    }
}

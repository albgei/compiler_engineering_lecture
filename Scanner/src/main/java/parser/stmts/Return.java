package parser.stmts;

import parser.expr.Expr;
import scanner.Token;

public class Return extends Stmt {
    public Return(Token keyword, Expr value) {
        this.keyword = keyword;
        this.value = value;
    }

    final Token keyword;
    final Expr value;

    @Override
    public String print() {
        return "(return %s)".formatted(value.print());
    }
}

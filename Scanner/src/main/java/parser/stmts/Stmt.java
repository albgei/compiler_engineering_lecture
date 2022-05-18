package parser.stmts;

public abstract class Stmt {
    public abstract String print();
    public abstract <R> R accept(StmtVisitor<R> stmtVisitor);
}


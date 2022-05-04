package parser.expr;

public class Literal extends Expr {
    public Literal(Object value) {
        this.value = value;
    }


    final Object value;

    @Override
    public String print() {
        return String.valueOf(value);
    }
}

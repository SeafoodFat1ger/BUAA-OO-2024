package expr;

public class ExprFactor implements Factor {
    private final Expr expr;
    private final int exp;

    public ExprFactor(Expr expr, int exp) {
        this.expr = expr;
        this.exp = exp;
    }

    public Poly toPoly() {
        Poly polyExpr = expr.toPoly();
        Poly poly = new Poly();
        poly = poly.pow(polyExpr, exp);
        return poly;
    }
}

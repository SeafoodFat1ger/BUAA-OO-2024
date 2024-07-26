public class DxFactor implements Factor {
    private Expr expr;

    public DxFactor(Expr expr) {
        this.expr = expr;
    }

    @Override
    public Poly toDiff() {
        return expr.toDiff().toDiff();
    }

    @Override
    public Poly toPoly() {
        return expr.toDiff();
    }
}

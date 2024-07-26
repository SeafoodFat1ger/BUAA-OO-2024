import java.math.BigInteger;

public class ExprFactor implements Factor {
    private final Expr expr;
    private final BigInteger exp;

    public ExprFactor(Expr expr, BigInteger exp) {
        this.expr = expr;
        this.exp = exp;
    }

    @Override
    public Poly toDiff() {
        if (exp.equals(BigInteger.ZERO)) {
            Mono mono = new Mono(BigInteger.ZERO, BigInteger.ZERO);
            Poly poly = new Poly();
            poly.addMono(mono);
            return poly;
        }

        Poly polyExpr = expr.toPoly();
        Poly subOne = new Poly();
        subOne = subOne.pow(polyExpr, exp.add(BigInteger.valueOf(-1)));


        NumberFactor num = new NumberFactor(exp);
        Poly numPoly = num.toPoly();

        Poly diffPoly = expr.toDiff();

        Poly poly = Poly.mul(numPoly, subOne);
        poly = Poly.mul(poly, diffPoly);

        return poly;
    }

    @Override
    public Poly toPoly() {
        Poly polyExpr = expr.toPoly();
        Poly poly = new Poly();
        poly = poly.pow(polyExpr, exp);
        return poly;
    }
}

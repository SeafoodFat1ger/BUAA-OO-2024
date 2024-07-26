import java.math.BigInteger;

public class ExpFactor implements Factor {

    private Factor base;
    private BigInteger exp;

    public ExpFactor(Factor factor, BigInteger exp) {
        this.base = factor;
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

        Poly thisPoly = toPoly();

        NumberFactor num = new NumberFactor(exp);
        Poly numPoly = num.toPoly();

        Poly diffPoly = base.toDiff();

        Poly poly = Poly.mul(numPoly, thisPoly);
        poly = Poly.mul(poly, diffPoly);

        return poly;
    }

    @Override
    public Poly toPoly() {
        Poly polyEexp = base.toPoly();
        Poly polyBase = Poly.mulNum(polyEexp, exp);
        Mono mono = new Mono(BigInteger.ZERO, BigInteger.ONE, polyBase);
        Poly poly = new Poly();
        poly.addMono(mono);
        return poly;
    }
}

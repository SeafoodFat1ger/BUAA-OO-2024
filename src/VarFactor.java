import java.math.BigInteger;

public class VarFactor implements Factor {
    private final BigInteger exp;
    private final String name;

    public VarFactor(String name, int exp) {
        this.name = name;
        BigInteger big = BigInteger.valueOf(exp);
        this.exp = big;
    }

    public VarFactor(String name, BigInteger exp) {
        this.name = name;
        this.exp = exp;
    }

    @Override
    public Poly toDiff() {
        BigInteger big = exp.add(BigInteger.valueOf(-1));
        Mono mono = new Mono(big, BigInteger.ONE);
        Poly xxPoly = new Poly();
        xxPoly.addMono(mono);

        NumberFactor num = new NumberFactor(exp);
        Poly numPoly = num.toPoly();

        Poly poly = new Poly();
        poly = Poly.mul(numPoly, xxPoly);

        return poly;
    }

    @Override
    public Poly toPoly() {
        Mono mono = new Mono(exp, BigInteger.ONE);
        Poly poly = new Poly();
        poly.addMono(mono);
        return poly;
    }
}

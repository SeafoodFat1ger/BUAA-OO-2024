package expr;

import java.math.BigInteger;

public class VarFactor implements Factor {
    private final int exp;

    public VarFactor(int exp) {
        this.exp = exp;
    }

    public Poly toPoly() {
        Mono mono = new Mono(exp, BigInteger.ONE);
        Poly poly = new Poly();
        poly.addMono(mono);
        return poly;
    }
}

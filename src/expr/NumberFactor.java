package expr;

import java.math.BigInteger;

public class NumberFactor implements Factor {
    private final BigInteger num;

    public NumberFactor(BigInteger num) {
        this.num = num;
    }

    public String toString() {
        return this.num.toString();
    }

    public Poly toPoly() {
        Mono mono = new Mono(0, num);
        Poly poly = new Poly();
        poly.addMono(mono);
        return poly;
    }

}

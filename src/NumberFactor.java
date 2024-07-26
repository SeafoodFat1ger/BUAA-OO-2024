import java.math.BigInteger;

public class NumberFactor implements Factor {
    private final BigInteger num;

    public NumberFactor(int oldnum) {
        this.num = new BigInteger(String.valueOf(oldnum));
    }

    public NumberFactor(BigInteger num) {
        this.num = num;
    }

    public String toString() {
        return this.num.toString();
    }

    @Override
    public Poly toDiff() {
        Mono mono = new Mono(BigInteger.ZERO, BigInteger.ZERO);
        Poly poly = new Poly();
        poly.addMono(mono);
        return poly;
    }

    @Override
    public Poly toPoly() {
        Mono mono = new Mono(BigInteger.ZERO, num);
        Poly poly = new Poly();
        poly.addMono(mono);
        return poly;
    }

}

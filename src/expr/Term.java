package expr;

import java.util.ArrayList;

public class Term {
    private final ArrayList<Factor> factors;

    public Term() {
        this.factors = new ArrayList<>();
    }

    public void addFactor(Factor factor) {
        this.factors.add(factor);
    }

    public Poly toPoly() {
        Poly poly = new Poly();
        for (Factor factor : factors) {
            Poly tmp = factor.toPoly();
            poly = poly.mul(tmp);
        }
        return poly;
    }
}

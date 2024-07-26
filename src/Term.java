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
        if (factors.isEmpty()) {
            return poly;
        }
        poly = factors.get(0).toPoly();
        for (int i = 1; i < factors.size(); i++) {
            Poly tmp = factors.get(i).toPoly();
            poly = Poly.mul(poly, tmp);
        }
        return poly;
    }

    public Poly toDiff() {
        Poly poly = new Poly();
        if (factors.isEmpty()) {
            return poly;
        }
        for (int i = 0; i < factors.size(); i++) {
            Poly tmp = factors.get(i).toDiff();

            for (int j = 0; j < factors.size(); j++) {
                if (i != j) {
                    tmp = Poly.mul(tmp, factors.get(j).toPoly());
                }
            }
            poly = Poly.add(poly, tmp);
        }
        return poly;
    }

}

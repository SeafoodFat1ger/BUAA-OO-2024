import java.util.ArrayList;

public class Expr implements Factor {
    private final ArrayList<Term> terms;

    public Expr() {
        this.terms = new ArrayList<>();
    }

    public void addTerm(Term term) {
        this.terms.add(term);
    }

    public Poly toPoly() {
        Poly poly = new Poly();

        if (terms.isEmpty()) {
            return poly;
        }
        poly = terms.get(0).toPoly();
        for (int i = 1; i < terms.size(); i++) {
            Poly tmp = terms.get(i).toPoly();
            poly = Poly.add(poly, tmp);
        }

        return poly;
    }

    public Poly toDiff() {
        Poly poly = new Poly();

        if (terms.isEmpty()) {
            return poly;
        }
        poly = terms.get(0).toDiff();
        for (int i = 1; i < terms.size(); i++) {
            Poly tmp = terms.get(i).toDiff();
            poly = Poly.add(poly, tmp);
        }

        return poly;
    }
}

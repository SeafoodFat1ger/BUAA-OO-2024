package expr;

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
        for (Term term : terms) {
            Poly tmp = term.toPoly();
            poly = poly.add(tmp);
        }
        return poly;
    }
}

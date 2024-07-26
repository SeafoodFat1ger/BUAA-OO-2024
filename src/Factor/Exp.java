package Factor;

public class Exp implements Factor {
    private Factor factor;

    public Exp(Factor factor) {
        this.factor = factor;
    }

    @Override
    public String toString() {
        return "exp(" + factor.toString() + ")";
    }

    @Override
    public Factor derive() {
        Term term = new Term();
        Factor newfactor = factor.derive();
        term.addFactor(this.clone());
        term.addFactor(newfactor);

        return term;
    }

    @Override
    public Factor clone() {
        return new Exp(factor.clone());
    }
}

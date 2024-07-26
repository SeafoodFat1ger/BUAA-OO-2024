import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class Expr implements Factor {

    private ArrayList<Term> terms = new ArrayList<>();
    private BigInteger exp = BigInteger.ONE;
    private boolean isNeg = false;

    private static final BigInteger ONE = BigInteger.ONE;

    public Expr() {}

    public Expr(Poly poly) {
        terms.addAll(poly.toTerms());
    }

    @Override
    public void Negate() { isNeg = !isNeg; }

    @Override
    public void setExp(BigInteger exp) { this.exp = exp; }

    public void addTerm(Term term) { terms.add(term); }

    public void addExpr(Expr expr) {
        this.terms.addAll(expr.terms);
    }

    public Poly extend() {
        Poly res = new Poly();
        Poly one = new Poly();
        one.createOne();
        if (exp.equals(BigInteger.ZERO)) {
            if (isNeg) { one.Negate(); }
            return one;
        }
        for (Term term : terms) {
            //System.out.println(terms.size());
            //System.out.println("tetst1"+term.extend());
            res.add(term.extend());
        }
        res.Merge();
        for (BigInteger i = ONE; i.compareTo(exp) <= 0; i = i.add(ONE)) {
            one.multiExpr(res);
        }
        one.Merge();
        if (isNeg) { one.Negate(); }
        return one;
    }

    public boolean equals(Expr expr) {
        String self = new String(this.extend().toString().getBytes(StandardCharsets.UTF_8));
        String other = new String(expr.extend().toString().getBytes(StandardCharsets.UTF_8));
        return (self.equals(other));
    } // 为三角函数化简作准备，equals挖了大坑

    public void replace(HashMap<Unit.Type,Factor> map) {
        for (Term term : terms) {
            term.replace(map);
            //System.out.println("New Term "+term.extend());
        }
        //System.out.println(terms.size());
    }

    public Expr pow(BigInteger exponent) {
        for (Term term : terms) {
            term.pow(exponent);
        }
        return this;
    }

    public Expr clone() {
        Expr expr = new Expr();
        expr.setExp(this.exp);
        for (Term term : terms) {
            expr.terms.add(term.clone());
        }
        expr.isNeg = this.isNeg;
        return expr;
    }

    @Override
    public Poly getDerivative(Unit.Type type) {
        return this.extend().getDerivative(type);
    }
}

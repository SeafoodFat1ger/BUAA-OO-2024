import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class Exp {
    private BigInteger exponent = BigInteger.ONE;
    private Factor factor;
    private Poly poly;
    private Expr trans;

    private boolean modified = false;

    public Exp(Factor factorIn) {
        //System.out.println("factor in Exp " + ((Expr)factorIn).extend().toString());
        this.factor = factorIn;
        //System.out.println("factor in Exp " + ((Expr)factor).extend().toString());
        if (factor instanceof Unit) {
            Expr expr = new Expr();
            Term term = new Term();
            term.addFactor(factor);
            expr.addTerm(term);
            trans = expr;
        } else if (factor instanceof Expr) {
            trans = (Expr) factor;
            //System.out.println(trans.extend().toString());
        }
        if (trans != null) {
            poly = trans.extend();
        }
        //System.out.println("poly initial "+poly);
    }

    public Exp multExp(Exp exp) {
        //System.out.println(this.trans.extend());
        //System.out.println(exp.trans.extend());
        //System.out.println("exp Unit bfa "+exp.poly);
        this.poly.add((exp.clone()).poly);
        //System.out.println("exp Unit "+exp.poly);
        this.poly.Merge();
        //System.out.println(this.trans.extend());
        this.factor = trans;
        this.modified = true;
        return this;
    }

    public void pow(BigInteger exponent) {
        this.poly = trans.extend();
        //System.out.println("poly before pow "+poly);
        //System.out.println("exponent "+exponent);
        //this.trans.pow(exponent);
        Expr expr = new Expr();
        Term term  = new Term();
        term.addFactor(new Unit(exponent));
        term.addFactor(this.trans);
        expr.addTerm(term);
        this.trans = expr;
        //System.out.println(trans.extend());
        this.factor = trans;
        //this.poly = poly.pow(exponent);
        this.poly = trans.extend();
        //System.out.println("poly in Exp "+poly);
        this.exponent = BigInteger.ONE;
    }

    public void setExponent(BigInteger exponent) {
        this.exponent = exponent;
    }

    public BigInteger getExponent() { return exponent; }

    public void replace(HashMap<Unit.Type, Factor> map) {
        if (factor instanceof Unit) {
            Expr expr = new Expr();
            Term term = new Term();
            term.addFactor(factor);
            expr.addTerm(term);
            expr.replace(map);
            factor = expr;
            this.trans = expr;
            this.poly = expr.extend();
        } else {
            ((Expr)factor).replace(map);
            this.trans = (Expr)factor;
            this.poly = trans.extend();
        }
    }

    public boolean equals(Exp e) {
        String self = new String(this.poly.toString().getBytes(StandardCharsets.UTF_8));
        String other = new String(e.poly.toString().getBytes(StandardCharsets.UTF_8));
        return self.equals(other);
    }

    public int compareTo(Exp e) {
        String self = new String(this.poly.toString().getBytes(StandardCharsets.UTF_8));
        String other = new String(e.poly.toString().getBytes(StandardCharsets.UTF_8));
        return self.compareTo(other);
        //return this.poly.compareTo(e.poly);
    }

    public BigInteger ifOne() {
        if (exponent.equals(BigInteger.ZERO)) { return null; }
        if (factor instanceof Unit) {
            Unit unit = (Unit) factor;
            unit.wipeExp();
            if (unit.getCoff().equals(BigInteger.ZERO)) {
                return BigInteger.ONE;
            }
        } else {
            if (poly.isEmpty()) {
                return BigInteger.ONE;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("exp(");
        if (poly.isSimple()) {
            String s = poly.toString();
            sb.append(s);
        } else {
            String s = poly.toString();
            sb.append("(").append(s).append(")");
        }
        sb.append(")");
        if (exponent.compareTo(BigInteger.ONE) > 0) {
            sb.append("^").append(exponent);
        }
        return sb.toString();
    }

    public Exp clone() {
        Exp exp;
        if (this.modified) {
            exp = new Exp(null);
        } else {
            if (factor != null) {
                exp = new Exp(factor.clone());
            } else {
                exp = new Exp(null);
            }
        }
        exp.exponent = this.exponent;
        exp.poly = this.poly.clone();
        return exp;
    }

    public boolean isSimple() { return this.poly.isSimple(); }

    public Poly getDerivative(Unit.Type type) {
        final Poly res = new Poly();
        if (exponent.equals(BigInteger.ZERO)) {
            return res;
        }
        Unit unit = new Unit(Unit.Type.EXP);
        unit.setExpFunc(this);
        res.addUnit(unit);
        res.multiExpr(this.poly.getDerivative(type));
        /*if (exponent.compareTo(BigInteger.ONE) > 0) {
            Exp exp = this.clone();
            exp.pow(exponent.add(BigInteger.ONE.negate()));
            res.multiUnit(new Unit(exponent));
            Unit u = new Unit(Unit.Type.EXP);
            u.setExpFunc(exp);
            res.multiUnit(u);
        }*/
        //System.out.println(res);
        return res;
    }
}

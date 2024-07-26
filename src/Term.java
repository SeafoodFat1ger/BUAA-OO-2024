import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

public class Term {

    private ArrayList<Factor> factors = new ArrayList<>();
    private boolean isNeg;

    public void Negate() {
        isNeg = !isNeg;
    }

    public void addFactor(Factor factor) {
        factors.add(factor);
    }

    public Poly extend() { //eg: 2*x*(2+x) -> Unit*Unit*Expr
        Poly res = new Poly();
        res.createOne();
        //System.out.println(factors.size());
        //final ArrayList<Factor> newFactors = (ArrayList<Factor>) factors.clone();
        for (Factor factor : factors) {
            if (factor instanceof Unit) { //是基元
                //System.out.println("Term f "+factor);
                res.multiUnit(((Unit) factor));
                //System.out.println("res "+res);
            } else { //是Expr
                //System.out.println("Term E " + ((Expr)factor).extend());
                res.multiExpr((((Expr) factor)).extend());
            }
        }
        if (isNeg) {
            res.Negate();
        }
        return res;
    }

    public void replace(HashMap<Unit.Type, Factor> map) {
        ArrayList<Factor> newFactors = new ArrayList<>();
        for (Factor factor : factors) {
            if (factor instanceof Unit) {
                //System.out.println("origin f " + factor);
                HashMap.Entry<Factor, BigInteger> virtualReal = ((Unit) factor).replace(map);
                if (virtualReal != null) {
                    if (virtualReal.getKey() instanceof Unit) {
                        if (((Unit) virtualReal.getKey()).getType() == Unit.Type.EXP) {
                            Unit replace = ((Unit) virtualReal.getKey()).clone();
                            replace.setExp(virtualReal.getValue());
                            newFactors.add(replace);
                        } else {
                            for (BigInteger i = BigInteger.ZERO;
                                 i.compareTo(virtualReal.getValue()) < 0;
                                 i = i.add(BigInteger.ONE)) {
                                //System.out.println("New " + virtualReal.getKey());
                                newFactors.add(virtualReal.getKey());
                            }
                        }
                    } else {
                        for (BigInteger i = BigInteger.ZERO;
                             i.compareTo(virtualReal.getValue()) < 0;
                             i = i.add(BigInteger.ONE)) {
                            //System.out.println("New " + virtualReal.getKey());
                            newFactors.add(virtualReal.getKey());
                        }
                    }
                } else {
                    //System.out.println("New "+factor);
                    newFactors.add(factor);
                }
            } else {
                ((Expr) factor).replace(map);
                //System.out.println("New "+((Expr) factor).extend());
                newFactors.add(factor.clone());
            }
        }
        factors = newFactors;
        /*for (Factor factor : factors) {
            if (factor instanceof Unit) {
                //System.out.println("New f " + factor);
            } else {
                // System.out.println("New f "+((Expr)factor).extend());
            }
        }*/
    }

    public void pow(BigInteger exponent) {
        Expr expr = new Expr();
        Term term = new Term();
        term.addFactor(new Unit(exponent));
        expr.addTerm(term);
        factors.add((expr));
    }

    public Term clone() {
        Term term = new Term();
        for (Factor factor : factors) {
            term.factors.add(factor.clone());
        }
        term.isNeg = this.isNeg;
        return term;
    }
}

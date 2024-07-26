//Unit,即基元，表现形式为a*x^b

import java.math.BigInteger;
import java.util.HashMap;
import java.util.StringJoiner;

public class Unit implements Factor, Comparable<Unit> {

    public enum Type { X, Y, Z, CONST, EXP; }

    private BigInteger coff;
    private Type type;//判断x还是常数
    private Exp expFunc;
    private boolean expWiped = false;
    private BigInteger[] exp = {BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO};

    public Unit(BigInteger coff) {
        this.coff = coff;
        this.type = Type.CONST;
        this.expFunc = null;
    }

    public Unit(Type type) {
        this.coff = BigInteger.ONE;
        this.type = type;
        exp[0] = type == Type.X ? BigInteger.ONE : BigInteger.ZERO;
        exp[1] = type == Type.Y ? BigInteger.ONE : BigInteger.ZERO;
        exp[2] = type == Type.Z ? BigInteger.ONE : BigInteger.ZERO;
        this.expFunc = null;
    }

    public Unit(Type type, Factor factor) {
        this.coff = BigInteger.ONE;
        this.type = type;
        //System.out.println(((Expr)factor).extend().toString());
        this.expFunc = new Exp(factor);
    }

    public void pow(BigInteger exponent) {
        this.coff = coff.multiply(exponent);
    }

    @Override
    public void Negate() {
        this.coff = coff.negate();
    }

    @Override
    public void setExp(BigInteger exp) {
        this.exp[0] = type == Type.X ? exp : BigInteger.ZERO;
        this.exp[1] = type == Type.Y ? exp : BigInteger.ZERO;
        this.exp[2] = type == Type.Z ? exp : BigInteger.ZERO;
        if (type == Type.EXP) {
            //expFunc.setExponent(exp);
            if (exp.compareTo(BigInteger.ONE) > 0) {
                if (expFunc != null) {
                    expFunc.pow(exp);
                }
            } else if (exp.equals(BigInteger.ZERO)) {
                expFunc = null;
            }
            //System.out.println(expFunc);
        }
    }

    public void multiUnit(Unit unit) {
        this.coff = this.coff.multiply(unit.coff);
        this.exp[0] = this.exp[0].add(unit.exp[0]);
        this.exp[1] = this.exp[1].add(unit.exp[1]);
        this.exp[2] = this.exp[2].add(unit.exp[2]);
        if (expFunc != null && unit.expFunc != null) {
            //System.out.println("exp unit bf m "+expFunc.toString());
            this.expFunc.multExp(unit.expFunc);
        } else if (expFunc == null && unit.getExpFunc() != null) {
            this.expFunc = unit.clone().expFunc;
        }
        //System.out.println(coff + " " + exp);
    } //乘其他Unit

    public void setCoff(BigInteger coff) {
        this.coff = coff;
    }

    public BigInteger getCoff() {
        return coff;
    }

    public BigInteger[] getExp() {
        return exp;
    }

    public Type getType() {
        return type;
    }

    public void setExpFunc(Exp exp) {
        this.expFunc = exp;
    }

    public Exp getExpFunc() {
        return expFunc;
    }

    public boolean canMerge(Unit unit) {
        return this.compareTo(unit) == 0;
    }

    public void merge(Unit unit) {
        this.coff = coff.add(unit.coff);
    }

    @Override
    public int compareTo(Unit unit) {
        if (this.exp[0].compareTo(unit.exp[0]) != 0) {
            return this.exp[0].compareTo(unit.exp[0]);
        } else if (this.exp[1].compareTo(unit.exp[1]) != 0) {
            return this.exp[1].compareTo(unit.exp[1]);
        } else if (this.exp[2].compareTo(unit.exp[2]) != 0) {
            return this.exp[2].compareTo(unit.exp[2]);
        }
        if (this.expFunc == null && unit.expFunc == null) {
            //System.out.println("Yes");
            return 0;
        } else if (this.expFunc == null) {
            return -1;
        } else if (unit.expFunc == null) {
            return 1;
        } else {
            /*if (this.expFunc.equals(unit.expFunc)) {
                return 0;
            }
            else {
                return this.expFunc.isSimple() ? -1 : 1;
            }*/
            return this.expFunc.compareTo(unit.expFunc);
        }
    }

    public HashMap.Entry<Factor, BigInteger> replace(HashMap<Type, Factor> map) {
        if (type == Type.CONST) {
            return null;
        } else if (type == Type.EXP) {
            expFunc.replace(map);
        }
        BigInteger exponent = exp[0].add(exp[1]).add(exp[2]);
        if (exponent.equals(BigInteger.ZERO)) {
            return null;
        }
        return new HashMap.SimpleImmutableEntry<>(map.get(type), exponent);
    }

    public void wipeExp() {
        if (expWiped) {
            return;
        }
        expWiped = true;
        if (expFunc == null) {
            return;
        }
        BigInteger res = expFunc.ifOne();
        if (res == null) {
            return;
        }
        if (res.equals(BigInteger.ONE)) {
            expFunc = null;
        }
    }

    public boolean isSimple() {
        BigInteger sum = exp[0].add(exp[1]).add(exp[2]);
        if (sum.equals(BigInteger.ZERO) && expFunc == null) {
            return true;
        }
        if (!coff.equals(BigInteger.ONE)) {
            return false;
        }
        if (sum.equals(BigInteger.ZERO) && expFunc.getExponent().equals(BigInteger.ONE)) {
            return true;
        }
        if (expFunc == null && exp[0].equals(BigInteger.ZERO)
                && exp[1].equals(BigInteger.ZERO)) {
            return true;
        }
        if (expFunc == null && exp[0].equals(BigInteger.ZERO)
                && exp[2].equals(BigInteger.ZERO)) {
            return true;
        }
        if (expFunc == null && exp[1].equals(BigInteger.ZERO)
                && exp[2].equals(BigInteger.ZERO)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        StringJoiner sj;
        BigInteger coefficient;
        BigInteger exponent = exp[0].add(exp[1]).add(exp[2]);
        //deal with coff
        coefficient = (coff.compareTo(BigInteger.ZERO) > 0) ? coff : coff.negate();
        if (coff.compareTo(BigInteger.ZERO) > 0) {
            sj = new StringJoiner("*", "+", "");
        } else {
            sj = new StringJoiner("*", "-", "");
        }
        if (!coefficient.equals(BigInteger.ONE) ||
                (exponent.equals(BigInteger.ZERO) && expFunc == null)) {
            sj.add(coefficient.toString());
        }
        for (int i = 0; i < 3; i++) {
            char c = (char) ('x' + i);
            if (exp[i].equals(BigInteger.ONE)) {
                sj.add(String.valueOf(c));
            } else if (!exp[i].equals(BigInteger.ZERO)) {
                sj.add(c + "^" + exp[i]);
            }
        }
        if (expFunc != null) {
            sj.add(expFunc.toString());
        }
        return sj.toString();
    }

    public Unit clone() {
        Unit unit = new Unit(Type.CONST);
        if (this.expFunc != null) {
            unit.expFunc = expFunc.clone();
        }
        unit.setCoff(this.coff);
        for (int i = 0; i < 3; i++) {
            unit.exp[i] = new BigInteger(String.valueOf(BigInteger.ZERO.add(exp[i])));
        }
        unit.setType(this.type);
        return unit;
    }

    public Unit simpleClone() {
        Unit unit = new Unit(Type.X);
        unit.setExp(this.exp[0]);
        return unit;
    }

    @Override
    public Poly getDerivative(Type type) {
        final Poly res = new Poly();
        int index = type.ordinal();
        Unit u = this.clone();
        if (this.exp[index].compareTo(BigInteger.ZERO) > 0) {
            u.coff = u.coff.multiply(this.exp[index]);
            u.exp[index] = this.exp[index].add(BigInteger.ONE.negate());
            res.addUnit(u);
        }
        if (this.expFunc != null) {
            Poly newPoly = new Poly();
            u = this.simpleClone();
            u.expFunc = null;
            newPoly.addUnit(u);
            //System.out.println(newPoly);
            newPoly.multiExpr(this.expFunc.getDerivative(type));
            res.add(newPoly);
        }
        return res;
    }

    private void setType(Type type) {
        this.type = type;
    }
}

package expr;

import java.math.BigInteger;

public class Mono {
    private BigInteger cof;
    private int exp;

    public Mono(int exp, BigInteger cof) {
        this.cof = cof;
        this.exp = exp;
        //System.out.println(cof);
        //System.out.println(exp);
    }
    public Mono mul(Mono mulOne) {
        int newExp = exp + mulOne.getExp();
        BigInteger newCof = cof.multiply(mulOne.getCof());
        Mono mono = new Mono(newExp, newCof);
        return mono;
    }

    public static boolean sameWith(Mono mono1, Mono mono2) {
        //不化简了 后期还要改？
        if (mono1.getExp() == mono2.getExp()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        //指数是0
        if (exp == 0) {
            return cof.toString();
        }
        //系数是0/-1/1/>1
        if (cof.equals(BigInteger.ZERO)) {
            return "0";
        } else if (cof.equals(BigInteger.ONE)) {
            return xToString(exp);
        } else if (cof.equals(BigInteger.valueOf(-1))) {
            //这个未免有点泰国奇怪？
            sb.append("-");
            sb.append(xToString(exp));
            return sb.toString();
        } else {
            sb.append(cof);
            sb.append("*");
            sb.append(xToString(exp));
            return sb.toString();
        }
    }

    public String xToString(int exp) {
        //等于0应该用不到吧。。。。
        if (exp == 0) {
            return "1";
        } else if (exp == 1) {
            return "x";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("x^");
            sb.append(exp);
            return sb.toString();
        }
    }

    public boolean isPositive() {
        //cof = 0 已经处理过了 不用管了
        return cof.compareTo(BigInteger.ZERO) > 0;
    }

    //////////////////////////////////////////////////////////////////

    public void setCof(BigInteger cof) {
        this.cof = cof;
    }

    public BigInteger getCof() {
        return cof;
    }

    public int getExp() {
        return exp;
    }
}

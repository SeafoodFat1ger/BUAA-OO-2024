import java.math.BigInteger;
import java.util.Objects;

public class Mono implements Cloneable {
    private BigInteger cof;
    private BigInteger exp;//imsb

    private Poly expBase;

    public Poly toDiff() {

        if (cof.equals(BigInteger.ZERO)) {
            return new Poly();
        }

        if (exp.equals(BigInteger.ZERO)) {
            Mono clone3 = (Mono) this.myClone();
            Poly origin = new Poly();
            origin.addMono(clone3);
            Poly baseDiff = expBase.toDiff();
            return Poly.mul(origin, baseDiff);

        }

        Mono clone1 = (Mono) this.myClone();
        clone1.setCof(cof.multiply(exp));
        clone1.setExp(exp.add(BigInteger.valueOf(-1)));

        Poly front = new Poly();
        front.addMono(clone1);

        Mono clone2 = (Mono) this.myClone();

        Poly back = new Poly();
        back.addMono(clone2);
        Poly baseDiff = expBase.toDiff();
        back = Poly.mul(back, baseDiff);

        return Poly.add(front, back);
    }

    public Mono(BigInteger exp, BigInteger cof) {
        this.cof = cof;
        this.exp = exp;
        this.expBase = new Poly();
        //this.EexpFactor = new NumberFactor(BigInteger.ZERO);
        //System.out.println(cof);
        //System.out.println(exp);
    }

    public Mono(BigInteger exp, BigInteger cof, Poly expBase) {
        this.cof = cof;
        this.exp = exp;
        this.expBase = expBase;
        //System.out.println(cof);
        //System.out.println(exp);
    }

    public Object myClone() {
        return new Mono(this.getExp(), this.getCof(), (Poly) this.getExpBase().myClone());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Mono)) {
            return false;
        }
        Mono mono = (Mono) o;
        return getExp().equals(mono.getExp()) && Objects.equals(getExpBase(), mono.getExpBase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getExp(), getExpBase());
    }

    public static Mono mul(Mono m1, Mono m2) {

        BigInteger newExp = m1.getExp().add(m2.getExp());
        BigInteger newCof = m1.getCof().multiply(m2.getCof());

        Poly newExpBase = Poly.add(m1.getExpBase(), m2.getExpBase());

        Mono mono = new Mono(newExp, newCof, newExpBase);
        return mono;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        //只有系数
        if (isOnlyCof()) {
            //System.out.println("**********");
            return cof.toString();
        }

        //系数是0/-1/1/>1
        String str = xAndExpToString();
        //System.out.println(str);
        if (cof.equals(BigInteger.ZERO)) {
            return "0";
        } else if (cof.equals(BigInteger.ONE)) {
            return str;

        } else if (cof.equals(BigInteger.valueOf(-1))) {
            //这个未免有点泰国奇怪？
            sb.append("-");
            sb.append(str);
            return sb.toString();
        } else {
            sb.append(cof);
            sb.append("*");
            sb.append(str);
            return sb.toString();
        }
    }

    //0 exp((exp((x-x^2))*x+exp((x-x^2))*x*3))*3*x+3*x^5*exp(0)^0
    //exp((x-x+x^2-x^2))^0
    //exp((exp(exp(exp(exp((exp(exp(exp(exp((exp(exp(exp(
    // ((exp(exp(exp(0)^0)^0)^0)^0)^0)^0)^0)^0)^0)^0)^0)^0)^0)^0)^0)^0)^0)^0)^0)^0
    //exp((4*x))
    //exp(x)^4
    //exp((40*x+60*x^2))
    //exp((40*x+60*x^2+10*x^3))
    //exp((x*3-2*x^2))*exp((-3*x))*2

    public String xToString() {
        //等于0应该用不到吧。。。。
        if (exp.equals(BigInteger.ZERO)) {
            return "1";
        } else if (exp.equals(BigInteger.ONE)) {
            return "x";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("x^");
            sb.append(exp);
            return sb.toString();
        }
    }

    public String expToString() {
        //已经判断过了
        if (expBase.getMonos().isEmpty()) {
            return "1";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("exp(");

        BigInteger expExp = BigInteger.ONE;

        boolean flag = expBase.isSimple();

        if (flag) {
            Mono mono = expBase.getMonos().get(0);
            if (mono.isOnlyCof()) {
                sb.append(expBase.toString());
            } else {
                Mono newMono = new Mono(mono.getExp(), BigInteger.ONE, mono.getExpBase());
                Poly newPoly = new Poly();
                newPoly.addMono(newMono);
                sb.append(newPoly.toString());
                expExp = mono.getCof();
            }

        } else {
            sb.append("(");
            sb.append(expBase.toString());
            sb.append(")");
        }
        sb.append(")");


        if (flag && (!expExp.equals(BigInteger.ONE))) {
            sb.append("^");
            sb.append(expExp);
        }
        return sb.toString();
    }

    public String xAndExpToString() {
        if (isOnlyX()) {
            return xToString();
        } else if (isOnlyExp()) {
            return expToString();
        } else {
            return xToString() + "*" + expToString();
        }
    }

    ////////////////////////////////////////
    public boolean isPositive() {
        //cof = 0 已经处理过了 不用管了
        return cof.compareTo(BigInteger.ZERO) > 0;
    }

    public boolean isZero() {
        return cof.equals(BigInteger.ZERO);
    }

    public boolean isOnlyX() {
        return expBase.getMonos().isEmpty();
    }

    public boolean isOnlyExp() {
        return exp.equals(BigInteger.ZERO);
    }

    public boolean isOnlyCof() {
        return isOnlyX() && isOnlyExp();
    }

    //////////////////////////////////////////////////////////////////
    public void setCof(BigInteger cof) {
        this.cof = cof;
    }

    public BigInteger getCof() {
        return cof;
    }

    public BigInteger getExp() {
        return exp;
    }

    public Poly getExpBase() {
        return expBase;
    }

    public void setExp(BigInteger exp) {
        this.exp = exp;
    }
}

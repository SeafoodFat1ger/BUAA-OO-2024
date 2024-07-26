package expr;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;

public class Poly {

    private ArrayList<Mono> monos;

    public Poly() {
        this.monos = new ArrayList<>();
    }

    public void addMono(Mono mono) {
        monos.add(mono);
    }

    public Poly add(Poly addOne) {
        Poly poly = new Poly();
        for (Mono mono : monos) {
            poly.addMono(mono);
        }
        for (Mono mono : addOne.getMonos()) {
            poly.addMono(mono);
        }
        poly.simplify();

        return poly;
    }

    public Poly mul(Poly mulOne) {
        Poly poly = new Poly();
        //如果是空的？"x "
        if (monos.isEmpty()) {
            return mulOne;
        }
        if (mulOne.getMonos().isEmpty()) {
            return this;
        }
        for (Mono mono : mulOne.getMonos()) {
            for (Mono monoo : monos) {
                poly.addMono(mono.mul(monoo));
            }
        }
        poly.simplify();

        return poly;
    }

    public Poly pow(Poly polyExpr, int exp) {
        Poly poly = new Poly();
        // 0 次？
        if (exp == 0) {
            Mono mono = new Mono(0, BigInteger.ONE);
            poly.addMono(mono);
            return poly;
        }
        for (int i = 0; i < exp; i++) {
            poly = poly.mul(polyExpr);
            //System.out.println(poly);
        }
        //poly.simplify();
        return poly;
    }

    public void simplify() {
        ArrayList<Mono> newMono = new ArrayList<>();
        //System.out.print("before:");
        //System.out.println(this.toString());

        for (Mono needAdd : monos) {
            boolean flag = true;

            //System.out.print("needAdd:");
            //System.out.println(needAdd);

            for (Mono alreadyAdd : newMono) {
                //System.out.print("alreadyAdd:");
                //System.out.println(alreadyAdd);

                if (Mono.sameWith(needAdd, alreadyAdd)) {
                    BigInteger cof = needAdd.getCof().add(alreadyAdd.getCof());
                    alreadyAdd.setCof(cof);
                    flag = false;
                    break;
                }
            }
            if (flag) {
                newMono.add(needAdd);
            }
        }
        monos = newMono;

        //System.out.print("After:");
        //System.out.println(this.toString());

    }

    public void postiveFront() {
        int index = 0;
        for (int i = 0; i < monos.size(); i++) {
            Mono mono = monos.get(i);
            if (mono.isPositive()) {
                //System.out.println(mono.getCof());
                index = i;
                break;
            }
        }
        //如果size是0不能换 但是好像不会size是0吧？
        if (!monos.isEmpty()) {
            //System.out.println("********");
            Collections.swap(monos, 0, index);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        //正项提前
        postiveFront();

        for (Mono mono : monos) {
            //System.out.println("**********");
            String str = mono.toString();
            if (str.equals("0")) {
                continue;
            }
            if (mono.isPositive()) {
                sb.append("+");
            }
            sb.append(str);
        }
        //空串补“0”
        if (sb.toString().equals("")) {
            sb.append("0");
        }

        if (sb.charAt(0) == '+') {
            sb.deleteCharAt(0);
        }
        return sb.toString();
    }

    /////////////////////////////////////////////////
    public ArrayList<Mono> getMonos() {
        return monos;
    }
}

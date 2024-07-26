import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
import java.util.Iterator;
import java.util.Map;

public class Poly {

    private ArrayList<Mono> monos;
    private HashMap<Mono, BigInteger> monoMap;

    public Poly() {
        this.monos = new ArrayList<>();
        this.monoMap = new HashMap<>();
    }

    public Poly toDiff() {
        Poly poly = new Poly();
        for (Mono mono : monos) {
            poly = Poly.add(poly, mono.toDiff());
        }
        return poly;
    }

    public Object myClone() {
        Poly poly = new Poly();
        for (Mono mono : monos) {
            Mono newMono = (Mono) mono.myClone();
            poly.getMonos().add(newMono);
            poly.getMonoMap().put(newMono, newMono.getCof());
        }
        return poly;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Poly)) {
            return false;
        }
        Poly poly = (Poly) o;
        return Objects.equals(monoMap, poly.monoMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(monoMap);
    }

    public void addMono(Mono mono) {
        Mono newMono = (Mono) mono.myClone();
        for (Mono oldMono : monos) {
            if (newMono.equals(oldMono)) {
                BigInteger cof = oldMono.getCof().add(newMono.getCof());
                oldMono.setCof(cof);
                monoMap.put(oldMono, cof);
                return;
            }
        }
        monoMap.put(newMono, newMono.getCof());
        monos.add(newMono);
    }

    public static Poly add(Poly p1, Poly p2) {
        Poly poly = new Poly();

        for (Mono mono : p1.getMonos()) {
            poly.addMono(mono);
        }
        for (Mono mono : p2.getMonos()) {
            poly.addMono(mono);
        }
        poly.removeZero();
        return poly;
    }

    public static Poly mulNum(Poly expBase, BigInteger num) {

        Poly poly = new Poly();
        NumberFactor numberFactor = new NumberFactor(num);
        Poly numPoly = numberFactor.toPoly();
        if (expBase.getMonos().isEmpty()) {
            return poly;
        }
        return Poly.mul(expBase, numPoly);
    }

    public static Poly mul(Poly p1, Poly p2) {
        Poly poly = new Poly();
        //        //如果是空的？"x "
        //        if (monos.isEmpty()) {
        //            return mulOne;
        //        }
        //        if (mulOne.getMonos().isEmpty()) {
        //            return this;
        //        }
        for (Mono mono : p1.getMonos()) {
            for (Mono monoo : p2.getMonos()) {
                Mono mono1 = Mono.mul(mono, monoo);
                poly.addMono(mono1);
            }
        }
        poly.removeZero();
        return poly;
    }

    public Poly pow(Poly polyExpr, BigInteger exp) {
        Poly poly = new Poly();
        // 0 次？
        if (exp.equals(BigInteger.ZERO)) {
            Mono mono = new Mono(BigInteger.ZERO, BigInteger.ONE);
            poly.addMono(mono);
            return poly;
        }
        poly = (Poly) polyExpr.myClone();
        Poly.add(poly, polyExpr);
        for (BigInteger i = BigInteger.ONE; i.compareTo(exp) < 0; i = i.add(BigInteger.ONE)) {
            poly = Poly.mul(poly, polyExpr);
        }

        return poly;
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

    public void removeZero() {

        Iterator<Map.Entry<Mono, BigInteger>> iterator = monoMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Mono, BigInteger> entry = iterator.next();
            if (entry.getValue().equals(BigInteger.ZERO)) {
                iterator.remove(); // 使用迭代器的remove方法删除元素
            }
        }

        Iterator<Mono> iterator1 = monos.iterator();
        while (iterator1.hasNext()) {
            Mono mono = iterator1.next();
            if (mono.isZero()) {
                iterator1.remove(); // 使用迭代器的remove方法安全删除元素
            }
        }


    }

    public boolean isSimple() {
        int size = monos.size();
        if (size == 0) {
            return false;
        } else if (monos.size() == 1) {
            Mono mono = monos.get(0);
            BigInteger num = monos.get(0).getCof();
            if (mono.isOnlyCof()) {
                return true;
            } else if (mono.isOnlyX() || mono.isOnlyExp()) {
                return num.compareTo(BigInteger.ZERO) > 0;
            } else {
                return false;
            }
        } else {
            return false;
        }

    }

    /////////////////////////////////////////////////
    public ArrayList<Mono> getMonos() {
        return monos;
    }

    public HashMap<Mono, BigInteger> getMonoMap() {
        return monoMap;
    }
}


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Poly {
    private ArrayList<Unit> units = new ArrayList<>();

    public Poly() {}

    public void createOne() { units.add(new Unit(BigInteger.ONE)); }

    public void add(Poly poly) { this.units.addAll(poly.units); }

    public void addUnit(Unit unit) { this.units.add(unit); }

    public void Negate() {
        for (Unit unit : units) { unit.Negate(); }
    }

    public Unit unitMulti(Unit unit1, Unit unit2) { //逻辑怪怪的
        Unit res = new Unit(Unit.Type.CONST);
        //Unit new1 = unit1.clone();
        //Unit new2 = unit2.clone();
        res.multiUnit(unit1);
        res.multiUnit(unit2);
        return res;
    }

    public void multiUnit(Unit unit) {
        //System.out.println(unit);
        unit.wipeExp();
        //System.out.println(unit);
        if (unit.getCoff().equals(BigInteger.ZERO)) {
            //System.out.println("coff "+unit.getCoff());
            units.clear();
        } else {
            for (Unit u : units) { u.multiUnit(unit); }
        }
        /*for (Unit u : units) {
            System.out.println(u.getCoff() + " " + u.getExp());
        }*/
    }

    public void multiExpr(Poly poly) {
        ArrayList<Unit> res = new ArrayList<>();
        for (Unit self : units) {
            for (Unit other : poly.units) {
                res.add(unitMulti(self, other));
            }
        }
        units = res;
        //TODO: Merge
        Merge();
    }

    public void Merge() { //!!!慎重对待这个方法!!!
        //Merge same
        Collections.sort(units);
        //System.out.println(unit +" " +unit.getExpFunc());
        /*ListIterator<Unit> iterator = units.listIterator(units.size());
        while (iterator.hasPrevious()) {
            int currentIndex = iterator.previousIndex();
            //System.out.println(currentIndex);
            Unit cur = iterator.previous();
            //System.out.println("cur " + cur);
            if (iterator.hasPrevious()) {
                Unit pre = iterator.previous();
                //System.out.println(iterator.next()); // 把指针移回前一个
                iterator.next();
                if (pre.canMerge(cur)) {
                    pre.merge(cur);
                    //System.out.println(iterator.next());
                    iterator.next();
                    iterator.remove();
                }
            }
        }*/ //尝试过使用Iterator的迷惑方法
        for (int i = units.size() - 1; i > 0; i--) {
            Unit pre = units.get(i - 1);
            Unit cur = units.get(i);
            if (pre.canMerge(cur)) {
                pre.merge(cur);
                units.remove(i);
            }
        }
        //System.out.println(unit.getCoff() + " "+unit);
        //remove 0
        for (int i = units.size() - 1; i >= 0; i--) {
            if (units.get(i).getCoff().equals(BigInteger.ZERO)) {
                units.remove(i);
            }
        }
        /*for (Unit u : units) {
            System.out.println(u.toString());
        }*/
    }

    public int adjustUnit() {
        int pos = -1;
        for (int i = 0; i < units.size(); i++) {
            if (units.get(i).getCoff().compareTo(BigInteger.ZERO) > 0) {
                pos = i;
                return pos;
            }
        }
        return pos;
    }

    @Override
    public String toString() {
        if (units.isEmpty()) { return "0"; }
        StringBuilder sb = new StringBuilder();
        int pos = adjustUnit();
        if (pos >= 0) {
            sb.append(units.get(pos).toString().substring(1));
        }
        for (int i = 0; i < units.size(); i++) {
            if (i == pos) { continue; }
            sb.append(units.get(i).toString());
        }
        return sb.toString();
    }

    public boolean isEmpty() { return units.isEmpty(); }

    public boolean isSimple() {
        int n = units.size();
        if (n > 1) {
            return false;
        } else if (n == 0) {
            return true;
        }
        return units.get(0).isSimple();
    }

    public Poly clone() {
        Poly poly = new Poly();
        for (Unit unit : units) {
            poly.units.add(unit.clone());
        }
        return poly;
    }

    public Poly pow(BigInteger exponent) {
        for (Unit unit : units) {
            unit.setCoff(unit.getCoff().multiply(exponent));
        }
        return this;
    }

    public Poly getDerivative(Unit.Type type) {
        Poly res = new Poly();
        for (Unit unit : units) {
            res.add(unit.getDerivative(type));
        }
        return res;
    }

    public Collection<Term> toTerms() {
        List<Term> terms = new ArrayList<>();
        for (Unit unit : units) {
            Term term = new Term();
            term.addFactor(unit);
            terms.add(term);
        }
        return terms;
    }

    public int compareTo(Poly p) {
        Collections.sort(this.units);
        Collections.sort(p.units);
        for (int i = 0; i < units.size(); i++) {
            if (units.get(i).compareTo(p.units.get(i)) != 0) {
                return units.get(i).compareTo(p.units.get(i));
            }
        }
        return 0;
    }
}

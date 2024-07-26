//将Expr 与基元 Unit 归为一类Factor，其均可以用a*()|x^b的形式表示，唯一区别在于基底
//若将常数因子也看为一类Factor，那么a*()|x^b又可以看作两个Factor相乘，故此时不再于接口中另设有关coff的方法
/*
coefficient -> 系数 -> coff
base -> 基底 -> base
exponent -> 指数 -> exp
negate -> 利用BigInteger取反
*/

import java.math.BigInteger;

public interface Factor {
    void Negate();

    void setExp(BigInteger exp);    //写系数

    Factor clone();

    Poly getDerivative(Unit.Type type);
}

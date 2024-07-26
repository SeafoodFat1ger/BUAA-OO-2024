import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {
    private final Lexer lexer;
    private static final Map<String, Func> funcs = new HashMap<>();

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    public Expr parseExpr() { // 记得处理"+|-"号在开头的情况
        Expr res = new Expr();
        if (lexer.peek().equals("-")) {
            lexer.next();
            Term term = parseTerm();
            term.Negate();
            res.addTerm(term);
        } else if (lexer.peek().equals("+")) {
            lexer.next();
            res.addTerm(parseTerm());
        } else {
            res.addTerm(parseTerm());
        }

        while (lexer.peek().equals("+") || lexer.peek().equals("-")) {
            if (lexer.peek().equals("-")) {
                lexer.next();
                Term term = parseTerm();
                term.Negate();
                res.addTerm(term);
            } else {
                lexer.next();
                res.addTerm(parseTerm());
            }
        }
        return res;
    }

    private Term parseTerm() { // 记得处理"+|-"号在开头的情况
        Term res = new Term();
        if (lexer.peek().equals("-")) {
            lexer.next();
            Factor factor = parseFactor();
            factor.Negate();
            res.addFactor(factor);
        } else if (lexer.peek().equals("+")) {
            lexer.next();
            res.addFactor(parseFactor());
        } else {
            res.addFactor(parseFactor());
        }

        while (lexer.peek().equals("*")) {
            lexer.next();
            res.addFactor(parseFactor());
        }
        return res;
    }

    private Factor parseFactor() {
        Factor res;
        if (lexer.peek().equals("(")) {
            lexer.next();
            res = parseExpr();
            lexer.next(); // 不会有非法括号，直接吞掉")"
            if (lexer.peek().equals("^")) {
                res.setExp(getExp());
            }
        } else { // 记得处理"+|-"号在开头的情况
            boolean needNeg = false;
            if (lexer.peek().equals("-")) {
                lexer.next();
                needNeg = true;
            } else if (lexer.peek().equals("+")) {
                lexer.next();
            }
            if (lexer.peek().equals("x")) {
                res = new Unit(Unit.Type.X);
            } else if (lexer.peek().equals("y")) {
                res = new Unit(Unit.Type.Y);
            } else if (lexer.peek().equals("z")) {
                res = new Unit(Unit.Type.Z);
            } else if (lexer.peek().equals("exp")) {
                lexer.next();
                lexer.next();
                res = new Unit(Unit.Type.EXP, parseFactor());
            } else if ("fgh".contains(lexer.peek())) {
                res = parseFunc();
            } else if (lexer.peek().startsWith("d")) {
                res = parseDerivative();
            } else {
                res = new Unit(new BigInteger(lexer.peek()));
            }
            lexer.next();
            if (lexer.peek().equals("^")) {
                res.setExp(getExp());
            }
            if (needNeg) { res.Negate(); }
        }
        return res;
    }

    private Factor parseDerivative() {
        char c = lexer.peek().charAt(1);
        lexer.next();
        lexer.next();
        Expr expr = parseExpr();
        if (c == 'x') {
            expr = new Expr(expr.getDerivative(Unit.Type.X));
        } else if (c == 'y') {
            expr = new Expr(expr.getDerivative(Unit.Type.Y));
        } else if (c == 'z') {
            expr = new Expr(expr.getDerivative(Unit.Type.Z));
        }
        return expr;
    }

    private Factor parseFunc() {
        final Func func = funcs.get(lexer.peek());
        lexer.next();
        lexer.next();
        List<Factor> params = new ArrayList<>();
        params.add(parseFactor());
        while (lexer.peek().equals(",")) {
            lexer.next();
            params.add(parseFactor());
        }
        Expr expr = (Expr) func.invoke(params);
        //System.out.println("invoked " + expr.extend());
        return expr;
    }

    private BigInteger getExp() {
        lexer.next();
        if (lexer.peek().equals("+")) {
            lexer.next();
        }
        BigInteger exp = new BigInteger(lexer.peek());
        lexer.next();
        return exp;
    }

    public void parseFuncDef() {
        final String funcName = lexer.peek();
        ArrayList<Character> paramNames = new ArrayList<>();
        lexer.next();
        lexer.next();
        paramNames.add(lexer.peek().charAt(0));
        lexer.next();
        while (lexer.peek().equals(",")) {
            lexer.next();
            paramNames.add(lexer.peek().charAt(0));
            lexer.next();
        }
        lexer.next();
        lexer.next();
        StringBuilder sb = new StringBuilder();
        do {
            sb.append(lexer.peek());
        } while (lexer.next());
        Character[] param = new Character[paramNames.size()];
        funcs.put(funcName, new Func(sb.toString(), paramNames.toArray(param)));
    }
}

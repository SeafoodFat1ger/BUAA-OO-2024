import java.math.BigInteger;
import java.util.ArrayList;

public class Parser {
    private final Lexer lexer;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    public Expr parseExpr() {
        Expr expr = new Expr();
        expr.addTerm(parseTerm());

        while (lexer.peek().equals("+") || lexer.peek().equals("-")) {
            //lexer.next();
            expr.addTerm(parseTerm());
        }
        return expr;
    }

    public Term parseTerm() {
        //System.out.println("********Term********");
        Term term = new Term();
        //term.addFactor(parseFactor());
        Factor numFu = new NumberFactor(BigInteger.valueOf(-1));

        if (lexer.peek().equals("+")) {
            lexer.next();
            term.addFactor(parseFactor());
        } else if (lexer.peek().equals("-")) {
            term.addFactor(numFu);
            lexer.next();
            term.addFactor(parseFactor());
        } else {
            term.addFactor(parseFactor());
        }


        while (lexer.peek().equals("*")) {
            lexer.next();
            if (lexer.peek().equals("-")) {
                term.addFactor(numFu);
                lexer.next();
            }
            term.addFactor(parseFactor());
        }
        return term;
    }

    public Factor parseFactor() {
        //System.out.println("********Factor********");

        if (lexer.peek().equals("(")) {
            //System.out.println("********ExpFactor********");
            return paserExprFactor();

        } else if (Character.isDigit(lexer.peek().charAt(0)) ||
                lexer.peek().equals("+") || lexer.peek().equals("-")) {
            //System.out.println("********NumFactor********");
            return paserNumberFactor();

        } else if (lexer.peek().charAt(0) == 'e') {
            //System.out.println("********ExpFactor********");
            return parseExpFactor();

        } else if (lexer.peek().charAt(0) == 'd') {
            //System.out.println("********DxFactor********");
            return parseDxFactor();

        } else if (lexer.peek().charAt(0) == 'f' || lexer.peek().charAt(0) == 'g'
                || lexer.peek().charAt(0) == 'h') {
            //System.out.println("********FuncFactor********");
            return paserFuncFactor();

        } else {
            //System.out.println("********VarFactor********");
            return paserVarFactor();
        }
    }

    public Factor parseDxFactor() {
        lexer.next();
        Expr expr = parseExpr();
        lexer.next();

        return new DxFactor(expr);
    }

    public Factor parseExpFactor() {
        lexer.next();
        Factor base = parseFactor();
        lexer.next();

        BigInteger exp = paserExp();

        return new ExpFactor(base, exp);
    }

    public Factor paserFuncFactor() {
        String str = lexer.peek();
        lexer.next();
        ArrayList<Factor> realParas = new ArrayList<>();
        realParas.add(parseFactor());
        String name = String.valueOf(str.charAt(0));
        while (!lexer.peek().equals(")")) {
            lexer.next();
            realParas.add(parseFactor());
        }

        lexer.next();

        return new FuncFactor(name, realParas);
    }

    public BigInteger paserExp() {
        BigInteger exp = BigInteger.ONE;
        if (lexer.peek().equals("^")) {
            lexer.next();
            exp = new BigInteger(lexer.peek());
            lexer.next();
        }
        return exp;
    }

    public Factor paserExprFactor() {
        lexer.next();
        Expr expr = parseExpr();
        lexer.next();

        BigInteger exp = paserExp();
        return new ExprFactor(expr, exp);
    }

    public Factor paserNumberFactor() {
        BigInteger plus = BigInteger.ONE;
        if (lexer.peek().equals("-")) {
            plus = BigInteger.valueOf(-1);
            lexer.next();
        } else if (lexer.peek().equals("+")) {
            lexer.next();
        }

        BigInteger num = new BigInteger(lexer.peek());
        num = num.multiply(plus);
        lexer.next();
        return new NumberFactor(num);
    }

    public Factor paserVarFactor() {
        String name = lexer.peek();
        lexer.next();

        BigInteger exp = paserExp();
        return new VarFactor(name, exp);

    }
}

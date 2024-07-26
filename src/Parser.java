import expr.Expr;
import expr.Term;
import expr.Factor;
import expr.VarFactor;
import expr.ExprFactor;
import expr.NumberFactor;

import java.math.BigInteger;

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

            lexer.next();
            Expr expr = parseExpr();
            lexer.next();

            if (lexer.peek().equals("^")) {
                lexer.next();
                int exp = Integer.parseInt(lexer.peek());
                lexer.next();
                return new ExprFactor(expr, exp);
            }
            return new ExprFactor(expr, 1);

        } else if (Character.isDigit(lexer.peek().charAt(0))) {
            //System.out.println("********NumFactor********");

            BigInteger num = new BigInteger(lexer.peek());
            lexer.next();
            return new NumberFactor(num);

        } else {
            //String var = lexer.peek();
            //System.out.println("********VarFactor********");

            lexer.next();
            if (lexer.peek().equals("^")) {
                lexer.next();
                int exp = Integer.parseInt(lexer.peek());
                lexer.next();
                return new VarFactor(exp);
            } else {
                return new VarFactor(1);
            }
        }
    }
}

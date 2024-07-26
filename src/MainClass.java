import expr.Expr;
import expr.Poly;

import java.util.Scanner;

public class MainClass {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        PreDeal preDeal = new PreDeal(input);
        preDeal.dealInput();
        input = preDeal.getInput();

        //System.out.println(input);

        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);

        Expr expr = parser.parseExpr();

        Poly poly = expr.toPoly();
        String ans = poly.toString();
        System.out.println(ans);
    }
}

import java.util.Scanner;

public class MainClass {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int n = Integer.parseInt(scanner.nextLine());
        for (int i = 0; i < n; i++) {
            String input = scanner.nextLine();
            input = PreDeal.dealInput(input);
            DefineFunc.addFunc(input);
        }


        String input = scanner.nextLine();

        input = PreDeal.dealInput(input);

        //System.out.println(input);

        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);

        Expr expr = parser.parseExpr();

        Poly poly = expr.toPoly();
        String ans = poly.toString();

        System.out.println(ans);
    }
}

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    private static Processor PROCESSOR = new Processor();
    private static ArrayList<String> funcList = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String raw = scanner.nextLine();
        int cnt = Integer.parseInt(raw.trim());
        String func;

        for (int i = 0; i < cnt; i++) {
            raw = scanner.nextLine();
            func = PROCESSOR.newInput(raw);
            //new Parser(new Lexer(func)).parseFuncDef();
            funcList.add(func);
        }
        int flag = -1;
        raw = scanner.nextLine();
        String input = PROCESSOR.newInput(raw);
        for (int i = funcList.size() - 1; i >= 0; i--) {
            if (input.indexOf(funcList.get(i).charAt(0)) != -1) {
                flag = i;
                break;
            }
        }
        for (int i = 0; i <= flag; i++) {
            new Parser(new Lexer(funcList.get(i))).parseFuncDef();
        }
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);

        Expr expr = parser.parseExpr();
        System.out.println(expr.extend());
        //for Debug Use
        //System.out.println(expr.extend().toString().replace("^", "**"));
        // 测试equals方法
        //String Test = scanner.nextLine();
        //Expr test = new Parser(new Lexer(Test)).parseExpr();
        //System.out.println(test.extend());
        //System.out.println(expr.equals(test));
    }
}

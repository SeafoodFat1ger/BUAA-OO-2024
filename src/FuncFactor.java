import java.util.ArrayList;

public class FuncFactor implements Factor {
    private String funcStr;
    private Expr funcExpr;

    public FuncFactor(String name, ArrayList<Factor> realParas) {
        this.funcStr = DefineFunc.useFunc(name, realParas);
        this.funcExpr = setExpr();
    }

    public Expr setExpr() {
        String str = PreDeal.dealInput(funcStr);
        Lexer lexer = new Lexer(str);
        Parser parser = new Parser(lexer);
        return parser.parseExpr();

    }

    @Override
    public Poly toDiff() {
        return funcExpr.toDiff();
    }

    @Override
    public Poly toPoly() {
        return funcExpr.toPoly();
    }
}

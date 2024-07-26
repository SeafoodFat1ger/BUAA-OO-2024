import java.util.HashMap;
import java.util.List;

public class Func {
    private final String definition;
    private final Character[] paramNames;

    public Func(String rawExpr, Character[] paramNames) {
        this.definition = new Parser(new Lexer(rawExpr)).parseExpr().extend().toString();
        this.paramNames = paramNames;
    }

    public Factor invoke(List<Factor> params) {
        Expr expr = new Parser(new Lexer(definition)).parseExpr();
        HashMap<Unit.Type, Factor> map = new HashMap<>();
        for (int i = 0; i < paramNames.length; i++) {
            Unit.Type type;
            if (paramNames[i] == 'x') {
                type = Unit.Type.X;
            } else if (paramNames[i] == 'y') {
                type = Unit.Type.Y;
            } else {
                type = Unit.Type.Z;
            }
            //System.out.println(params.get(i));
            map.put(type, params.get(i));
        }
        //System.out.println("expr term size "+expr.terms.size());
        expr.replace(map);
        //System.out.println("expr term size "+expr.terms.size());
        //System.out.println("replaced " + expr.extend());
        return expr;
    }
}

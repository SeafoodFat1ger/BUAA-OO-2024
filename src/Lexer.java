public class Lexer {
    private final String input;
    private int pos = 0;
    private String curToken;

    public Lexer(String input) {
        this.input = input;
        this.next();
    }

    private String getNumber() {
        StringBuilder sb = new StringBuilder();
        while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
            sb.append(input.charAt(pos));
            ++pos;
        }

        return sb.toString();
    }

    public void next() {
        if (pos == input.length()) {
            return;
        }

        char c = input.charAt(pos);
        if (Character.isDigit(c)) {
            curToken = getNumber();

            //()+-*^x,

        } else if (c == '(' || c == ')' || c == '+' || c == '-' || c == '*' || c == '^'
                || c == 'x') {
            pos += 1;
            curToken = String.valueOf(c);
        } else if (c == ',') {
            pos += 1;
            curToken = String.valueOf(c);

            //自定义

        } else if (c == 'f' || c == 'g' || c == 'h') {
            pos += 2;
            curToken = c + "(";

            //指数函数
        } else if (c == 'e') {
            pos += 2;
            curToken = c + "(";
        } else if (c == 'd') {
            pos += 3;
            curToken = c + "x(";
        }

    }

    public String peek() {
        return this.curToken;
    }
}

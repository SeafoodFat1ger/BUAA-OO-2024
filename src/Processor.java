public class Processor {
    private String input;

    public void getString(String string) { this.input = string; }

    public void treatPlusMinus() {
        //删掉了连续的多个+-
        input = input.replace("+-", "-");
        input = input.replace("-+", "-");
        input = input.replace("++", "+");
        input = input.replace("--", "+");
        //-+x**3 + -x**2*y**7 * z ++  5 *  6 - - 5 * x ** +03
        //删掉第一个字符为+的
        if (input.startsWith("+")) { input = input.substring(1); }
        //...用于拓展
    }

    public void replaceDoubleStar() {
        input = input.replace("**", "^");
    } //用于Debug

    public void consumeBlank() {
        input = input.replace("\t", "");
        input = input.replace(" ", "");
    }

    public void adjustSign() {
        //删掉无必要的+
        //...
        input = input.replace("*+", "*");
        //...用于拓展
    }

    public String newInput(String raw) {
        getString(raw);
        consumeBlank();
        treatPlusMinus();
        treatPlusMinus();
        adjustSign();
        replaceDoubleStar();
        return outputString();
    }

    public String outputString() { return input; }

}

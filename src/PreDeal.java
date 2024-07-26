public class PreDeal {
    private String input;

    public PreDeal(String input) {
        this.input = input;
    }

    public void dealInput() {
        //去除空白项
        input = input.replaceAll("[ \t]", "");


//        //去除++/--/+-/-+
//        input = input.replaceAll("\\+\\+|--", "+");
//        input = input.replaceAll("\\+-|-\\+", "-");
//        //+ + +1
//        input = input.replaceAll("\\+\\+|--", "+");
//        input = input.replaceAll("\\+-|-\\+", "-");
//        //去除冗余+
//        input = input.replaceAll("\\*\\+", "*");
//        input = input.replaceAll("\\^\\+", "^");
//        input = input.replaceAll("\\(\\+", "(");

    }

    public String getInput() {
        return input;
    }
}

public class PreDeal {

    public static String dealInput(String input) {
        //去除空白项
        String input1 = input.replaceAll("[ \t]", "");

        //去除++/--/+-/-+
        String input2 = input1.replaceAll("\\+\\+|--", "+");
        String input3 = input2.replaceAll("\\+-|-\\+", "-");
        //+ + +1
        String input4 = input3.replaceAll("\\+\\+|--", "+");
        String input5 = input4.replaceAll("\\+-|-\\+", "-");
        //去除冗余+
        String input6 = input5.replaceAll("\\*\\+", "*");
        String input7 = input6.replaceAll("\\^\\+", "^");
        String input8 = input7.replaceAll("\\(\\+", "(");
        String input9 = input8.replaceAll(",\\+", ",");
        //exp->x
        String input10 = input9.replaceAll("exp", "e");



        return input10;
    }

}

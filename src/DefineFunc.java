import java.util.ArrayList;
import java.util.HashMap;

public class DefineFunc {
    private static HashMap<String, String> funcMap = new HashMap<>();
    private static HashMap<String, ArrayList<String>> formalParaMap = new HashMap<>();

    public static void addFunc(String input) {
        String name = String.valueOf(input.charAt(0));
        int indexEqual = input.indexOf('=');
        String funcStr = input.substring(indexEqual + 1);

        funcMap.put(name, funcStr);

        ArrayList<String> formalParas = new ArrayList<>();
        for (int i = 0; i < indexEqual; i++) {
            char c = input.charAt(i);
            if (c == 'x' || c == 'y' || c == 'z') {
                formalParas.add(String.valueOf(c));
            }
        }
        formalParaMap.put(name, formalParas);


    }

    public static String useFunc(String name, ArrayList<Factor> realParas) {
        String func = funcMap.get(name);
        ArrayList<String> formalParas = formalParaMap.get(name);
        int size = formalParas.size();

        //chatgpt说sb的性能比replaceAll好？
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < func.length(); i++) {
            char c = func.charAt(i);
            int index = -1;
            for (int j = 0; j < size; j++) {
                if (formalParas.get(j).equals(String.valueOf(c))) {
                    index = j;
                    break;
                }
            }

            if (index == -1) {
                sb.append(c);
            } else {
                sb.append("(");
                sb.append(realParas.get(index).toPoly().toString());
                sb.append(")");
            }
        }
        return sb.toString();
    }

}

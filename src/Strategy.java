import java.util.ArrayList;
import java.util.HashMap;

public class Strategy {
    private final RequestTable request;

    public Strategy(RequestTable request) {
        this.request = request;
    }

    public Advice getAdvice(int eleFloor, int elePersonNum, boolean eleDirection,
                            HashMap<Integer, ArrayList<Person>> eleMap, int eleMaxNum) {
        if (request.getResetRequest() != null) {
            return Advice.RESET;
        }
        //是否开门
        if (openForOut(eleFloor, eleMap)
                || openForIn(eleFloor, elePersonNum, eleDirection, eleMaxNum)) {
            return Advice.OPEN;
        }
        //电梯是否有人->继续原方向
        if (elePersonNum != 0) {
            return Advice.MOVE;
        }

        //请求队列是否空
        if (request.isEmpty()) {
            if (request.isEnd()) {
                return Advice.OVER;
            } else {
                return Advice.WAIT;
            }
        } else {
            if (reqSameDirection(eleFloor, eleDirection)) {
                return Advice.MOVE;
            } else {
                return Advice.REVERSE;
            }
        }
    }

    public boolean openForIn(int eleFloor, int elePersonNum, boolean eleDirection, int eleMaxNum) {
        //如果满人了  先下后上 不下绝对不可能上....
        if (elePersonNum >= eleMaxNum) {
            return false;
        }
        if (!request.isEmpty()) {
            synchronized (request) {
                if (request.getReqMap().containsKey(eleFloor)) {
                    ArrayList<Person> persons = request.getReqMap().get(eleFloor);
                    for (Person person : persons) {
                        if ((person.getToFloor() > eleFloor && eleDirection) ||
                                (person.getToFloor() < eleFloor && !eleDirection)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean openForOut(int eleFloor, HashMap<Integer, ArrayList<Person>> eleMap) {

        if (eleMap.containsKey(eleFloor)) {
            ArrayList<Person> persons = eleMap.get(eleFloor);
            if (!persons.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public boolean reqSameDirection(int eleFloor, boolean eleDirection) {
        //请求队列非空已判断
        synchronized (request) {
            for (Integer i : request.getReqMap().keySet()) {
                if ((i > eleFloor && eleDirection) ||
                        (i < eleFloor && !eleDirection)) {

                    //TimableOutput.println(String.format
                    // ("persons-%d-%d", persons.get(0).getId(),persons.get(0).getFromFloor() ));

                    return true;
                }
            }
        }
        return false;
    }
}

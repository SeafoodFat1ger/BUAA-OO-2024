import com.oocourse.elevator1.TimableOutput;

import java.util.ArrayList;
import java.util.HashMap;

public class Elevator extends Thread {
    private int id;
    private int elePersonNum;
    private int eleFloor;
    private boolean eleDirection;
    private RequestTable eleRequest;
    private Strategy strategy;

    //目的地key
    private HashMap<Integer, ArrayList<Person>> eleMap;

    public Elevator(int id, RequestTable eleRequest) {
        this.id = id;
        this.elePersonNum = 0;
        this.eleFloor = 1;
        this.eleDirection = true;
        this.eleMap = new HashMap<>();
        this.strategy = new Strategy(eleRequest);
        this.eleRequest = eleRequest;
    }

    @Override
    public void run() {
        while (true) {
            Advice advice = strategy.getAdvice(eleFloor, elePersonNum, eleDirection, eleMap);
            //            System.out.println("id:"+id);
            //            System.out.println(eleRequest.isEmpty());
            //            System.out.println(eleRequest.isEnd());
            //            System.out.println("eleNum:"+elePersonNum);
            if (advice == Advice.OVER) {
                break;
            } else if (advice == Advice.MOVE) {
                move();
            } else if (advice == Advice.REVERSE) {
                reverse();
            } else if (advice == Advice.WAIT) {
                eleRequest.waitRequest();
            } else if (advice == Advice.OPEN) {
                openAndClose();
            }
        }
    }

    public void reverse() {
        eleDirection = !eleDirection;
    }

    public void move() {
        try {
            if (eleDirection) {
                if (eleFloor < 11) {
                    eleFloor++;
                } else {
                    eleFloor--;
                    eleDirection = false;
                }
            } else {
                if (eleFloor > 1) {
                    eleFloor--;
                } else {
                    eleFloor++;
                    eleDirection = true;
                }
            }
            sleep(400);
            TimableOutput.println(String.format("ARRIVE-%d-%d", eleFloor, id));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void openAndClose() {
        TimableOutput.println(String.format("OPEN-%d-%d", eleFloor, id));

        //先下后上好文明

        firstOut();

        try {
            sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //reverse？
        Advice advice = strategy.getAdvice(eleFloor, elePersonNum, eleDirection, eleMap);
        if (advice == Advice.REVERSE) {
            reverse();
        }
        secondIn();

        TimableOutput.println(String.format("CLOSE-%d-%d", eleFloor, id));
    }

    public void firstOut() {
        ArrayList<Person> outPersons = new ArrayList<>();

        if (eleMap.containsKey(eleFloor)) {
            outPersons = eleMap.get(eleFloor);
            for (Person person : outPersons) {
                TimableOutput.println(String.format("OUT-%d-%d-%d", person.getId(), eleFloor, id));
                elePersonNum--;
            }
        }
        eleMap.remove(eleFloor, outPersons);
    }

    public void secondIn() {
        ArrayList<Person> inPerson = eleRequest.delRequest(eleFloor, eleDirection, elePersonNum);

        for (Person person : inPerson) {
            TimableOutput.println(String.format("IN-%d-%d-%d", person.getId(), eleFloor, id));
            int toFloor = person.getToFloor();

            if (eleMap.containsKey(toFloor)) {
                eleMap.get(toFloor).add(person);
            } else {
                ArrayList<Person> persons = new ArrayList<>();
                persons.add(person);
                eleMap.put(person.getToFloor(), persons);
            }

            elePersonNum++;
        }
    }
}

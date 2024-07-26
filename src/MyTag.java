import com.oocourse.spec3.main.Person;
import com.oocourse.spec3.main.Tag;

import java.util.HashMap;

public class MyTag implements Tag {
    private final int id;
    private int valueSum;
    private int ageSum;
    private int agePowSum;
    private int personSum;
    private final HashMap<Integer, Person> persons = new HashMap<>();

    public MyTag(int id) {
        this.id = id;
        this.valueSum = 0;
        this.ageSum = 0;
        this.personSum = 0;
        this.agePowSum = 0;
    }

    public int getId() {
        return id;
    }

    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Tag) {
            return (((Tag) obj).getId() == id);
        } else {
            return false;
        }
    }

    public void addPerson(Person person) {
        if (!hasPerson(person)) { //TO/DO* should delet?
            persons.put(person.getId(), person);
            personSum++;
            ageSum += person.getAge();
            agePowSum += person.getAge() * person.getAge();
            //            for (Integer id : ((MyPerson) person).getAcquaintance().keySet()) {
            //                if (persons.containsKey(id)) {
            //                    valueSum += 2 * person.queryValue(persons.get(id));
            //                }
            //            }
        }

    }

    public boolean hasPerson(Person person) {
        return persons.containsKey(person.getId());
    }

    public int getValueSum() {
        valueSum = 0;
        for (Person person : persons.values()) {
            for (Person person1 : ((MyPerson) person).getAcquaintance().values()) {
                if (hasPerson(person1)) {
                    valueSum += person1.queryValue(person);
                }
            }
        }

        return valueSum;
    }

    public int getAgeMean() {
        if (personSum == 0) {
            return 0;
        }
        return ageSum / personSum;
    }

    public int getAgeVar() {
        if (personSum == 0) {
            return 0;
        }
        int mean = getAgeMean();
        return (agePowSum - 2 * mean * ageSum + mean * mean * personSum) / personSum;
    }

    public void delPerson(Person person) {
        if (hasPerson(person)) { //TO/DO* should delet?
            persons.remove(person.getId(), (MyPerson) person);
            ageSum -= person.getAge();
            agePowSum -= person.getAge() * person.getAge();
            personSum--;
            //            for (Integer id : persons.keySet()) {
            //                if (persons.get(id).isLinked(person)) {
            //                    valueSum -= 2 * person.queryValue(persons.get(id));
            //                }
            //            }
        }
    }

    public int getSize() {
        return personSum;
    }

    public void addSocialValue(int num) {
        for (Person person : persons.values()) {
            person.addSocialValue(num);
        }
    }

    public void addMoney(int num) {
        for (Person person : persons.values()) {
            person.addMoney(num);
        }
    }
}

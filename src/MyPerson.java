import com.oocourse.spec1.main.Person;

import java.util.HashMap;

public class MyPerson implements Person {

    private final int id;
    private final String name;
    private final int age;
    private final HashMap<Integer, Person> acquaintance = new HashMap<>();
    private final HashMap<Integer, Integer> value = new HashMap<>();

    public MyPerson(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Person) {
            return (((Person) obj).getId() == id);
        } else {
            return false;
        }
    }

    public boolean isLinked(Person person) {
        if (person.getId() == id) {
            return true;
        }
        return acquaintance.containsKey(person.getId());
    }

    public int queryValue(Person person) {
        if (acquaintance.containsKey(person.getId())) {
            return value.get(person.getId());
        }
        return 0;
    }

    public void addRelation(Person person, int valueNum) {
        acquaintance.put(person.getId(), person);
        value.put(person.getId(), valueNum);
    }

    public HashMap<Integer, Person> getAcquaintance() {
        return acquaintance;
    }

    public void addValue(int id, int addValue) {
        value.put(id, value.get(id) + addValue);
    }

    public void delRelation(int id) {
        acquaintance.remove(id);
        value.remove(id);
    }

    public boolean strictEquals(Person person) {
        return true;
    }
}

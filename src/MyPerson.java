import com.oocourse.spec3.main.Message;
import com.oocourse.spec3.main.NoticeMessage;
import com.oocourse.spec3.main.Person;
import com.oocourse.spec3.main.Tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyPerson implements Person {

    private final int id;
    private final String name;
    private final int age;
    private final HashMap<Integer, Person> acquaintance = new HashMap<>();
    private final HashMap<Integer, Integer> value = new HashMap<>();

    private final HashMap<Integer, Tag> tags = new HashMap<>();

    private int bestId;
    private int bestValue;
    private int socialValue;

    private int money;

    private ArrayList<Message> messages = new ArrayList<>();
    private boolean noticeDirty;

    public MyPerson(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.bestId = id;
        this.bestValue = -1;
        this.socialValue = 0;
        this.money = 0;
        this.noticeDirty = false;
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

    public boolean containsTag(int id) {
        return tags.containsKey(id);
    }

    public Tag getTag(int id) {
        return tags.getOrDefault(id, null);
    }

    public void addTag(Tag tag) {
        if (!containsTag(tag.getId())) { //TO/DO* should delet?
            tags.put(tag.getId(), tag);
        }
    }

    public void delTag(int id) {
        if (containsTag(id)) { //TO/DO* should delet?
            tags.remove(id);
        }
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

        if (valueNum > bestValue ||
                (bestValue == valueNum && person.getId() < bestId)) {
            bestValue = valueNum;
            bestId = person.getId();
        }
    }

    public HashMap<Integer, Person> getAcquaintance() {
        return acquaintance;
    }

    public void addValue(int id, int addValue) {
        value.put(id, value.get(id) + addValue);

        if (id == bestId) {
            if (addValue >= 0) {
                bestValue += addValue;
            } else {
                rebuildNewBestId();
            }
        } else if (addValue > 0) {
            if (value.get(id) > bestValue
                    || (value.get(id) == bestValue && id < bestId)) {
                bestId = id;
                bestValue = value.get(id);
            }
        }

    }

    public void delRelation(int id) {
        acquaintance.remove(id);
        value.remove(id);

        if (id == bestId) {
            rebuildNewBestId();
        }
    }

    public void rebuildNewBestId() {
        bestValue = -1;
        bestId = id;
        for (Integer pid : acquaintance.keySet()) {
            int valueNum = value.get(pid);
            if (valueNum > bestValue ||
                    (bestValue == valueNum && pid < bestId)) {
                bestId = pid;
                bestValue = valueNum;
            }
        }
    }

    public void delPersonFromTag(Person person) {
        for (Tag tag : tags.values()) {
            if (tag.hasPerson(person)) {
                tag.delPerson(person);
            }
        }
    }

    public int getBestId() {
        return bestId;
    }

    //hw11-------------------------------------------------------

    public void addSocialValue(int num) {
        socialValue += num;
    }

    public int getSocialValue() {
        return socialValue;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public List<Message> getReceivedMessages() {
        ArrayList<Message> msg = new ArrayList<>();
        for (int i = 0; i < messages.size() && i <= 4; i++) {
            msg.add(messages.get(i));
        }
        return msg;
    }

    public void addMoney(int num) {
        money += num;
    }

    public int getMoney() {
        return money;
    }

    public void addMessage(Message message) {
        if (message instanceof NoticeMessage) {
            noticeDirty = true;
        }
        messages.add(0, message);
    }

    public void clearNotices() {
        if (noticeDirty) {
            messages.removeIf(message -> message instanceof NoticeMessage);
            noticeDirty = false;
        }
    }

}

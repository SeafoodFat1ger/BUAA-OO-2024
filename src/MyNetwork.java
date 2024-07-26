import com.oocourse.spec2.exceptions.EqualPersonIdException;
import com.oocourse.spec2.exceptions.PersonIdNotFoundException;
import com.oocourse.spec2.exceptions.RelationNotFoundException;
import com.oocourse.spec2.exceptions.TagIdNotFoundException;
import com.oocourse.spec2.exceptions.EqualTagIdException;
import com.oocourse.spec2.exceptions.EqualRelationException;
import com.oocourse.spec2.exceptions.AcquaintanceNotFoundException;
import com.oocourse.spec2.exceptions.PathNotFoundException;
import com.oocourse.spec2.main.Network;
import com.oocourse.spec2.main.Person;
import com.oocourse.spec2.main.Tag;

import java.util.HashMap;

public class MyNetwork implements Network {
    private final HashMap<Integer, Person> persons = new HashMap<>();

    private final DisjointSet disjointSet = new DisjointSet();
    private int blockSum;
    private int tripleSum;

    private int coupleSum;
    private boolean dirtyDisMap;

    //key为起点（小id） vale为: key是终点 vale是距离
    private HashMap<Integer, HashMap<Integer, Integer>> disMap = new HashMap<>();

    public MyNetwork() {
        blockSum = 0;
        tripleSum = 0;
        coupleSum = 0;
        dirtyDisMap = false;
    }

    public boolean containsPerson(int id) {
        return persons.containsKey(id);
    }

    public Person getPerson(int id) {
        return persons.getOrDefault(id, null);
    }

    public void addPerson(Person person) throws EqualPersonIdException {
        int id = person.getId();
        if (persons.containsKey(id)) {
            throw new MyEqualPersonIdException(id);
        } else {
            persons.put(id, person);
            disjointSet.add(id);//TO/DO*
            blockSum++;
        }
    }

    public void personNotFind(int id1, int id2) throws PersonIdNotFoundException {
        if (!persons.containsKey(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (!persons.containsKey(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        }
    }

    public void addRelation(int id1, int id2, int value)
            throws PersonIdNotFoundException, EqualRelationException {

        if (!persons.containsKey(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (!persons.containsKey(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        }
        Person person1 = persons.get(id1);
        Person person2 = persons.get(id2);

        if (person1.isLinked(person2)) { //id相等也会进入
            throw new MyEqualRelationException(id1, id2);
        }

        //id在这里不可能相等了
        if (((MyPerson) person1).getAcquaintance().values().size()
                < ((MyPerson) person2).getAcquaintance().values().size()) {
            for (Person person : ((MyPerson) person1).getAcquaintance().values()) {
                if (person.isLinked(person2)) {
                    tripleSum++;
                }
            }
        } else {
            for (Person person : ((MyPerson) person2).getAcquaintance().values()) {
                if (person.isLinked(person1)) {
                    tripleSum++;
                }
            }
        }


        //TO/DO--------------------------------------
        //if (!isCircle(id1, id2)) {
        //    blockSum--;
        //}

        int oldId1 = ((MyPerson) person1).getBestId();
        int oldId2 = ((MyPerson) person2).getBestId();

        ((MyPerson) person1).addRelation(person2, value);
        ((MyPerson) person2).addRelation(person1, value);

        updateCoupleSum(id1, id2, oldId1, oldId2);

        if (!disjointSet.isDirty()) {
            if (disjointSet.merge(id1, id2) == 0) {
                blockSum--;
            }
        }
        //TO/DO------------------------------------

        dirtyDisMap = true;
    }

    public void modifyRelation(int id1, int id2, int value) throws PersonIdNotFoundException,
            EqualPersonIdException, RelationNotFoundException {

        if (!persons.containsKey(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (!persons.containsKey(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        } else if (id1 == id2) {
            throw new MyEqualPersonIdException(id1);
        }
        Person person1 = persons.get(id1);
        Person person2 = persons.get(id2);

        int oldId1 = ((MyPerson) person1).getBestId();
        int oldId2 = ((MyPerson) person2).getBestId();

        if (!person1.isLinked(person2)) {
            throw new MyRelationNotFoundException(id1, id2);
        }
        if (person1.queryValue(person2) + value > 0) {
            ((MyPerson) person1).addValue(id2, value);
            ((MyPerson) person2).addValue(id1, value);
        } else {
            ((MyPerson) person1).delRelation(id2);
            ((MyPerson) person2).delRelation(id1);

            if (((MyPerson) person1).getAcquaintance().values().size()
                    < ((MyPerson) person2).getAcquaintance().values().size()) {
                for (Person person : ((MyPerson) person1).getAcquaintance().values()) {
                    if (person.isLinked(person2)) {
                        //原本连的三元环断开了
                        tripleSum--;
                    }
                }
            } else {
                for (Person person : ((MyPerson) person2).getAcquaintance().values()) {
                    if (person.isLinked(person1)) {
                        //原本连的三元环断开了
                        tripleSum--;
                    }
                }
            }

            ((MyPerson) person1).delPersonFromTag(person2);
            ((MyPerson) person2).delPersonFromTag(person1);

            //TO/DO--------------------------------------
            //modifyDisJointSet(id1, id2);
            //TO/DO--------------------------------------
            disjointSet.setDirty(true);
            dirtyDisMap = true;
        }
        updateCoupleSum(id1, id2, oldId1, oldId2);

    }

    public int queryValue(int id1, int id2) throws PersonIdNotFoundException,
            RelationNotFoundException {

        if (!persons.containsKey(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (!persons.containsKey(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        }

        Person person1 = persons.get(id1);
        Person person2 = persons.get(id2);
        if (!person1.isLinked(person2)) {
            throw new MyRelationNotFoundException(id1, id2);
        }

        return person1.queryValue(person2);
    }

    public void rebulidDisjoinset() {
        blockSum = 0;
        //disjointSet.clearAll(); woc clear好像是O(n)
        for (int id : persons.keySet()) {
            disjointSet.add(id);
            blockSum++;
        }
        for (int id1 : persons.keySet()) {
            MyPerson myPerson = (MyPerson) persons.get(id1);
            for (int id2 : myPerson.getAcquaintance().keySet()) {
                if (disjointSet.merge(id1, id2) == 0) {
                    blockSum--;
                }
            }
        }
    }

    public boolean isCircle(int id1, int id2) throws PersonIdNotFoundException {
        if (!persons.containsKey(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (!persons.containsKey(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        }
        if (disjointSet.isDirty()) {
            disjointSet.setDirty(false);
            rebulidDisjoinset();
        }
        return disjointSet.find(id1) == disjointSet.find(id2);
    }

    public int queryBlockSum() {
        if (disjointSet.isDirty()) {
            disjointSet.setDirty(false);
            rebulidDisjoinset();
        }
        return blockSum;
    }

    public int queryTripleSum() {
        return tripleSum;
    }

    public void addTag(int personId, Tag tag) throws
            PersonIdNotFoundException, EqualTagIdException {
        MyNetwork2.addTag(this, personId, tag);
    }

    public void addPersonToTag(int personId1, int personId2, int tagId) throws
            PersonIdNotFoundException, RelationNotFoundException,
            TagIdNotFoundException, EqualPersonIdException {
        MyNetwork2.addPersonToTag(this, personId1, personId2, tagId);
    }

    public int queryTagValueSum(int personId, int tagId) throws
            PersonIdNotFoundException, TagIdNotFoundException {
        return MyNetwork2.queryTagValueSum(this, personId, tagId);
    }

    public int queryTagAgeVar(int personId, int tagId) throws
            PersonIdNotFoundException, TagIdNotFoundException {
        return MyNetwork2.queryTagAgeVar(this, personId, tagId);
    }

    public void delPersonFromTag(int personId1, int personId2, int tagId)
            throws PersonIdNotFoundException, TagIdNotFoundException {
        MyNetwork2.delPersonFromTag(this, personId1, personId2, tagId);
    }

    public void delTag(int personId, int tagId)
            throws PersonIdNotFoundException, TagIdNotFoundException {
        MyNetwork2.delTag(this, personId, tagId);
    }

    public int queryBestAcquaintance(int id) throws
            PersonIdNotFoundException, AcquaintanceNotFoundException {
        return MyNetwork2.queryBestAcquaintance(this, id);
    }

    public void updateCoupleSum(int id1, int id2, int oldId1, int oldId2) {
        MyPerson p1 = (MyPerson) getPerson(id1);
        MyPerson p2 = (MyPerson) getPerson(id2);
        MyPerson oldP1 = (MyPerson) getPerson(oldId1);
        MyPerson oldP2 = (MyPerson) getPerson(oldId2);
        int newId1 = p1.getBestId();
        int newId2 = p2.getBestId();
        MyPerson newP1 = (MyPerson) getPerson(newId1);
        MyPerson newP2 = (MyPerson) getPerson(newId2);

        boolean bestChange1 = (oldId1 != newId1);
        boolean bestChange2 = (oldId2 != newId2);
        if (bestChange1 || bestChange2) {
            if (newId1 == id2 && newId2 == id1) { //现在凑了一对cp
                coupleSum++;
            }
            if (oldId1 == id2 && oldId2 == id1) { //原来拆了一对cp
                coupleSum--;
            }

            if (bestChange1) {
                if (oldId1 != id1 && newId1 == id2 && oldP1.getBestId() == id1) {
                    //原来1的cp存在，原来1的cp的现在的cp还是1，但1的现在cp是2了
                    coupleSum--;
                }
                if (newId1 != id1 && newId1 != id2 && newP1.getBestId() == id1) {
                    //现在1的cp存在，现在1的cp的cp是1，1的现在cp还不是2
                    coupleSum++;
                }
            }
            if (bestChange2) {
                if (oldId2 != id2 && newId2 == id1 && oldP2.getBestId() == id2) {
                    //原来2的cp存在，原来2的cp的现在的cp还是2，但2的现在cp是1
                    coupleSum--;
                }
                if (newId2 != id2 && newId2 != id1 && newP2.getBestId() == id2) {
                    //现在2的cp存在，现在2的cp的cp是2，2的现在cp还不是1
                    coupleSum++;
                }
            }
        }

    }

    public int queryCoupleSum() {
        return coupleSum;
    }

    public int queryShortestPath(int id1, int id2)
            throws PersonIdNotFoundException, PathNotFoundException {
        return MyNetwork2.queryShortestPath(this, id1, id2);
    }

    public Person[] getPersons() {
        return null;
    }

    public HashMap<Integer, Person> getPersonMap() {
        return persons;
    }

    public HashMap<Integer, HashMap<Integer, Integer>> getDisMap() {
        return disMap;
    }

    public void putDisMap(int key, HashMap<Integer, Integer> value) {
        disMap.put(key, value);
    }

    public boolean isDirtyDisMap() {
        return dirtyDisMap;
    }

    public void clearDisMap() {
        disMap.clear();
    }

    public void setDirtyDisMap(boolean dirtyDisMap) {
        this.dirtyDisMap = dirtyDisMap;
    }
}

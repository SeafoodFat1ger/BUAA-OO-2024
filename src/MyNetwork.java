import com.oocourse.spec1.exceptions.EqualPersonIdException;
import com.oocourse.spec1.exceptions.EqualRelationException;
import com.oocourse.spec1.exceptions.PersonIdNotFoundException;
import com.oocourse.spec1.exceptions.RelationNotFoundException;
import com.oocourse.spec1.main.Network;
import com.oocourse.spec1.main.Person;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class MyNetwork implements Network {
    private final HashMap<Integer, Person> persons = new HashMap<>();

    private final DisjointSet disjointSet = new DisjointSet();

    private HashMap<Integer, HashSet<Integer>> modifyEdge;
    private HashMap<Integer, HashSet<Integer>> addEdge;
    private int blockSum;
    private int tripleSum;

    public MyNetwork() {
        modifyEdge = new HashMap<>();
        addEdge = new HashMap<>();
        blockSum = 0;
        tripleSum = 0;
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
            System.out.println("addPerson:  " + blockSum);
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
        ((MyPerson) person1).delRelation(id2);
        ((MyPerson) person2).delRelation(id1);
        if (((MyPerson) person1).getAcquaintance().size() <
                ((MyPerson) person2).getAcquaintance().size()) {
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
        /*
        if (!isCircle(id1, id2)) {
            blockSum--;
        }
         */

        ((MyPerson) person1).addRelation(person2, value);
        ((MyPerson) person2).addRelation(person1, value);

        if (!modifyEdge.isEmpty()) {
            addAddEdge(id1, id2);
        } else {
            if (disjointSet.find(id1) != disjointSet.find(id2)) {
                blockSum--;
                System.out.println("addRelation:  " + blockSum);

            }
            disjointSet.merge(id1, id2);
        }
        //disjointSet.merge(id1, id2);//TO/DO*

    }

    public void addDisJointSet(int id1, int id2) {
        if (disjointSet.find(id1) != disjointSet.find(id2)) {
            blockSum--;
            System.out.println("addRelationDelay:  " + blockSum);

        }
        disjointSet.merge(id1, id2);
    }

    public void addAddEdge(int id1, int id2) {
        int max = Math.max(id1, id2);
        int min = Math.min(id1, id2);
        //增减抵消
        if (modifyEdge.containsKey(min)) {
            HashSet<Integer> minSet = modifyEdge.get(min);
            if (minSet.contains(max)) {
                minSet.remove(max);
                if (minSet.isEmpty()) {
                    modifyEdge.remove(min);
                }
                return;
            }
        }
        //需要增
        if (addEdge.containsKey(min)) {
            addEdge.get(min).add(max);
        } else {
            HashSet<Integer> minSet = new HashSet<>();
            minSet.add(max);
            addEdge.put(min, minSet);
        }

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
        if (!person1.isLinked(person2)) {
            throw new MyRelationNotFoundException(id1, id2);
        }

        if (person1.queryValue(person2) + value > 0) {
            ((MyPerson) person1).addValue(id2, value);
            ((MyPerson) person2).addValue(id1, value);
        } else {
            ((MyPerson) person1).delRelation(id2);
            ((MyPerson) person2).delRelation(id1);
            if (((MyPerson) person1).getAcquaintance().size() <
                    ((MyPerson) person2).getAcquaintance().size()) {
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

            addModifyEdge(id1, id2);
            //modifyDisJointSet(id1, id2);
        }

    }

    public void addModifyEdge(int id1, int id2) {
        int max = Math.max(id1, id2);
        int min = Math.min(id1, id2);
        //增减抵消
        if (addEdge.containsKey(min)) {
            HashSet<Integer> minSet = addEdge.get(min);
            if (minSet.contains(max)) {
                minSet.remove(max);
                if (minSet.isEmpty()) {
                    addEdge.remove(min);
                }
                return;
            }
        }
        //需要减
        if (modifyEdge.containsKey(min)) {
            modifyEdge.get(min).add(max);
        } else {
            HashSet<Integer> minSet = new HashSet<>();
            minSet.add(max);
            modifyEdge.put(min, minSet);
        }

    }

    public void modifyDisJointSet(int id1, int id2) {
        System.out.printf("id1:%d, id2:%d\n", id1, id2);
        if (disjointSet.find(id1) != disjointSet.find(id2)) {
            return;
        }
        disjointSet.modify(id1, id1);
        disjointSet.modify(id2, id2); //TO/DO* id完全有可能是负数
        ArrayList<Integer> visited = new ArrayList<>();

        //所有与 id1 相连的点的 father[] 标记为 leftId
        dfs(id1, id1, visited);

        //id2 检查父节点是否为 id1
        if (disjointSet.find(id2) != id1) {
            blockSum++;
            System.out.printf("id1:%d, id2:%d\n", id1, id2);
            System.out.println("modifyRelationDelay:  " + blockSum);
            visited.clear();
            disjointSet.modify(id2, id2);
            dfs(id2, id2, visited);
        }
        //disjointSet.printAll();
    }

    public void dfs(int id1, int id2, ArrayList<Integer> visited) {
        visited.add(id2);
        Person person2 = persons.get(id2);
        for (Integer id : ((MyPerson) person2).getAcquaintance().keySet()) {
            if (visited.contains(id)) {
                continue;
            }

            //addRelation已经添加过的熟人 但我这个时候还没有维护那个并查集啊啊啊啊啊
            int max = Math.max(id2, id);
            int min = Math.min(id2, id);
            if (addEdge.containsKey(min)) {
                HashSet<Integer> minSet = addEdge.get(min);
                if (minSet.contains(max)) {
                    continue;
                }
            }

            disjointSet.modify(id, id1);
            dfs(id1, id, visited);
        }
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

    public void rebuildDisJointSet() {
        if (!modifyEdge.isEmpty()) {
            for (Integer i : modifyEdge.keySet()) {
                HashSet<Integer> set = modifyEdge.get(i);
                for (Integer j : set) {
                    modifyDisJointSet(i, j);
                }
            }
            modifyEdge.clear();
        }

        if (!addEdge.isEmpty()) {
            for (Integer i : addEdge.keySet()) {
                HashSet<Integer> set = addEdge.get(i);
                for (Integer j : set) {
                    addDisJointSet(i, j);
                }
            }
            addEdge.clear();
        }

    }

    public boolean isCircle(int id1, int id2) throws PersonIdNotFoundException {
        if (!persons.containsKey(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (!persons.containsKey(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        }
        //延迟重建1
        printAcquaintance();
        disjointSet.printAll();
        rebuildDisJointSet();
        System.out.println("******");
        disjointSet.printAll();

        return disjointSet.find(id1) == disjointSet.find(id2);
    }

    public int queryBlockSum() {
        //延迟重建2
        disjointSet.printAll();
        rebuildDisJointSet();
        System.out.println("***********");
        disjointSet.printAll();
        printAcquaintance();
        return blockSum;
    }

    public void printAcquaintance() {
        for (Person pr : persons.values()) {
            System.out.println("**" + pr.getId() + "**");
            for (Integer id : ((MyPerson) pr).getAcquaintance().keySet()) {
                System.out.print(id + " ");
            }
            System.out.println(" ");
            System.out.println("******");
        }
    }

    public int queryTripleSum() {
        return tripleSum;
    }

    public Person[] getPersons() {
        return null;
    }

    public DisjointSet getDisjointSet() {
        return disjointSet;
    }

    public HashMap<Integer, Person> getPersonMap() {
        return persons;
    }
}


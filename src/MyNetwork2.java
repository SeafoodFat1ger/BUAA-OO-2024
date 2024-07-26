import com.oocourse.spec2.exceptions.EqualPersonIdException;
import com.oocourse.spec2.exceptions.PersonIdNotFoundException;
import com.oocourse.spec2.exceptions.RelationNotFoundException;
import com.oocourse.spec2.exceptions.TagIdNotFoundException;
import com.oocourse.spec2.exceptions.EqualTagIdException;
import com.oocourse.spec2.exceptions.AcquaintanceNotFoundException;
import com.oocourse.spec2.exceptions.PathNotFoundException;
import com.oocourse.spec2.main.Person;
import com.oocourse.spec2.main.Tag;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.HashSet;
import java.util.Queue;
import java.util.Map;

public class MyNetwork2 {
    public static void addTag(MyNetwork myNetwork, int personId, Tag tag) throws
            PersonIdNotFoundException, EqualTagIdException {
        HashMap<Integer, Person> persons = myNetwork.getPersonMap();
        if (!persons.containsKey(personId)) {
            throw new MyPersonIdNotFoundException(personId);
        } else if (persons.get(personId).containsTag(tag.getId())) {
            throw new MyEqualTagIdException(tag.getId());
        }
        persons.get(personId).addTag(tag);
    }

    public static void addPersonToTag(MyNetwork myNetwork, int personId1, int personId2, int tagId)
            throws PersonIdNotFoundException, RelationNotFoundException,
            TagIdNotFoundException, EqualPersonIdException {
        HashMap<Integer, Person> persons = myNetwork.getPersonMap();
        if (!persons.containsKey(personId1)) {
            throw new MyPersonIdNotFoundException(personId1);
        } else if (!persons.containsKey(personId2)) {
            throw new MyPersonIdNotFoundException(personId2);
        } else if (personId1 == personId2) {
            throw new MyEqualPersonIdException(personId1);
        }
        Person person1 = persons.get(personId1);
        Person person2 = persons.get(personId2);
        if (!person2.isLinked(person1)) {
            throw new MyRelationNotFoundException(personId1, personId2);
        } else if (!person2.containsTag(tagId)) {
            throw new MyTagIdNotFoundException(tagId);
        }
        Tag tag = person2.getTag(tagId);
        if (tag.hasPerson(person1)) {
            throw new MyEqualPersonIdException(personId1);
        }

        if (tag.getSize() <= 1111) {
            tag.addPerson(person1);
        }
    }

    public static int queryTagValueSum(MyNetwork myNetwork, int personId, int tagId) throws
            PersonIdNotFoundException, TagIdNotFoundException {
        HashMap<Integer, Person> persons = myNetwork.getPersonMap();
        if (!persons.containsKey(personId)) {
            throw new MyPersonIdNotFoundException(personId);
        }
        Person person = persons.get(personId);
        if (!person.containsTag(tagId)) {
            throw new MyTagIdNotFoundException(tagId);
        }
        Tag tag = person.getTag(tagId);
        return tag.getValueSum();
    }

    public static int queryTagAgeVar(MyNetwork myNetwork, int personId, int tagId) throws
            PersonIdNotFoundException, TagIdNotFoundException {
        HashMap<Integer, Person> persons = myNetwork.getPersonMap();

        if (!persons.containsKey(personId)) {
            throw new MyPersonIdNotFoundException(personId);
        }
        Person person = persons.get(personId);
        if (!person.containsTag(tagId)) {
            throw new MyTagIdNotFoundException(tagId);
        }
        Tag tag = person.getTag(tagId);
        return tag.getAgeVar();
    }

    public static void delPersonFromTag(MyNetwork myNetwork, int personId1, int personId2
            , int tagId) throws PersonIdNotFoundException, TagIdNotFoundException {
        HashMap<Integer, Person> persons = myNetwork.getPersonMap();
        if (!persons.containsKey(personId1)) {
            throw new MyPersonIdNotFoundException(personId1);
        } else if (!persons.containsKey(personId2)) {
            throw new MyPersonIdNotFoundException(personId2);
        }
        Person person1 = persons.get(personId1);
        Person person2 = persons.get(personId2);
        if (!person2.containsTag(tagId)) {
            throw new MyTagIdNotFoundException(tagId);
        }
        Tag tag = person2.getTag(tagId);
        if (!tag.hasPerson(person1)) {
            throw new MyPersonIdNotFoundException(personId1);
        }
        tag.delPerson(person1);
    }

    public static void delTag(MyNetwork myNetwork, int personId, int tagId)
            throws PersonIdNotFoundException, TagIdNotFoundException {
        HashMap<Integer, Person> persons = myNetwork.getPersonMap();
        if (!persons.containsKey(personId)) {
            throw new MyPersonIdNotFoundException(personId);
        }
        Person person = persons.get(personId);
        if (!person.containsTag(tagId)) {
            throw new MyTagIdNotFoundException(tagId);
        }
        person.delTag(tagId);
    }

    public static int queryBestAcquaintance(MyNetwork myNetwork, int id)
            throws PersonIdNotFoundException, AcquaintanceNotFoundException {
        HashMap<Integer, Person> persons = myNetwork.getPersonMap();
        if (!persons.containsKey(id)) {
            throw new MyPersonIdNotFoundException(id);
        }
        MyPerson person = (MyPerson) persons.get(id);
        if (person.getAcquaintance().isEmpty()) {
            throw new MyAcquaintanceNotFoundException(id);
        } else {
            return person.getBestId();
        }
    }

    public static HashMap<Integer, Integer> dijkstra(
            HashMap<Integer, Person> persons, int id) {
        HashMap<Integer, Integer> dis = new HashMap<>();
        HashMap<Integer, Boolean> vis = new HashMap<>();
        PriorityQueue<Node> heap = new PriorityQueue<>();

        for (Integer i : persons.keySet()) {
            dis.put(i, Integer.MAX_VALUE);
            vis.put(i, false);
        }
        dis.put(id, 0);
        heap.add(new Node(id, 0));

        while (!heap.isEmpty()) {
            Node cur = heap.poll();
            int curId = cur.getKey();
            if (vis.get(curId)) {
                continue;
            }
            vis.put(curId, true);
            for (Integer acId :
                    ((MyPerson) persons.get(curId)).getAcquaintance().keySet()) {
                if (!vis.get(acId) && dis.get(curId) + 1 < dis.get(acId)) {
                    dis.put(acId, dis.get(curId) + 1);
                    heap.add(new Node(acId, dis.get(acId)));
                }
            }
        }
        return dis;
    }

    public static int bfs(MyNetwork myNetwork, int id1, int id2) {
        HashMap<Integer, Person> persons = myNetwork.getPersonMap();
        Queue<Integer> queue = new LinkedList<>();
        HashSet<Integer> visited = new HashSet<>();
        Map<Integer, Integer> dis = new HashMap<>();
        queue.offer(id1);
        visited.add(id1);
        dis.put(id1, 0);
        while (!queue.isEmpty()) {
            int currentId = queue.poll();
            if (currentId == id2) {
                return dis.get(currentId);
            }

            MyPerson curPerson = (MyPerson) persons.get(currentId);
            for (Integer acPerson : curPerson.getAcquaintance().keySet()) {
                //System.out.printf("id: %d   acPerson: %d\n", id, acPerson);
                if (!visited.contains(acPerson)) {
                    queue.offer(acPerson);
                    dis.put(acPerson, dis.get(currentId) + 1);
                    visited.add(acPerson);
                }
            }
        }
        return -1;
    }

    public static int queryShortestPath(MyNetwork myNetwork, int id1, int id2)
            throws PersonIdNotFoundException, PathNotFoundException {
        HashMap<Integer, Person> persons = myNetwork.getPersonMap();
        if (!persons.containsKey(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (!persons.containsKey(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        }
        if (id1 == id2) {
            return 0;
        }

        int dis = bfs(myNetwork, id1, id2);
        if (dis == -1) {
            throw new MyPathNotFoundException(id1, id2);
        } else {
            return dis - 1;
        }
    }

    public static HashMap<Integer, Integer> zeroAndOneBfs(
            HashMap<Integer, Person> persons, int id) {
        HashMap<Integer, Integer> dis = new HashMap<>();
        HashMap<Integer, Boolean> vis = new HashMap<>();
        Deque<Node> dq = new LinkedList<>();

        for (Integer i : persons.keySet()) {
            dis.put(i, Integer.MAX_VALUE);
            vis.put(i, false);
        }
        dis.put(id, 0);
        dq.offer(new Node(id, 0));

        while (!dq.isEmpty()) {
            Node cur = dq.poll();
            int curId = cur.getKey();
            if (vis.get(curId)) {
                continue;
            }
            vis.put(curId, true);
            for (Integer acId :
                    ((MyPerson) persons.get(curId)).getAcquaintance().keySet()) {
                if (!vis.get(acId) && dis.get(curId) + 1 < dis.get(acId)) {
                    dis.put(acId, dis.get(curId) + 1);
                    dq.offerLast(new Node(acId, dis.get(acId)));
                }
            }
        }
        return dis;
    }
}

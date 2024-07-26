import static org.junit.Assert.*;

import com.oocourse.spec1.main.Person;
import org.junit.Test;

public class MyNetworkTest {

    private MyNetwork myNetwork = new MyNetwork();
    private MyNetwork oldNetwork = new MyNetwork();


    public void addPerson() throws Exception {
        for (int i = 0; i < 11; i++) {
            MyPerson person = new MyPerson(i, "1", 1);
            myNetwork.addPerson(person);
            MyPerson oldPerson = new MyPerson(i, "1", 1);//得一样hhh
            oldNetwork.addPerson(oldPerson);
        }
    }

    public void addRelTest(int i, int j) throws Exception {
        myNetwork.addRelation(i, j, 1);
        oldNetwork.addRelation(i, j, 1);
        testForTrip();
    }

    @Test
    public void queryTripleSum() throws Exception {
        addPerson();
        addRelTest(10, 3);
        addRelTest(10, 2);

        addRelTest(0, 1);
        addRelTest(0, 2);
        addRelTest(1, 2);

        addRelTest(2, 3);
        addRelTest(3, 4);
        addRelTest(2, 4);

        addRelTest(4, 1);
        addRelTest(4, 5);
        addRelTest(4, 6);
        addRelTest(5, 6);

        addRelTest(3, 6);
        addRelTest(3, 5);

        addRelTest(1, 7);
        addRelTest(1, 8);
        addRelTest(1, 9);

        addRelTest(7, 5);
        addRelTest(7, 8);
        addRelTest(9, 8);
        addRelTest(7, 9);

    }

    public void testForTrip() {
        Person[] persons = oldNetwork.getPersons();
        assertNotNull(persons);
        int len = persons.length;
        int sum = 0;
        for (int i = 0; i < len; i++) {
            for (int j = i + 1; j < len; j++) {
                for (int k = j + 1; k < len; k++) {
                    if (persons[i].isLinked(persons[j]) &&
                            persons[j].isLinked(persons[k]) &&
                            persons[k].isLinked(persons[i])) {
                        sum++;
                    }
                }
            }
        }
        int result = myNetwork.queryTripleSum();
        assertEquals(result, sum);

        Person[] newPersons = myNetwork.getPersons();
        assertNotNull(newPersons);
        int len1 = newPersons.length;
        assertEquals(len1, len);

        for (int i = 0; i < len1; i++) {
            MyPerson myPerson = (MyPerson) newPersons[i];
            MyPerson myOldPerson = (MyPerson) persons[i];
            if (myOldPerson.strictEquals(myPerson)) {
                continue;
            }
            assertEquals(true, false);
        }
    }
}
import static org.junit.Assert.*;

import com.oocourse.spec2.exceptions.AcquaintanceNotFoundException;
import com.oocourse.spec2.exceptions.PersonIdNotFoundException;
import com.oocourse.spec2.main.Person;
import org.junit.Test;

import java.util.ArrayList;

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

    public void addRelTest(int i, int j, int value) throws Exception {
        myNetwork.addRelation(i, j, value);
        oldNetwork.addRelation(i, j, value);
        testForCouple();
    }

    public void modRelTest(int i, int j, int value) throws Exception {
        myNetwork.modifyRelation(i, j, value);
        oldNetwork.modifyRelation(i, j, value);
        testForCouple();
    }

    @Test
    public void queryCoupleSum() throws Exception {
        addPerson();
        addRelTest(10, 3, 1);
        addRelTest(10, 2, 1);

        addRelTest(0, 1, 1);
        addRelTest(0, 2, 2);
        addRelTest(1, 2, 2);

        addRelTest(2, 3, 1);
        addRelTest(3, 4, 3);
        addRelTest(2, 4, 3);

        addRelTest(4, 1, 1);
        addRelTest(4, 5, 2);
        addRelTest(4, 6, 1);
        addRelTest(5, 6, 2);

        addRelTest(3, 6, 2);
        addRelTest(3, 5, 2);

        addRelTest(1, 7, 3);
        addRelTest(1, 8, 2);
        addRelTest(1, 9, 2);

        addRelTest(7, 5, 2);
        addRelTest(7, 8, 1);
        addRelTest(9, 8, 2);
        addRelTest(7, 9, 2);


        modRelTest(10, 3, -1);
        modRelTest(10, 2, -1);

        modRelTest(0, 1, 1);
        modRelTest(0, 2, -2);
        modRelTest(1, 2, -1);

        modRelTest(2, 3, 1);
        modRelTest(3, 4, -2);
        modRelTest(2, 4, -1);

        modRelTest(4, 1, -1);
        modRelTest(4, 5, -1);
        modRelTest(4, 6, 1);
        modRelTest(5, 6, -1);

        modRelTest(3, 6, -1);
        modRelTest(3, 5, -1);

        modRelTest(1, 7, -3);
        modRelTest(1, 8, -2);
        modRelTest(1, 9, -2);

        modRelTest(7, 5, -2);
        modRelTest(7, 8, -1);
        modRelTest(9, 8, -1);
        modRelTest(7, 9, 1);


    }

    public void testForCouple() throws AcquaintanceNotFoundException, PersonIdNotFoundException {
        Person[] persons = oldNetwork.getPersons();
        assertNotNull(persons);
        int len = persons.length;
        int sum = 0;
        ArrayList<Boolean> isOrphan = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            isOrphan.add(i, true);
            for (int j = 0; j < len; j++) {
                if (i != j && persons[i].isLinked(persons[j])) {
                    isOrphan.add(i, false);
                    break;
                }
            }
        }
        for (int i = 0; i < len; i++) {
            for (int j = i + 1; j < len; j++) {
                if (!isOrphan.get(i) && !isOrphan.get(j)
                        && oldNetwork.queryBestAcquaintance(persons[i].getId()) == persons[j].getId()
                        && oldNetwork.queryBestAcquaintance(persons[j].getId()) == persons[i].getId()) {
                    sum++;
                }
            }
        }
        int result = myNetwork.queryCoupleSum();
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
import static org.junit.Assert.*;

import com.oocourse.spec3.exceptions.*;
import com.oocourse.spec3.main.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Objects;

public class MyNetworkTest {

    private MyNetwork myNetwork = new MyNetwork();
    private MyNetwork oldNetwork = new MyNetwork();
    private ArrayList<MyPerson> persons = new ArrayList<>();
    private ArrayList<MyTag> tags = new ArrayList<>();

    public void addPerson() throws EqualPersonIdException {
        for (int i = 0; i < 10; i++) {
            MyPerson person = new MyPerson(i, "1", 1);
            persons.add(person);
            myNetwork.addPerson(person);
            oldNetwork.addPerson(person);
        }
    }

    public void addTag() throws PersonIdNotFoundException, EqualTagIdException, RelationNotFoundException, TagIdNotFoundException, EqualPersonIdException, EqualRelationException {
        MyTag tag = new MyTag(1);
        tags.add(tag);
        myNetwork.addTag(2, tag);
        myNetwork.addRelation(1, 2, 1);
        myNetwork.addRelation(1, 3, 1);
        myNetwork.addRelation(3, 2, 1);
        myNetwork.addRelation(3, 4, 1);
        myNetwork.addRelation(5, 4, 1);
        myNetwork.addRelation(1, 5, 1);
        myNetwork.addRelation(1, 4, 1);
        myNetwork.addRelation(3, 5, 1);
        myNetwork.addPersonToTag(1, 2, 1);
        myNetwork.addPersonToTag(3, 2, 1);

        MyTag tag1 = new MyTag(2);
        tags.add(tag1);
        myNetwork.addTag(3, tag1);
        myNetwork.addPersonToTag(1, 3, 2);
        myNetwork.addPersonToTag(2, 3, 2);
    }

    public void addEmojiMessage(int id, int emjId, int type, int pid1, int pid2) throws EqualEmojiIdException, EmojiIdNotFoundException, EqualPersonIdException, EqualMessageIdException, RelationNotFoundException, TagIdNotFoundException, MessageIdNotFoundException {
        Message message;
        if (type == 0) {
            message = new MyEmojiMessage(id, emjId, persons.get(pid1), persons.get(pid2));
        } else if (type == 1) {
            message = new MyEmojiMessage(id, emjId, persons.get(pid1), tags.get(0));
        } else {
            message = new MyEmojiMessage(id, emjId, persons.get(pid1), tags.get(1));
        }

        myNetwork.addMessage(message);
        oldNetwork.addMessage(message);

    }

    public void addNotice(int id, int type, int pid1, int pid2) throws EmojiIdNotFoundException, EqualPersonIdException, EqualMessageIdException {
        Message message;
        if (type == 0) {
            message = new MyNoticeMessage(id, "1", persons.get(pid1), persons.get(pid2));
        } else if (type == 1) {
            message = new MyNoticeMessage(id, "1", persons.get(pid1), tags.get(0));
        } else {
            message = new MyNoticeMessage(id, "1", persons.get(pid1), tags.get(1));
        }

        myNetwork.addMessage(message);
        oldNetwork.addMessage(message);
    }

    public void addRed(int id, int type, int pid1, int pid2) throws EmojiIdNotFoundException, EqualPersonIdException, EqualMessageIdException {
        Message message;
        if (type == 0) {
            message = new MyRedEnvelopeMessage(id, 1, persons.get(pid1), persons.get(pid2));
        } else if (type == 1) {
            message = new MyRedEnvelopeMessage(id, 1, persons.get(pid1), tags.get(0));
        } else {
            message = new MyRedEnvelopeMessage(id, 1, persons.get(pid1), tags.get(1));
        }

        myNetwork.addMessage(message);
        oldNetwork.addMessage(message);

    }


    @Test
    public void deleteColdEmoji() throws EqualPersonIdException, RelationNotFoundException, TagIdNotFoundException, PersonIdNotFoundException, EqualTagIdException, EqualRelationException, EqualEmojiIdException, EmojiIdNotFoundException, MessageIdNotFoundException, EqualMessageIdException {
        addPerson();
        addTag();

        myNetwork.storeEmojiId(113);
        oldNetwork.storeEmojiId(113);
        myNetwork.storeEmojiId(114);
        oldNetwork.storeEmojiId(114);
        myNetwork.storeEmojiId(115);
        oldNetwork.storeEmojiId(115);
        myNetwork.storeEmojiId(116);
        oldNetwork.storeEmojiId(116);
        myNetwork.storeEmojiId(117);
        oldNetwork.storeEmojiId(117);

        addNotice(1, 0, 3, 2);
        addNotice(2, 0, 2, 4);
        addEmojiMessage(3, 113, 0, 1, 3);
        addEmojiMessage(4, 114, 0, 1, 4);
        addEmojiMessage(5, 115, 0, 1, 5);
        addRed(6, 0, 1, 2);
        addRed(7, 0, 1, 2);

        myNetwork.sendMessage(3);
        oldNetwork.sendMessage(3);
        myNetwork.sendMessage(4);
        oldNetwork.sendMessage(4);
        myNetwork.sendMessage(5);
        oldNetwork.sendMessage(5);


        addEmojiMessage(8, 113, 0, 2, 3);
        addEmojiMessage(9, 113, 0, 1, 3);
        addEmojiMessage(10, 113, 0, 3, 5);

        myNetwork.sendMessage(8);
        oldNetwork.sendMessage(8);
        myNetwork.sendMessage(9);
        oldNetwork.sendMessage(9);
        myNetwork.sendMessage(10);
        oldNetwork.sendMessage(10);

        addEmojiMessage(11, 114, 0, 4, 3);
        addEmojiMessage(12, 114, 0, 1, 4);
        addEmojiMessage(13, 115, 0, 3, 2);


        myNetwork.sendMessage(11);
        oldNetwork.sendMessage(11);
        myNetwork.sendMessage(12);
        oldNetwork.sendMessage(12);
        myNetwork.sendMessage(13);
        oldNetwork.sendMessage(13);

        addEmojiMessage(14, 113, 0, 2, 3);
        addEmojiMessage(15, 114, 0, 1, 4);
        addEmojiMessage(16, 115, 0, 5, 4);
        addEmojiMessage(17, 116, 0, 2, 3);

        testForDelete(3);
    }

    public void testForDelete(int limit) {
        int result = myNetwork.deleteColdEmoji(limit);
        Message[] msg = myNetwork.getMessages();
        int[] el = myNetwork.getEmojiIdList();
        int[] ehl = myNetwork.getEmojiHeatList();
        //ensures8
        assertEquals(result, el.length);

        Message[] oldMsg = oldNetwork.getMessages();
        int[] oldEl = oldNetwork.getEmojiIdList();
        int[] oldEhl = oldNetwork.getEmojiHeatList();
        //ensures4
        assertEquals(el.length, ehl.length);

        //ensures1
        int result1 = 0;
        for (int i = 0; i < oldEl.length; i++) {
            if (oldEhl[i] >= limit) {
                result1++;
                boolean flag = false;
                for (int j = 0; j < el.length; j++) {
                    if (oldEl[i] == el[j]) {
                        flag = true;
                        break;
                    }
                }
                assertEquals(flag, true);
            }
        }

        //ensures2
        for (int i = 0; i < el.length; i++) {
            boolean flag = false;
            for (int j = 0; j < oldEl.length; j++) {
                if (oldEl[j] == el[i] && oldEhl[j] == ehl[i]) {
                    flag = true;
                    break;
                }
            }
            assertEquals(flag, true);
        }
        //ensures3
        assertEquals(result1, el.length);

        //ensures5
        int result2 = 0;
        for (int i = 0; i < oldMsg.length; i++) {
            if (oldMsg[i] instanceof EmojiMessage) {
                boolean have = false;
                for (int j = 0; j < el.length; j++) {
                    if (el[j] == ((EmojiMessage) oldMsg[i]).getEmojiId()) {
                        have = true;
                        break;
                    }
                }
                if (have) {
                    result2++;

                    boolean flag = false;
                    for (int j = 0; j < msg.length; j++) {
                        if (msg[j].equals(oldMsg[i])) {
                            flag = msgEqual(oldMsg[i], msg[j]);
                            break;
                        }
                    }
                    assertEquals(flag, true);
                }
            } else {
                //ensures6
                boolean flag = false;
                for (int j = 0; j < msg.length; j++) {
                    if (msg[j].equals(oldMsg[i])) {
                        flag = msgEqual(oldMsg[i], msg[j]);
                        break;
                    }
                }
                assertEquals(flag, true);
            }
        }
        //ensures7
        assertEquals(6, msg.length);
    }

    public boolean msgEqual(Message m1, Message m2) {
        if (m1.getType() == m2.getType() && m1.getId() == m2.getId() && m1.getSocialValue() == m2.getSocialValue() && Objects.equals(m1.getPerson1(), m2.getPerson1())) {
            if (m1 instanceof RedEnvelopeMessage && m2 instanceof RedEnvelopeMessage) {
                if (((RedEnvelopeMessage) m1).getMoney() != ((RedEnvelopeMessage) m2).getMoney()) {
                    return false;
                }
            } else if (m1 instanceof NoticeMessage && m2 instanceof NoticeMessage) {
                if (!(((NoticeMessage) m1).getString().equals(((NoticeMessage) m2).getString()))) {
                    return false;
                }
            } else if (m1 instanceof EmojiMessage && m2 instanceof EmojiMessage) {
                if (((EmojiMessage) m1).getEmojiId() != ((EmojiMessage) m2).getEmojiId()) {
                    return false;
                }
            } else {
                return false;
            }
            if (m1.getType() == 0) {
                return Objects.equals(m1.getPerson2(), m2.getPerson2());
            } else {
                return Objects.equals(m1.getTag(), m2.getTag());
            }
        }
        return false;
    }
}
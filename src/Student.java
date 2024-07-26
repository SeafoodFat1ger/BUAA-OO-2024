import com.oocourse.library3.LibraryBookId;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Student {
    private String studentId;
    private BookPool borrowBooks;
    private HashMap<LibraryBookId, LocalDate> times;
    private HashSet<LibraryBookId> overDue;
    private int score;

    public Student(String id) {
        this.studentId = id;
        this.borrowBooks = new BookPool();
        this.times = new HashMap<>();
        this.score = 10;
        this.overDue = new HashSet<>();
    }

    public boolean hasTypeB() {
        return borrowBooks.hasTypeB();
    }

    public boolean hasTypeBU() {
        return borrowBooks.hasTypeBU();
    }

    public boolean hasBookC(LibraryBookId bid) {
        return borrowBooks.hasBook(bid);
    }

    public void addBook(LocalDate date, LibraryBookId bid) {
        borrowBooks.addBook(bid);
        times.put(bid, date);
    }

    public void stuReturnBook(LibraryBookId bid) {
        borrowBooks.borrowBook(bid);
        times.remove(bid);
        overDue.remove(bid);
    }

    public boolean isOverDue(LocalDate date, LibraryBookId bid) {
        LocalDate store = times.get(bid);
        long cha = store.until(date, ChronoUnit.DAYS);
        long tmp = 0;
        //date > store + 30
        if (bid.isTypeB()) {
            tmp = 30;
        } else if (bid.isTypeC()) {
            tmp = 60;
        } else if (bid.isTypeBU()) {
            tmp = 7;
        } else if (bid.isTypeCU()) {
            tmp = 14;
        }
        return cha > tmp;
    }

    public LocalDate getTime(LibraryBookId bid) {
        return times.get(bid);
    }

    public void addTime(LibraryBookId bid) {
        LocalDate store = times.get(bid);
        LocalDate newDate = store.plusDays(30);
        times.put(bid, newDate);
    }

    public void updateScore(int x) {
        int old = score;
        score = Math.min(old + x, 20);
    }

    public void renewScore(LocalDate date) {
        for (Map.Entry<LibraryBookId, Integer> entry : borrowBooks.getBooks().entrySet()) {
            if (entry.getValue().equals(0) || overDue.contains(entry.getKey())) {
                continue;
            }
            LibraryBookId bid = entry.getKey();
            LocalDate store = times.get(bid);
            long tmp = 0;
            //date > store + 30
            if (bid.isTypeB()) {
                tmp = 30;
            } else if (bid.isTypeC()) {
                tmp = 60;
            } else if (bid.isTypeBU()) {
                tmp = 7;
            } else if (bid.isTypeCU()) {
                tmp = 14;
            }
            if (date.isEqual(store.plusDays(tmp)) || date.isAfter(store.plusDays(tmp))) {
                score = score - 2;
                overDue.add(entry.getKey());
            }
        }

    }

    public void renewScoreOpen(LocalDate date) {
        for (Map.Entry<LibraryBookId, Integer> entry : borrowBooks.getBooks().entrySet()) {
            if (entry.getValue().equals(0) || overDue.contains(entry.getKey())) {
                continue;
            }
            LibraryBookId bid = entry.getKey();
            LocalDate store = times.get(bid);
            long tmp = 0;
            //date > store + 30
            if (bid.isTypeB()) {
                tmp = 30;
            } else if (bid.isTypeC()) {
                tmp = 60;
            } else if (bid.isTypeBU()) {
                tmp = 7;
            } else if (bid.isTypeCU()) {
                tmp = 14;
            }
            if (date.isAfter(store.plusDays(tmp))) {
                score = score - 2;
                overDue.add(entry.getKey());
            }
        }

    }

    public int getScore() {
        return score;
    }

    public void orderOverdue(HashMap<LibraryBookId, Integer> map) {
        for (Integer integer : map.values()) {
            if (integer.equals(0)) {
                continue;
            }
            updateScore(-3 * integer);
        }
    }

}

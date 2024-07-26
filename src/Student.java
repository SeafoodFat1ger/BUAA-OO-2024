import com.oocourse.library2.LibraryBookId;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

public class Student {
    private String studentId;
    private BookPool borrowBooks;
    private HashMap<LibraryBookId, LocalDate> times;

    public Student(String id) {
        this.studentId = id;
        this.borrowBooks = new BookPool();
        this.times = new HashMap<>();
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

}

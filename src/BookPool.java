import com.oocourse.library3.LibraryBookId;

import java.util.HashMap;
import java.util.Map;

public class BookPool {
    private HashMap<LibraryBookId, Integer> books;

    public BookPool() {
        this.books = new HashMap<>();
    }

    public void initBook(Map<LibraryBookId, Integer> book) {
        books.putAll(book);
    }

    public void addBook(LibraryBookId bid) {
        if (books.containsKey(bid)) {
            int num = books.get(bid);
            books.put(bid, num + 1);
        } else {
            books.put(bid, 1);
        }
    }

    public boolean hasBook(LibraryBookId bid) {
        if (books.containsKey(bid)) {
            int num = books.get(bid);
            return num > 0;
        }
        return false;
    }

    public boolean canOrderBook(LibraryBookId bid) {
        if (books.containsKey(bid)) {
            int num = books.get(bid);
            if (num > 0) {
                books.put(bid, num - 1);
                return true;
            }

        }
        return false;
    }

    public void borrowBook(LibraryBookId bid) {
        int num = books.get(bid);
        books.put(bid, num - 1);
    }

    public boolean hasTypeB() {
        int cnt = 0;
        for (LibraryBookId bid : books.keySet()) {
            if (bid.isTypeB()) {
                cnt += books.get(bid);
            }
        }
        return cnt > 0;
    }

    public boolean hasTypeBU() {
        int cnt = 0;
        for (LibraryBookId bid : books.keySet()) {
            if (bid.isTypeBU()) {
                cnt += books.get(bid);
            }
        }
        return cnt > 0;
    }

    public int queryBook(LibraryBookId bid) {
        int num = 0;
        if (books.containsKey(bid)) {
            num = books.get(bid);
        }
        return num;
    }

    public HashMap<LibraryBookId, Integer> getBooks() {
        return books;
    }
}

import com.oocourse.library1.LibraryBookId;
import com.oocourse.library1.LibraryRequest;

import static com.oocourse.library1.LibrarySystem.PRINTER;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Library {
    private ArrangeLibrarian arrangeLibrarian;
    private BorrowLibrarian borrowLibrarian;
    private OrderLibrarian orderLibrarian;
    private Machine machine;
    private BookPool bookPool;
    private HashMap<String, Student> students;

    public Library() {
        this.arrangeLibrarian = new ArrangeLibrarian(this);
        this.borrowLibrarian = new BorrowLibrarian(this);
        this.orderLibrarian = new OrderLibrarian(this);
        this.machine = new Machine(this);
        this.bookPool = new BookPool();
        this.students = new HashMap<>();
    }

    public void initBooks(Map<LibraryBookId, Integer> books) {
        bookPool.initBooks(books);
    }

    public void borrowBook(LocalDate date, LibraryRequest request) {
        LibraryBookId bid = request.getBookId();
        if (bid.isTypeA()) {
            PRINTER.reject(date, request);
        } else if (bid.isTypeB()) {
            checkBorrowB(date, request);
        } else {
            checkBorrowC(date, request);
        }
    }

    public void checkBorrowB(LocalDate date, LibraryRequest request) {
        if (bookPool.hasBook(request.getBookId())) {
            bookPool.borrowBook(request.getBookId());
            borrowLibrarian.borrowBookB(date, request);
        } else {
            PRINTER.reject(date, request);
        }
    }

    public void checkBorrowC(LocalDate date, LibraryRequest request) {
        if (bookPool.hasBook(request.getBookId())) {
            bookPool.borrowBook(request.getBookId());
            borrowLibrarian.borrowBookC(date, request);
        } else {
            PRINTER.reject(date, request);
        }
    }

    public void returnBook(LocalDate date, LibraryRequest request) {
        borrowLibrarian.returnBook(date, request);
    }

    //-----------------------------------------
    public HashMap<String, Student> getStudents() {
        return students;
    }

    public BookPool getBookPool() {
        return bookPool;
    }

    public Machine getMachine() {
        return machine;
    }

    public BorrowLibrarian getBorrowLibrarian() {
        return borrowLibrarian;
    }

    public ArrangeLibrarian getArrangeLibrarian() {
        return arrangeLibrarian;
    }

    public OrderLibrarian getOrderLibrarian() {
        return orderLibrarian;
    }
}

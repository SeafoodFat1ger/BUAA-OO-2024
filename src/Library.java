import com.oocourse.library2.LibraryBookId;
import com.oocourse.library2.LibraryCommand;
import com.oocourse.library2.LibraryReqCmd;
import com.oocourse.library2.LibraryRequest;
import com.oocourse.library2.annotation.Trigger;

import static com.oocourse.library2.LibrarySystem.PRINTER;

import java.util.HashMap;
import java.util.Map;

public class Library {
    private ArrangeLibrarian arrangeLibrarian;
    private BorrowLibrarian borrowLibrarian;
    private OrderLibrarian orderLibrarian;
    private Machine machine;
    private BookPool bookPool;
    private HashMap<String, Student> students;
    private DriftLibrarian driftLibrarian;

    public Library() {
        this.arrangeLibrarian = new ArrangeLibrarian(this);
        this.borrowLibrarian = new BorrowLibrarian(this);
        this.orderLibrarian = new OrderLibrarian(this);
        this.machine = new Machine(this);
        this.bookPool = new BookPool();
        this.students = new HashMap<>();
        this.driftLibrarian = new DriftLibrarian(this);
    }

    //TO/DO trigger yuan -> bs
    @Trigger(from = "InitState", to = "BS")
    public void initBooks(Map<LibraryBookId, Integer> books) {
        bookPool.initBook(books);
    }

    public void borrowBook(LibraryCommand command) {
        LibraryRequest request = ((LibraryReqCmd) command).getRequest();
        LibraryBookId bid = request.getBookId();
        if (bid.isTypeA() || bid.isTypeAU()) {
            PRINTER.reject(command);
        } else if (bid.isTypeB()) {
            checkBorrowB(command);
        } else if (bid.isTypeC()) {
            checkBorrowC(command);
        } else if (bid.isTypeBU()) {
            checkBorrowBU(command);
        } else if (bid.isTypeCU()) {
            checkBorrowCU(command);
        }
    }

    public void checkBorrowB(LibraryCommand command) {
        LibraryRequest request = ((LibraryReqCmd) command).getRequest();
        if (bookPool.hasBook(request.getBookId())) {
            bookPool.borrowBook(request.getBookId());
            borrowLibrarian.borrowBookB(command);
        } else {
            PRINTER.reject(command);
        }
    }

    public void checkBorrowBU(LibraryCommand command) {
        LibraryRequest request = ((LibraryReqCmd) command).getRequest();
        BookPool driftBooks = driftLibrarian.getDriftBooks();
        if (driftBooks.hasBook(request.getBookId())) {
            driftBooks.borrowBook(request.getBookId());
            borrowLibrarian.borrowBookBU(command);
        } else {
            PRINTER.reject(command);
        }
    }

    public void checkBorrowC(LibraryCommand command) {
        LibraryRequest request = ((LibraryReqCmd) command).getRequest();
        if (bookPool.hasBook(request.getBookId())) {
            bookPool.borrowBook(request.getBookId());
            borrowLibrarian.borrowBookC(command);
        } else {
            PRINTER.reject(command);
        }
    }

    public void checkBorrowCU(LibraryCommand command) {
        LibraryRequest request = ((LibraryReqCmd) command).getRequest();
        BookPool driftBooks = driftLibrarian.getDriftBooks();
        if (driftBooks.hasBook(request.getBookId())) {
            driftBooks.borrowBook(request.getBookId());
            borrowLibrarian.borrowBookCU(command);
        } else {
            PRINTER.reject(command);
        }
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

    public DriftLibrarian getDriftLibrarian() {
        return driftLibrarian;
    }
}

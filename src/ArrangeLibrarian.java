import com.oocourse.library1.LibraryBookId;
import com.oocourse.library1.LibraryMoveInfo;
import com.oocourse.library1.LibraryRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

import static com.oocourse.library1.LibrarySystem.PRINTER;

public class ArrangeLibrarian {
    private Library library;
    private List<LibraryMoveInfo> moveInfo;
    private ArrayList<LibraryRequest> needArrangeOrder;

    public ArrangeLibrarian(Library library) {
        this.library = library;
        this.needArrangeOrder = new ArrayList<>();
        this.moveInfo = new ArrayList<>();
    }

    public void moveBorrowToShelf() {
        BookPool borrowPool = library.getBorrowLibrarian().getBorrowPool();
        BookPool shelfPool = library.getBookPool();
        for (Map.Entry<LibraryBookId, Integer> entry : borrowPool.getBooks().entrySet()) {
            if (entry.getValue() > 0) {
                LibraryMoveInfo smove = new LibraryMoveInfo(entry.getKey()
                        , "bro", "bs");
                for (int i = 0; i < entry.getValue(); i++) {
                    shelfPool.addBook(entry.getKey());
                    moveInfo.add(smove);
                }
            }
        }
        library.getBorrowLibrarian().clearBorrowPool();
    }

    public void moveOrderToShelf(HashMap<LibraryBookId, Integer> map) {
        BookPool shelfPool = library.getBookPool();
        for (Map.Entry<LibraryBookId, Integer> entry : map.entrySet()) {
            if (entry.getValue() > 0) {
                LibraryMoveInfo smove = new LibraryMoveInfo(entry.getKey()
                        , "ao", "bs");
                for (int i = 0; i < entry.getValue(); i++) {
                    shelfPool.addBook(entry.getKey());
                    moveInfo.add(smove);
                }
            }
        }
    }

    public void printInfo(LocalDate date) {
        PRINTER.move(date, moveInfo);
        moveInfo.clear();
    }

    public void addNeedArrangeOrder(LibraryRequest request) {
        needArrangeOrder.add(request);
    }

    public void arrangeOrder(LocalDate date) {
        Iterator<LibraryRequest> iterator = needArrangeOrder.iterator();
        while (iterator.hasNext()) {
            LibraryRequest request = iterator.next();
            if (haveBookToOrder(date, request)) {
                iterator.remove();
            }
        }
    }

    public boolean haveBookToOrder(LocalDate date, LibraryRequest request) {
        LibraryBookId bookId = request.getBookId();
        if (library.getBookPool().canOrderBook(bookId)) {
            library.getOrderLibrarian().addOrderBook(date, request);
            LibraryMoveInfo smove = new LibraryMoveInfo(request.getBookId()
                    , "bs", "ao", request.getStudentId());
            moveInfo.add(smove);
            return true;
        } else {
            return false;
        }
    }

}

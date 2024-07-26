import com.oocourse.library2.LibraryBookId;
import com.oocourse.library2.LibraryMoveInfo;
import com.oocourse.library2.LibraryRequest;
import com.oocourse.library2.annotation.Trigger;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

import static com.oocourse.library2.LibraryBookId.Type.B;
import static com.oocourse.library2.LibraryBookId.Type.C;
import static com.oocourse.library2.LibrarySystem.PRINTER;

public class ArrangeLibrarian {
    private Library library;
    private List<LibraryMoveInfo> moveInfo;
    private ArrayList<LibraryRequest> needArrangeOrder;

    public ArrangeLibrarian(Library library) {
        this.library = library;
        this.needArrangeOrder = new ArrayList<>();
        this.moveInfo = new ArrayList<>();
    }

    //TO/DO trigger bo->bs
    @Trigger(from = "BO", to = "BS")
    public void moveBorrowToShelf() {
        BookPool borrowPool = library.getBorrowLibrarian().getBorrowPool();
        BookPool shelfPool = library.getBookPool();
        for (Map.Entry<LibraryBookId, Integer> entry : borrowPool.getBooks().entrySet()) {
            if (entry.getValue() > 0) {
                LibraryBookId oldBid = entry.getKey();
                LibraryBookId newBid;
                if (entry.getKey().isTypeBU()) {
                    newBid = new LibraryBookId(B, oldBid.getUid());
                } else if (entry.getKey().isTypeCU()) {
                    newBid = new LibraryBookId(C, oldBid.getUid());
                } else {
                    newBid = new LibraryBookId(oldBid.getType(), oldBid.getUid());
                }
                LibraryMoveInfo smove = new LibraryMoveInfo(entry.getKey()
                        , "bro", "bs");
                for (int i = 0; i < entry.getValue(); i++) {
                    shelfPool.addBook(newBid);
                    moveInfo.add(smove);
                }
            }
        }
        library.getBorrowLibrarian().clearBorrowPool();
    }

    //TO/DO trigger bo->bdc
    @Trigger(from = "BO", to = "BDC")
    public void moveBorrowToDrift() {
        BookPool borrowDrift = library.getBorrowLibrarian().getBorrowDrift();
        BookPool driftPool = library.getDriftLibrarian().getDriftBooks();
        for (Map.Entry<LibraryBookId, Integer> entry : borrowDrift.getBooks().entrySet()) {
            if (entry.getValue() > 0) {
                LibraryMoveInfo smove = new LibraryMoveInfo(entry.getKey()
                        , "bro", "bdc");
                for (int i = 0; i < entry.getValue(); i++) {
                    driftPool.addBook(entry.getKey());
                    moveInfo.add(smove);
                }
            }
        }
        library.getBorrowLibrarian().clearBorrowDrift();
    }

    //TO/DO trigger ao->bs
    @Trigger(from = "AO", to = "BS")
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

    //TO/DO trigger bs->ao
    @Trigger(from = "BS", to = "AO")
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

    public boolean query4CanRenew(LibraryBookId bid) {
        for (LibraryRequest request : needArrangeOrder) {
            if (request.getBookId().equals(bid)) {
                return true;
            }
        }
        return false;
    }

}

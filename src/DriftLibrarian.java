import com.oocourse.library2.LibraryBookId;
import com.oocourse.library2.LibraryCommand;
import com.oocourse.library2.LibraryReqCmd;
import com.oocourse.library2.LibraryRequest;
import com.oocourse.library2.annotation.Trigger;

import java.util.HashMap;

import static com.oocourse.library2.LibrarySystem.PRINTER;

public class DriftLibrarian {
    private BookPool driftBooks;
    private Library library;
    private HashMap<LibraryBookId, Integer> times;

    public DriftLibrarian(Library library) {
        this.library = library;
        this.driftBooks = new BookPool();
        this.times = new HashMap<>();
    }

    //TO/DO trigger yuan -> bdc
    @Trigger(from = "InitState", to = "BDC")
    public void addDriftBook(LibraryCommand command) {
        LibraryRequest request = ((LibraryReqCmd) command).getRequest();
        driftBooks.addBook(request.getBookId());
        times.put(request.getBookId(), 0);
        PRINTER.accept(command);
    }

    public void addTimes(LibraryBookId bid) {
        times.put(bid, times.get(bid) + 1);
    }

    public int getTimes(LibraryBookId bid) {
        return times.get(bid);
    }

    public BookPool getDriftBooks() {
        return driftBooks;
    }
}

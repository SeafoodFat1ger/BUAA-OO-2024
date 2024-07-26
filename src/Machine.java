import com.oocourse.library1.LibraryBookId;
import com.oocourse.library1.LibraryRequest;

import static com.oocourse.library1.LibrarySystem.PRINTER;

import java.time.LocalDate;

public class Machine {
    private Library library;

    public Machine(Library library) {
        this.library = library;
    }

    public void queryBook(LocalDate date, LibraryRequest request) {
        LibraryBookId bid = request.getBookId();
        int num = library.getBookPool().queryBook(bid);
        PRINTER.info(date, bid, num);
    }

}

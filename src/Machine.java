import com.oocourse.library2.LibraryBookId;
import com.oocourse.library2.LibraryCommand;
import com.oocourse.library2.LibraryReqCmd;
import com.oocourse.library2.LibraryRequest;

import static com.oocourse.library2.LibrarySystem.PRINTER;

public class Machine {
    private Library library;

    public Machine(Library library) {
        this.library = library;
    }

    public void queryBook(LibraryCommand command) {
        LibraryRequest request = ((LibraryReqCmd) command).getRequest();
        LibraryBookId bid = request.getBookId();
        int num;
        if (bid.isTypeAU() || bid.isTypeBU() || bid.isTypeCU()) {
            num = library.getDriftLibrarian().getDriftBooks().queryBook(bid);
        } else {
            num = library.getBookPool().queryBook(bid);

        }
        PRINTER.info(command, num);
    }

}

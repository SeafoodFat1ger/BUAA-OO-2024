import com.oocourse.library3.LibraryBookId;
import com.oocourse.library3.LibraryCommand;
import com.oocourse.library3.LibraryReqCmd;
import com.oocourse.library3.LibraryRequest;
import com.oocourse.library3.LibraryQcsCmd;

import java.util.HashMap;

import static com.oocourse.library3.LibrarySystem.PRINTER;

public class Machine {
    private Library library;

    public Machine(Library library) {
        this.library = library;
    }

    public void queryBook1(LibraryCommand command) {
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

    public void qcs(LibraryQcsCmd command) {
        String sid = command.getStudentId();
        HashMap<String, Student> students = library.getStudents();
        Student student = students.getOrDefault(sid, new Student(sid));
        int score = student.getScore();
        PRINTER.info(command.getDate(), sid, score);
    }

}

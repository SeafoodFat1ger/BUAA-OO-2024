import com.oocourse.library3.LibraryBookId;
import com.oocourse.library3.LibraryCommand;
import com.oocourse.library3.LibraryReqCmd;
import com.oocourse.library3.LibraryRequest;
import com.oocourse.library3.annotation.Trigger;

import java.util.HashMap;

import static com.oocourse.library3.LibrarySystem.PRINTER;

public class DriftLibrarian {
    private BookPool driftBooks;
    private Library library;
    private HashMap<LibraryBookId, Integer> times;
    private HashMap<LibraryBookId, String> stuMap;

    public DriftLibrarian(Library library) {
        this.library = library;
        this.driftBooks = new BookPool();
        this.times = new HashMap<>();
        this.stuMap = new HashMap<>();
    }

    //TO/DO trigger yuan -> bdc
    @Trigger(from = "InitState", to = "BDC")
    public void addDriftBook(LibraryCommand command) {
        LibraryRequest request = ((LibraryReqCmd) command).getRequest();
        String sid = request.getStudentId();
        stuMap.put(request.getBookId(), sid);
        HashMap<String, Student> students = library.getStudents();
        Student student = students.getOrDefault(sid, new Student(sid));
        students.put(sid, student);
        student.updateScore(2);

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

    public void updateDonScore(LibraryBookId bid, int times) {
        String sid = stuMap.get(bid);
        HashMap<String, Student> students = library.getStudents();
        Student student = students.get(sid);
        student.updateScore(2 * times);
    }

    public BookPool getDriftBooks() {
        return driftBooks;
    }
}

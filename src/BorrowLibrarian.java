import com.oocourse.library3.LibraryBookId;
import com.oocourse.library3.LibraryCommand;
import com.oocourse.library3.LibraryReqCmd;
import com.oocourse.library3.LibraryRequest;
import com.oocourse.library3.annotation.Trigger;

import static com.oocourse.library3.LibrarySystem.PRINTER;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

public class BorrowLibrarian {
    private Library library;
    private BookPool borrowPool;
    private BookPool borrowDrift;

    public BorrowLibrarian(Library library) {
        this.library = library;
        this.borrowPool = new BookPool();
        this.borrowDrift = new BookPool();
    }

    //TO/DO trigger bs -> stu
    @Trigger(from = "BS", to = "STU")
    public void studentAddBook(Student student, LocalDate date, LibraryRequest request) {
        student.addBook(date, request.getBookId());
    }

    //TO/DO trigger bdc -> stu
    @Trigger(from = "BDC", to = "STU")
    public void studentAddDriftBook(Student student, LocalDate date, LibraryRequest request) {
        student.addBook(date, request.getBookId());
    }

    //TO/DO trigger bs -> bo
    @Trigger(from = "BS", to = "BO")
    public void boAddBook(LibraryRequest request) {
        borrowPool.addBook(request.getBookId());
    }

    //TO/DO trigger bdc -> bo
    @Trigger(from = "BDC", to = "BO")
    public void boAddDriftBook(LibraryRequest request) {
        borrowDrift.addBook(request.getBookId());
    }

    public void borrowBookB(LibraryCommand command) {
        LocalDate date = command.getDate();
        LibraryRequest request = ((LibraryReqCmd) command).getRequest();
        String sid = request.getStudentId();
        HashMap<String, Student> students = library.getStudents();
        Student student = students.getOrDefault(sid, new Student(sid));
        students.put(sid, student);

        if (student.hasTypeB() || student.getScore() < 0) {
            boAddBook(request);
            PRINTER.reject(command);
        } else {
            studentAddBook(student, date, request);
            PRINTER.accept(command);
        }
    }

    public void borrowBookBU(LibraryCommand command) {
        LocalDate date = command.getDate();
        LibraryRequest request = ((LibraryReqCmd) command).getRequest();
        String sid = request.getStudentId();
        HashMap<String, Student> students = library.getStudents();
        Student student = students.getOrDefault(sid, new Student(sid));
        students.put(sid, student);

        if (student.hasTypeBU() || student.getScore() < 0) {
            boAddDriftBook(request);
            PRINTER.reject(command);
        } else {
            studentAddDriftBook(student, date, request);
            PRINTER.accept(command);
        }
    }

    public void borrowBookC(LibraryCommand command) {
        LocalDate date = command.getDate();
        LibraryRequest request = ((LibraryReqCmd) command).getRequest();
        String sid = request.getStudentId();
        HashMap<String, Student> students = library.getStudents();
        Student student = students.getOrDefault(sid, new Student(sid));
        students.put(sid, student);

        if (student.hasBookC(request.getBookId()) || student.getScore() < 0) {
            boAddBook(request);
            PRINTER.reject(command);
        } else {
            studentAddDriftBook(student, date, request);
            PRINTER.accept(command);
        }
    }

    public void borrowBookCU(LibraryCommand command) {
        LocalDate date = command.getDate();
        LibraryRequest request = ((LibraryReqCmd) command).getRequest();
        String sid = request.getStudentId();
        HashMap<String, Student> students = library.getStudents();
        Student student = students.getOrDefault(sid, new Student(sid));
        students.put(sid, student);

        if (student.hasBookC(request.getBookId()) || student.getScore() < 0) {
            boAddDriftBook(request);
            PRINTER.reject(command);
        } else {
            studentAddBook(student, date, request);
            PRINTER.accept(command);
        }
    }

    //TO/DO trigger stu->bo
    @Trigger(from = "STU", to = "BO")
    public void returnBook(LibraryCommand command) {
        LocalDate date = command.getDate();
        LibraryRequest request = ((LibraryReqCmd) command).getRequest();

        if (request.getBookId().isTypeBU()
                || request.getBookId().isTypeCU()) {
            library.getDriftLibrarian().addTimes(request.getBookId());
            int times = library.getDriftLibrarian().getTimes(request.getBookId());
            if (times == 2) {
                borrowPool.addBook(request.getBookId());
            } else {
                borrowDrift.addBook(request.getBookId());
            }
        } else {
            borrowPool.addBook(request.getBookId());
        }

        Student student = library.getStudents().get(request.getStudentId());
        if (student.isOverDue(date, request.getBookId())) {
            PRINTER.accept(command, "overdue");
        } else {
            PRINTER.accept(command, "not overdue");
            student.updateScore(1);
        }
        student.stuReturnBook(request.getBookId());
    }

    public void renewBook(LibraryCommand command) {
        LocalDate date = command.getDate();
        LibraryRequest request = ((LibraryReqCmd) command).getRequest();
        LibraryBookId bid = request.getBookId();
        Student student = library.getStudents().get(request.getStudentId());

        if (student.getScore() < 0) {
            PRINTER.reject(command);
            return;
        }

        if (bid.isTypeAU() || bid.isTypeBU() || bid.isTypeCU()) {
            PRINTER.reject(command);
            return;
        }
        if (!canRenew(date, request)) {
            PRINTER.reject(command);
            return;
        }
        if (!library.getBookPool().hasBook(bid)) {
            if (library.getArrangeLibrarian().query4CanRenew(bid)
                    || library.getOrderLibrarian().query4CanRenew(bid)) {
                PRINTER.reject(command);
                return;
            }
        }
        student.addTime(bid);
        PRINTER.accept(command);
    }

    public boolean canRenew(LocalDate date, LibraryRequest request) {
        Student student = library.getStudents().get(request.getStudentId());
        LibraryBookId bid = request.getBookId();
        LocalDate store = student.getTime(bid);
        long tmp = 0;
        if (bid.isTypeB()) {
            tmp = 30;
        } else if (bid.isTypeC()) {
            tmp = 60;
        } else if (bid.isTypeBU()) {
            tmp = 7;
        } else if (bid.isTypeCU()) {
            tmp = 14;
        }
        // 26 < 1+30-5
        // today > store + tmp   today < store + tmp -5
        long cha = store.until(date, ChronoUnit.DAYS);
        //System.out.println(store.plusDays(tmp));
        //System.out.println(date);
        //System.out.println(cha + " " + tmp);
        if (cha > tmp || cha <= tmp - 5) {
            return false;
        } else {
            return true;
        }

    }

    public BookPool getBorrowPool() {
        return borrowPool;
    }

    public void clearBorrowPool() {
        borrowPool = new BookPool();
    }

    public BookPool getBorrowDrift() {
        return borrowDrift;
    }

    public void clearBorrowDrift() {
        borrowDrift = new BookPool();
    }
}

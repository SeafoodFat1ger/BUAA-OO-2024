import com.oocourse.library1.LibraryRequest;

import static com.oocourse.library1.LibrarySystem.PRINTER;

import java.time.LocalDate;
import java.util.HashMap;

public class BorrowLibrarian {
    private Library library;
    private BookPool borrowPool;

    public BorrowLibrarian(Library library) {
        this.library = library;
        this.borrowPool = new BookPool();
    }

    public void borrowBookB(LocalDate date, LibraryRequest request) {
        String sid = request.getStudentId();
        HashMap<String, Student> students = library.getStudents();
        Student student = students.getOrDefault(sid, new Student(sid));
        students.put(sid, student);

        if (student.hasTypeB()) {
            borrowPool.addBook(request.getBookId());
            PRINTER.reject(date, request);
        } else {
            student.addBook(request.getBookId());
            PRINTER.accept(date, request);
        }
    }

    public void borrowBookC(LocalDate date, LibraryRequest request) {
        String sid = request.getStudentId();
        HashMap<String, Student> students = library.getStudents();
        Student student = students.getOrDefault(sid, new Student(sid));
        students.put(sid, student);

        if (student.hasBookC(request.getBookId())) {
            borrowPool.addBook(request.getBookId());
            PRINTER.reject(date, request);
        } else {
            student.addBook(request.getBookId());
            PRINTER.accept(date, request);
        }
    }

    public void returnBook(LocalDate date, LibraryRequest request) {
        Student student = library.getStudents().get(request.getStudentId());
        student.returnBook(request.getBookId());
        borrowPool.addBook(request.getBookId());
        PRINTER.accept(date, request);
    }

    public BookPool getBorrowPool() {
        return borrowPool;
    }

    public void clearBorrowPool() {
        borrowPool = new BookPool();
    }
}

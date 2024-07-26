import com.oocourse.library1.LibraryBookId;

public class Student {
    private String studentId;
    private BookPool borrowBooks;

    public Student(String id) {
        this.studentId = id;
        this.borrowBooks = new BookPool();
    }

    public boolean hasTypeB() {
        return borrowBooks.hasTypeB();
    }

    public boolean hasBookC(LibraryBookId bid) {
        return borrowBooks.hasBook(bid);
    }

    public void addBook(LibraryBookId bid) {
        borrowBooks.addBook(bid);
    }

    public void returnBook(LibraryBookId bid) {
        borrowBooks.borrowBook(bid);
    }
}

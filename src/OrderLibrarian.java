import com.oocourse.library1.LibraryBookId;
import com.oocourse.library1.LibraryRequest;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import static com.oocourse.library1.LibrarySystem.PRINTER;

public class OrderLibrarian {
    private Library library;
    private TreeMap<LocalDate, HashMap<String, HashMap<LibraryBookId, Integer>>> orderBooks;

    public OrderLibrarian(Library library) {
        this.library = library;
        this.orderBooks = new TreeMap<>();
    }

    /*
    预约的书不是 A 类书（规定 A 类书无法预约）。
    若已经持有一本 B 类书，则无法再预约其他 B 类书。
    若已经持有某书号的 C 类书，则不能预约该书号的 C 类书。
    若当前没有持有 B 类书，可以预约任意数量的 B 类书（ B 类书只要求用户不可以持有两本及以上，但预约不算在其中）。
    若当前没有持有某书号的 C 类书，就可以预约任意数量该书号的 C 类书。
     */
    public void orderBook(LocalDate date, LibraryRequest request) {
        if (request.getBookId().isTypeA()) {
            PRINTER.reject(date, request);
        } else if (request.getBookId().isTypeB()) {
            orderBookB(date, request);
        } else {
            orderBookC(date, request);
        }
    }

    public void orderBookB(LocalDate date, LibraryRequest request) {
        String sid = request.getStudentId();
        HashMap<String, Student> students = library.getStudents();
        Student student = students.getOrDefault(sid, new Student(sid));
        students.put(sid, student);

        if (student.hasTypeB()) {
            PRINTER.reject(date, request);
        } else {
            library.getArrangeLibrarian().addNeedArrangeOrder(request);
            PRINTER.accept(date, request);
        }
    }

    public void orderBookC(LocalDate date, LibraryRequest request) {
        String sid = request.getStudentId();
        HashMap<String, Student> students = library.getStudents();
        Student student = students.getOrDefault(sid, new Student(sid));
        students.put(sid, student);

        if (student.hasBookC(request.getBookId())) {
            PRINTER.reject(date, request);
        } else {
            library.getArrangeLibrarian().addNeedArrangeOrder(request);
            PRINTER.accept(date, request);
        }
    }

    public void addOrderBook(LocalDate date, LibraryRequest request) {
        String sid = request.getStudentId();
        if (orderBooks.containsKey(date)) {
            HashMap<String, HashMap<LibraryBookId, Integer>> dateMap = orderBooks.get(date);
            if (dateMap.containsKey(sid)) {
                HashMap<LibraryBookId, Integer> stuMap = dateMap.get(sid);
                if (stuMap.containsKey(request.getBookId())) {
                    int num = stuMap.get(request.getBookId());
                    stuMap.put(request.getBookId(), num + 1);
                } else {
                    stuMap.put(request.getBookId(), 1);
                }
            } else {
                HashMap<LibraryBookId, Integer> stuMap = new HashMap<>();
                stuMap.put(request.getBookId(), 1);
                dateMap.put(sid, stuMap);
            }
        } else {
            HashMap<LibraryBookId, Integer> stuMap = new HashMap<>();
            stuMap.put(request.getBookId(), 1);
            HashMap<String, HashMap<LibraryBookId, Integer>> dateMap = new HashMap<>();
            dateMap.put(sid, stuMap);
            orderBooks.put(date, dateMap);
        }
    }

    public void pickBook(LocalDate date, LibraryRequest request) {
        String sid = request.getStudentId();
        if (request.getBookId().isTypeA()) {
            PRINTER.reject(date, request);
            return;
        } else if (request.getBookId().isTypeB()) {
            if (!canPickBookB(request)) {
                PRINTER.reject(date, request);
                return;
            }
        } else {
            if (!canPickBookC(request)) {
                PRINTER.reject(date, request);
                return;
            }
        }
        Student student = library.getStudents().get(sid);
        if (haveBookToPick(date, request)) {
            student.addBook(request.getBookId());
            PRINTER.accept(date, request);
        } else {
            PRINTER.reject(date, request);
        }
    }

    public boolean canPickBookB(LibraryRequest request) {
        String sid = request.getStudentId();
        Student student = library.getStudents().get(sid);
        if (student.hasTypeB()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean canPickBookC(LibraryRequest request) {
        String sid = request.getStudentId();
        Student student = library.getStudents().get(sid);
        if (student.hasBookC(request.getBookId())) {
            return false;
        } else {
            return true;
        }
    }

    public boolean haveBookToPick(LocalDate date, LibraryRequest request) {
        String sid = request.getStudentId();
        for (LocalDate date1 : orderBooks.keySet()) {
            if (orderBooks.get(date1).containsKey(sid)) {
                HashMap<LibraryBookId, Integer> books = orderBooks.get(date1).get(sid);
                if (books.containsKey(request.getBookId())) {
                    int num = books.get(request.getBookId());
                    if (num > 0) {
                        books.put(request.getBookId(), num - 1);
                        return true;
                    }
                }

            }
        }
        return false;
    }

    public void clearToShelf(LocalDate date) {
        Iterator<LocalDate> iterator = orderBooks.keySet().iterator();
        while (iterator.hasNext()) {
            LocalDate date1 = iterator.next();
            long cha = date1.until(date, ChronoUnit.DAYS);
            if (cha >= 5) {
                for (HashMap<LibraryBookId, Integer> map : orderBooks.get(date1).values()) {
                    library.getArrangeLibrarian().moveOrderToShelf(map);
                }
                iterator.remove();
            }
        }

    }

    public void clearToShelfOpen(LocalDate date) {
        Iterator<LocalDate> iterator = orderBooks.keySet().iterator();
        while (iterator.hasNext()) {
            LocalDate date1 = iterator.next();
            long cha = date1.until(date, ChronoUnit.DAYS);
            if (cha >= 6) {
                for (HashMap<LibraryBookId, Integer> map : orderBooks.get(date1).values()) {
                    library.getArrangeLibrarian().moveOrderToShelf(map);
                }
                iterator.remove();
            }
        }

    }

}

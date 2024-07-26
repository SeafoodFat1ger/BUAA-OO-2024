import com.oocourse.library1.LibraryBookId;
import com.oocourse.library1.LibraryCommand;
import com.oocourse.library1.LibraryRequest;

import static com.oocourse.library1.LibrarySystem.SCANNER;

import java.time.LocalDate;
import java.util.Map;

public class Main {
    private static Library library = new Library();
    private static LocalDate date;

    public static void main(String[] args) {
        Map<LibraryBookId, Integer> books = SCANNER.getInventory();
        library.initBooks(books);
        while (true) {
            LibraryCommand<?> command = SCANNER.nextCommand();
            if (command == null) {
                break;
            }
            date = command.getDate();
            if (command.getCmd().equals("OPEN")) {
                //1.清理过期预约（中间可能歇业了？）
                library.getOrderLibrarian().clearToShelfOpen(date);
                library.getArrangeLibrarian().printInfo(date);

            } else if (command.getCmd().equals("CLOSE")) {
                //1.清理过期预约
                library.getOrderLibrarian().clearToShelf(date);
                //2.借还处所有的书还回去
                library.getArrangeLibrarian().moveBorrowToShelf();
                //3.新预约运到预约处
                library.getArrangeLibrarian().arrangeOrder(date);
                library.getArrangeLibrarian().printInfo(date);
            } else {
                LibraryRequest request = (LibraryRequest) command.getCmd();
                // 对 request 干点什么
                manage(request);
            }
        }
    }

    public static void manage(LibraryRequest request) {
        String type = request.getType().toString();
        if (type.equals("queried")) {
            library.getMachine().queryBook(date, request);
        } else if (type.equals("borrowed")) {
            library.borrowBook(date, request);
        } else if (type.equals("ordered")) {
            library.getOrderLibrarian().orderBook(date, request);
        } else if (type.equals("returned")) {
            library.returnBook(date, request);
        } else if (type.equals("picked")) {
            library.getOrderLibrarian().pickBook(date, request);
        }
    }

}
import com.oocourse.library2.LibraryBookId;
import com.oocourse.library2.LibraryCommand;
import com.oocourse.library2.LibraryReqCmd;
import com.oocourse.library2.LibraryRequest;

import static com.oocourse.library2.LibrarySystem.SCANNER;

import java.time.LocalDate;
import java.util.Map;

public class Main {
    private static Library library = new Library();
    private static LocalDate date;

    public static void main(String[] args) {
        Map<LibraryBookId, Integer> books = SCANNER.getInventory();
        library.initBooks(books);
        while (true) {
            LibraryCommand command = SCANNER.nextCommand();
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
                library.getArrangeLibrarian().moveBorrowToDrift();
                //3.新预约运到预约处
                library.getArrangeLibrarian().arrangeOrder(date);
                library.getArrangeLibrarian().printInfo(date);
            } else {
                manage(command);
            }
        }
    }

    public static void manage(LibraryCommand command) {
        LibraryRequest request = ((LibraryReqCmd) command).getRequest();
        String type = request.getType().toString();
        if (type.equals("queried")) {
            library.getMachine().queryBook(command);
        } else if (type.equals("borrowed")) {
            library.borrowBook(command);
        } else if (type.equals("ordered")) {
            library.getOrderLibrarian().orderBook(command);
        } else if (type.equals("returned")) {
            library.getBorrowLibrarian().returnBook(command);
        } else if (type.equals("picked")) {
            library.getOrderLibrarian().pickBook(command);
        } else if (type.equals("donated")) {
            library.getDriftLibrarian().addDriftBook(command);
        } else if (type.equals("renewed")) {
            library.getBorrowLibrarian().renewBook(command);
        }
    }

}
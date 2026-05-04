import models.Customer;
import models.Reservation;
import models.Room;
import models.RoomType;
import records.CreateReservationRequest;
import records.CreateRoomRequest;
import models.ReservationStatus;
import services.HotelService;
import helperFunctions.Helper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;




public class Main {

    private static final String DATE_FORMAT      = "yyyy-MM-dd";
    private static final int    MENU_MIN_OPTION  = 0;
    private static final int    MENU_MAX_OPTION  = 7;
    private static final String MENU_PROMPT      = "Choose an option (" + MENU_MIN_OPTION
            + "-" + MENU_MAX_OPTION + "): ";

    void main() {

        Scanner     scanner = new Scanner(System.in);
        Helper      helper  = new Helper();
        HotelService hotel  = new HotelService();

        printWelcome();

        int menuOption = -1;
        while (menuOption != MENU_MIN_OPTION) {
            printMenu();
            menuOption = readIntSafe(scanner, MENU_PROMPT);

            // Έλεγχος εύρους — αποτρέπει "option 99"
            if (menuOption < MENU_MIN_OPTION || menuOption > MENU_MAX_OPTION) {
                System.out.println("Invalid option. Please enter a number between "
                        + MENU_MIN_OPTION + " and " + MENU_MAX_OPTION + ".");
                continue;
            }

            switch (menuOption) {
                case 1 -> handleCreateRoom(scanner, hotel);
                case 2 -> handleDisplayRooms(hotel);
                case 3 -> handleCreateReservation(scanner, helper, hotel);
                case 4 -> handleCheckAvailability(scanner, helper, hotel);
                case 5 -> handleCancelReservation(scanner, hotel);
                case 6 -> handleSearchByCustomer(scanner, hotel);
                case 7 -> handleSearchByDateRange(scanner, helper, hotel);
                case 0 -> System.out.println("Goodbye!");
                // Δεν χρειάζεται default: ήδη ελέγχθηκε το εύρος παραπάνω
            }
        }

        // Κλείνουμε πάντα τον Scanner — αποτρέπει resource leak
        scanner.close();
    }


    private static void handleCreateRoom(Scanner scanner, HotelService hotel) {
        System.out.print("Room number: ");
        String roomNumber = scanner.nextLine().trim();

        System.out.print("Room type (SINGLE / DOUBLE / FAMILY / SUITE): ");
        RoomType type = readRoomTypeSafe(scanner);
        if (type == null) return; // Validation απέτυχε, επιστρέφουμε στο menu

        System.out.print("Daily price: ");
        double dailyPrice = readDoubleSafe(scanner, "Daily price");
        if (dailyPrice < 0) return;

        System.out.print("Available? (yes / no): ");
        // ΑΛΛΑΓΗ: trim().toLowerCase() — αποδεχόμαστε "Yes", "YES", " yes "
        boolean available = scanner.nextLine().trim().equalsIgnoreCase("yes");

        hotel.createRoom(new CreateRoomRequest(roomNumber, type, dailyPrice, available));
    }

    private static void handleDisplayRooms(HotelService hotel) {
        List<Room> availableRooms = hotel.getAvailableRooms();

        if (availableRooms.isEmpty()) {
            System.out.println("No available rooms at the moment.");
            return;
        }

        System.out.println("--- Available Rooms ---");
        for (Room room : availableRooms) {
            System.out.printf("  #%-5s | %-8s | €%.2f/night%n",
                    room.getRoomNumber(),
                    room.getType(),
                    room.getDailyPrice());
        }
        System.out.println("---");
    }

    private static void handleCreateReservation(Scanner scanner, Helper helper, HotelService hotel) {
        System.out.print("Reservation number: ");
        int reservationNum = readIntSafe(scanner, "Reservation number");
        if (reservationNum < 0) return;

        System.out.print("Customer full name: ");
        String fullname = scanner.nextLine().trim();

        System.out.print("Customer mobile phone: ");
        String mobilePhone = scanner.nextLine().trim();

        System.out.print("Customer email: ");
        String email = scanner.nextLine().trim();

        Customer customer = new Customer(fullname, mobilePhone, email);

        System.out.print("Room number: ");
        String roomNum = scanner.nextLine().trim();
        Room room = helper.findRoombyRoomNumber(roomNum, hotel.getAvailableRooms());

        // ΑΛΛΑΓΗ: null check — αποτρέπει NullPointerException
        if (room == null) {
            System.out.println("Room not found: " + roomNum);
            return;
        }

        LocalDate checkIn = readDateSafe(scanner, helper, "Check-in date");
        if (checkIn == null) return;

        LocalDate checkOut = readDateSafe(scanner, helper, "Check-out date");
        if (checkOut == null) return;

        hotel.createReservation(new CreateReservationRequest(
                reservationNum, customer, room, checkIn, checkOut, ReservationStatus.ACTIVE
        ));
    }

    private static void handleCheckAvailability(Scanner scanner, Helper helper, HotelService hotel) {
        System.out.print("Room number: ");
        String roomNumber = scanner.nextLine().trim();

        LocalDate from = readDateSafe(scanner, helper, "From date");
        if (from == null) return;

        LocalDate to = readDateSafe(scanner, helper, "To date");
        if (to == null) return;

        boolean isAvailable = hotel.getRoomAvailability(roomNumber, from, to);

        // ΑΛΛΑΓΗ: επιστρέφουμε πληροφοριακό μήνυμα με το δωμάτιο
        if (isAvailable) {
            System.out.println("Room " + roomNumber + " is AVAILABLE from " + from + " to " + to + ".");
        } else {
            System.out.println("Room " + roomNumber + " is NOT available for that period.");
        }
    }

    private static void handleCancelReservation(Scanner scanner, HotelService hotel) {
        System.out.print("Reservation number to cancel: ");
        int reservationNumber = readIntSafe(scanner, "Reservation number");
        if (reservationNumber < 0) return;

        // ΑΛΛΑΓΗ: Optional — ο compiler μας αναγκάζει να χειριστούμε "not found"
        Optional<Reservation> result = hotel.cancelReservation(reservationNumber);
        result.ifPresentOrElse(
                r -> System.out.println("Reservation " + r.getReservationNumber() + " cancelled successfully."),
                ()  -> System.out.println("Reservation " + reservationNumber + " not found.")
        );
    }

    private static void handleSearchByCustomer(Scanner scanner, HotelService hotel) {
        System.out.print("Customer mobile phone: ");
        String mobilePhone = scanner.nextLine().trim();

        List<Reservation> results = hotel.searchByCustomerPhone(mobilePhone);
        printReservationList(results, "No reservations found for phone: " + mobilePhone);
    }

    private static void handleSearchByDateRange(Scanner scanner, Helper helper, HotelService hotel) {
        LocalDate from = readDateSafe(scanner, helper, "From date");
        if (from == null) return;

        LocalDate to = readDateSafe(scanner, helper, "To date");
        if (to == null) return;

        List<Reservation> results = hotel.searchByDateRange(from, to);
        printReservationList(results, "No reservations found for that date range.");
    }



    private static int readIntSafe(Scanner scanner, String fieldName) {
        if (!scanner.hasNextInt()) {
            System.out.println("Invalid input for " + fieldName + ". Expected a number.");
            scanner.nextLine(); // καθάρισε το buffer
            return -1;
        }
        int value = scanner.nextInt();
        scanner.nextLine(); // ΚΡΙΣΙΜΟ: καταναλώνει το \n που αφήνει το nextInt()
        return value;
    }

    private static double readDoubleSafe(Scanner scanner, String fieldName) {
        if (!scanner.hasNextDouble()) {
            System.out.println("Invalid input for " + fieldName + ". Expected a number.");
            scanner.nextLine();
            return -1;
        }
        double value = scanner.nextDouble();
        scanner.nextLine();
        return value;
    }

    private static LocalDate readDateSafe(Scanner scanner, Helper helper, String label) {
        System.out.print(label + " (" + DATE_FORMAT + "): ");
        String input = scanner.nextLine().trim();
        LocalDate date = helper.validateDate(input);
        if (date == null) {
            System.out.println("Invalid date format. Expected: " + DATE_FORMAT);
        }
        return date;
    }


    private static RoomType readRoomTypeSafe(Scanner scanner) {
        String input = scanner.nextLine().trim().toUpperCase();
        try {
            return RoomType.valueOf(input);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid room type: '" + input + "'. Use SINGLE, DOUBLE, SUITE, or DELUXE.");
            return null;
        }
    }

    private static void printReservationList(List<Reservation> reservations, String emptyMessage) {
        if (reservations.isEmpty()) {
            System.out.println(emptyMessage);
            return;
        }
        System.out.println("--- Reservations ---");
        for (Reservation r : reservations) {
            System.out.printf("  #%d | Room: %-5s | Customer: %-20s | %s → %s | Status: %s%n",
                    r.getReservationNumber(),
                    r.getRoom().getRoomNumber(),
                    r.getCustomer().getMobilePhone(),
                    r.getCheckIn(),
                    r.getCheckOut(),
                    r.getStatus());
        }
        System.out.println("---");
    }

    private static void printWelcome() {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║      OTS HOTEL MANAGEMENT DEMO       ║");
        System.out.println("╚══════════════════════════════════════╝");
    }

    private static void printMenu() {
        System.out.println("====== Main Menu ======");
        System.out.println("1. Create new Room");
        System.out.println("2. Display available Rooms");
        System.out.println("3. Create new Reservation");
        System.out.println("4. Check Room availability for date range");
        System.out.println("5. Cancel a Reservation");
        System.out.println("6. Search Reservations by Customer phone");
        System.out.println("7. Search Reservations by Date range");
        System.out.println("0. Exit");
    }
}

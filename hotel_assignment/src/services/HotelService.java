package services;

import records.CreateReservationRequest;
import records.CreateRoomRequest;
import models.Reservation;
import models.ReservationStatus;
import models.Room;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * HotelService — η κεντρική υπηρεσία επιχειρηματικής λογικής.
 * χρησιμοποιηθηκε η κλαση strams κλαση για να αποφευγχθουν καποιες loop.
 */
public class HotelService {

    private static final int MAX_ROOM_CAPACITY = 100;
    private static final int MAX_RESERVATIONS  = 500;

    private final List<Room>        rooms        = new ArrayList<>();
    private final List<Reservation> reservations = new ArrayList<>();


    public void createRoom(CreateRoomRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("CreateRoomRequest cannot be null");
        }

        if (rooms.size() >= MAX_ROOM_CAPACITY) {
            System.out.println("Cannot add room: hotel is at full capacity (" + MAX_ROOM_CAPACITY + ").");
            return;
        }

        boolean alreadyExists = rooms.stream()
                .anyMatch(r -> r.getRoomNumber().equals(request.roomNumber()));

        if (alreadyExists) {
            System.out.println("Room " + request.roomNumber() + " already exists.");
            return;
        }

        Room room = new Room(
                request.roomNumber(),
                request.type(),
                request.dailyPrice(),
                request.availability()
        );
        rooms.add(room);
        System.out.println("Room created: " + room.getRoomNumber());
    }

    /**
     * Επιστρέφει τα διαθέσιμα δωμάτια ως unmodifiable list.
     *
     * ΑΛΛΑΓΗ: Επιστρέφουμε Collections.unmodifiableList().
     * ΓΙΑΤΙ; Ο καλών παίρνει μια "read-only" εικόνα.
     * Αν έκανε availableRooms.add(fakeRoom), δεν θα επηρέαζε
     * την εσωτερική μας λίστα. Αυτό λέγεται defensive copy.
     */
    public List<Room> getAvailableRooms() {
        List<Room> availableRooms = new ArrayList<>();
        for (Room room : rooms) {
            if (room.isAvailable()) {
                availableRooms.add(room);
            }
        }
        return Collections.unmodifiableList(availableRooms);
    }

    /**
     * Δημιουργεί κράτηση αφού ελέγξει διαθεσιμότητα.
     *
     * ΑΛΛΑΓΗ: Επιστρέφει boolean αντί void,
     * ώστε ο καλών να ξέρει αν πέτυχε.
     */
    public boolean createReservation(CreateReservationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("CreateReservationRequest cannot be null");
        }

        if (reservations.size() >= MAX_RESERVATIONS) {
            System.out.println("Cannot create reservation: system limit reached.");
            return false;
        }

        // Έλεγχος ημερομηνιών πριν ελέγξουμε διαθεσιμότητα
        if (!request.checkIn().isBefore(request.checkOut())) {
            System.out.println("Check-in must be before check-out.");
            return false;
        }

        boolean roomIsAvailable = getRoomAvailability(
                request.room().getRoomNumber(),
                request.checkIn(),
                request.checkOut()
        );

        if (!roomIsAvailable) {
            System.out.println("Room " + request.room().getRoomNumber()
                    + " is not available for the selected dates.");
            return false;
        }

        Reservation reservation = new Reservation(
                request.reservationNumber(),
                request.customer(),
                request.room(),
                request.checkIn(),
                request.checkOut(),
                ReservationStatus.ACTIVE
        );
        reservations.add(reservation);
        System.out.println("Reservation " + request.reservationNumber() + " created.");
        return true;
    }


    public boolean getRoomAvailability(String roomNumber, LocalDate checkIn, LocalDate checkOut) {
        for (Reservation reservation : reservations) {
            boolean sameRoom = reservation.getRoom().getRoomNumber().equals(roomNumber);
            boolean isActive  = reservation.getStatus() == ReservationStatus.ACTIVE;

            if (sameRoom && isActive) {
                // Επικάλυψη: η υπάρχουσα κράτηση τελειώνει ΜΕΤΑ το checkIn μας
                //            ΚΑΙ αρχίζει ΠΡΙΝ το checkOut μας
                boolean overlaps = reservation.getCheckOut().isAfter(checkIn)
                        && reservation.getCheckIn().isBefore(checkOut);

                if (overlaps) {
                    return false; // Μη διαθέσιμο
                }
            }
        }
        return true; // Ελεύθερο
    }

    /**
     * Ακυρώνει κράτηση βάσει αριθμού.
     * ΑΛΛΑΓΗ : Επιστρέφει Optional<Reservation> αντί για Reservation.
     *
     * ΓΙΑΤΙ Optional;
     * Η αρχική υλοποίηση επέστρεφε null αν δεν βρισκόταν η κράτηση,
     * και το main έκανε reservation.getReservationNumber() πάνω στο null
     * → NullPointerException. Το Optional εκφράζει ρητά "μπορεί να μην υπάρχει"
     * και αναγκάζει τον καλούντα να χειριστεί και τις δύο περιπτώσεις.
     */
    public Optional<Reservation> cancelReservation(int reservationNumber) {
        for (Reservation reservation : reservations) {
            if (reservation.getReservationNumber() == reservationNumber) {

                // Δεν ακυρώνουμε κράτηση που ήδη ακυρώθηκε
                if (reservation.getStatus() == ReservationStatus.CANCELLED) {
                    System.out.println("Reservation " + reservationNumber + " is already cancelled.");
                    return Optional.of(reservation); // Βρέθηκε αλλά ήδη cancelled
                }

                reservation.setStatus(ReservationStatus.CANCELLED);
                return Optional.of(reservation);
            }
        }
        return Optional.empty(); // Δεν βρέθηκε καθόλου
    }


    public List<Reservation> searchByCustomerPhone(String customerMobilePhone) {
        if (customerMobilePhone == null || customerMobilePhone.isBlank()) {
            return Collections.emptyList();
        }

        List<Reservation> result = new ArrayList<>();
        for (Reservation reservation : reservations) {
            if (reservation.getCustomer().getMobilePhone().equals(customerMobilePhone)) {
                result.add(reservation);
            }
        }
        return Collections.unmodifiableList(result);
    }


    public List<Reservation> searchByDateRange(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            return Collections.emptyList();
        }
        if (from.isAfter(to)){
            return Collections.emptyList();
        }
        List<Reservation> result = new ArrayList<>();
        for (Reservation reservation : reservations) {
            boolean overlaps = reservation.getCheckOut().isAfter(from)
                    && reservation.getCheckIn().isBefore(to);
            if (overlaps) {
                result.add(reservation);
            }
        }
        return Collections.unmodifiableList(result);
    }

}

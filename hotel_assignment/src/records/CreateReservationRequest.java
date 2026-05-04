package records;
import models.Customer;
import models.ReservationStatus;
import models.Room;
import java.time.LocalDate;

public record CreateReservationRequest(
        int reservationNumber,
        Customer customer,
        Room room,
        LocalDate checkIn,
        LocalDate checkOut,
        ReservationStatus status
)
{}

package helperFunctions;

import models.Reservation;
import models.ReservationStatus;
import models.Room;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class Helper {
    public Reservation findReservationbyNumber(int reservationNumber, List<Reservation> reservations){
        for(Reservation reservation: reservations){
            if(reservation.getReservationNumber() == reservationNumber) {
                return reservation;
            }
        }
        return new Reservation(-1,null,null,null,null, ReservationStatus.CANCELLED);
    }
    public Room findRoombyRoomNumber(String roomNumber, List<Room> rooms){
        for (Room room: rooms){
            if(room.getRoomNumber().equals(roomNumber)){
                return room;
            }
        }
        return new Room("-1",null,0.0,false);
    }
    public LocalDate validateDate(String inputDate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            LocalDate date = LocalDate.parse(inputDate, formatter);
            return date;
        } catch (DateTimeParseException e) {
            return LocalDate.parse("1000-01-01", formatter);
        }
    }
}

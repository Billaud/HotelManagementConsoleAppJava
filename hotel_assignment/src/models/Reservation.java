package models;

import java.time.LocalDate;
import java.util.UUID;

public class Reservation {
    private int reservationId;
    private final int reservationNumber;
    private Customer customer;
    private Room room;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private ReservationStatus status;


    public Reservation(int reservationNumber,Customer customer,Room room, LocalDate checkIn,LocalDate checkOut,ReservationStatus status){
        this.reservationNumber = reservationNumber;
        this.customer = customer;
        this.room = room;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.status = status;
    }
    public int getReservationId(){
        return reservationId;
    }

    public int getReservationNumber() {
        return reservationNumber;
    }
    public Customer getCustomer(){
        return customer;
    }

    public Room getRoom(){
        return room;
    }

    public LocalDate getCheckIn(){
        return checkIn;
    }

    public LocalDate getCheckOut(){
        return checkOut;
    }

    public ReservationStatus getStatus(){
        return status;
    }

    public void setCustomer(Customer customer){
        this.customer = customer;
    }

    public void setRoom(Room room){
        this.room = room;
    }

    public void setCheckIn(LocalDate checkIn){
        this.checkIn = checkIn;
    }

    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }

    public void setStatus(ReservationStatus status){
        this.status = status;
    }
}

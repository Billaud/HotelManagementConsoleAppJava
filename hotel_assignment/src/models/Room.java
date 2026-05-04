package models;

import java.util.UUID;

public class Room {
    private int roomId;
    private String roomNumber;
    private RoomType type;
    private double dailyPrice;
    private boolean available;

    public Room(String roomNumber, RoomType type, double dailyPrice, boolean availability){
        this.roomNumber = roomNumber;
        this.type = type;
        this.dailyPrice = dailyPrice;
        this.available = true;
    }
    public int getRoomId(){
        return roomId;
    }
    public String getRoomNumber(){
        return roomNumber;
    }
    public RoomType getType(){
        return type;
    }
    public double getDailyPrice(){
        return dailyPrice;
    }
    public boolean isAvailable(){
        return available;
    }
    public void setRoomNumber(String roomNumber){
          this.roomNumber = roomNumber;
    }
    public void setRoomType(RoomType type){
        this.type = type;
    }
    public void setDailyPrice(double dailyPrice){
        this.dailyPrice = dailyPrice;
    }
    public void setAvailable(boolean available){
        this.available = available;
    }
}

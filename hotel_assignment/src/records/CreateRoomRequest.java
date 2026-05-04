package records;
import models.RoomType;

public record CreateRoomRequest(
   String roomNumber,
   RoomType type,
   double dailyPrice,
   boolean availability
) {}

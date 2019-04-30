package sample.editValues;

/**
 * Reference for the design of MenuModelTable:
 * https://www.youtube.com/watch?v=LoiQVoNil9Q&ab_channel=RashidCoder
 */

public class RoomModelTable {
    String hallName, roomNum, occupied, firstName, lastName, foodDiet, liquidDiet, otherNotes;

    public RoomModelTable(String hallName, String roomNum, String occupied, String firstName, String lastName, String foodDiet, String liquidDiet, String otherNotes){
        this.hallName = hallName;
        this.roomNum = roomNum;
        this.occupied = occupied;
        this.firstName = firstName;
        this.lastName = lastName;
        this.foodDiet = foodDiet;
        this.liquidDiet = liquidDiet;
        this.otherNotes = otherNotes;
    }
    public String getHallName(){
        return hallName;
    }
    public String getRoomNum(){
        return roomNum;
    }
    public String getOccupied(){
        return occupied;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName(){
        return lastName;
    }
    public String getFoodDiet(){
        return foodDiet;
    }
    public String getLiquidDiet(){
        return liquidDiet;
    }
    public String getOtherNotes(){
        return otherNotes;
    }
}

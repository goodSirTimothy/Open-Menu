package sample.menuSpecials;

/**
 * Reference for the design of MenuModelTable:
 * https://www.youtube.com/watch?v=LoiQVoNil9Q&ab_channel=RashidCoder
 */

public class WeeklySpecialsModelTable {
    String itemID, dayID, itemName, itemDescription;

    public WeeklySpecialsModelTable(String itemID, String dayID, String itemName, String itemDescription){
        this.itemID = itemID;
        this.dayID = dayID;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
    }
    public String getItemID(){return itemID;}
    public String getDayID(){
        return dayID;
    }
    public String getItemName(){
        return itemName;
    }
    public String getItemDescription(){
        return itemDescription;
    }
}

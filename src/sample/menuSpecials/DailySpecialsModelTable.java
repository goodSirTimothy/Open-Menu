package sample.menuSpecials;

/**
 * Reference for the design of MenuModelTable:
 * https://www.youtube.com/watch?v=LoiQVoNil9Q&ab_channel=RashidCoder
 */

public class DailySpecialsModelTable {
    String itemID, itemType, itemName, itemDescription;

    public DailySpecialsModelTable(String itemID, String itemType, String itemName, String itemDescription){
        this.itemID = itemID;
        this.itemType = itemType;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
    }
    public String getItemID(){return itemID;}
    public String getItemType(){
        return itemType;
    }
    public String getItemName(){
        return itemName;
    }
    public String getItemDescription(){
        return itemDescription;
    }
}

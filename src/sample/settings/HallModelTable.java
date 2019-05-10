package sample.settings;

/**
 * Reference for the design of MenuModelTable:
 * https://www.youtube.com/watch?v=LoiQVoNil9Q&ab_channel=RashidCoder
 */

public class HallModelTable {
    String hallID, hallName;

    public HallModelTable(String hallID, String hallName){
        this.hallID = hallID;
        this.hallName = hallName;
    }
    public String getHallID(){
        return hallID;
    }
    public String getHallName(){
        return hallName;
    }
}

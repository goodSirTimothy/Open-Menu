package sample.editValues;

/**
 * Reference for the design of MenuModelTable:
 * https://www.youtube.com/watch?v=LoiQVoNil9Q&ab_channel=RashidCoder
 */

public class MenuModelTable {
    // mealTime is not needed. BUT because of this redo, it solved my problem with Dessert not appearing in its column.
    String mealTime, week, day, primaryDish, side1, side2, side3, dessert;

    public MenuModelTable (String mealTime, String week, String day, String primaryDish, String side1, String side2, String side3, String dessert){
        this.mealTime = mealTime;
        this.week = week;
        this.day = day;
        this.primaryDish = primaryDish;
        this.side1 = side1;
        this.side2 = side2;
        this.side3 = side3;
        this.dessert = dessert;
    }
    public String getMealTime(){
        return mealTime;
    }
    public String getWeek(){
        return week;
    }
    public String getDay(){
        return day;
    }
    public String getPrimaryDish() {
        return primaryDish;
    }
    public String getSide1(){
        return side1;
    }
    public String getSide2(){
        return side2;
    }
    public String getSide3(){
        return side3;
    }
    public String getDessert(){
        return dessert;
    }
}

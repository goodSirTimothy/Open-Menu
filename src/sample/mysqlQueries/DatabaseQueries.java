package sample.mysqlQueries;

import javafx.scene.control.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.Scanner;

/**
 * Rework of the database connections (if I read this correctly:
 *      https://stackoverflow.com/questions/638421/best-way-for-many-classes-to-reference-a-database-connection-class ).
 *
 * All database connections (for MySQL) will be placed within this class. This should also help with the readability of code.
 *
 * I have also placed the .txt file reading logic in this .java file as well.
 */

public class DatabaseQueries {

    /**
     * Setup the database for the MySQL server
     */
    public void createDB(String mysqlQuery, String rootUser, String rootPass){
        PreparedStatement statement;
        try {
            Connection conn = DriverManager.getConnection(getURL() + ":" + getPort(), rootUser, rootPass);
            conn.setAutoCommit(false);
            statement = conn.prepareStatement(mysqlQuery);
            statement.executeUpdate();
            conn.commit();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error in creating database: " + mysqlQuery + "\n e");
            e.printStackTrace();
        }
    }

    /**
     * For creating the basic user
     * @param mysqlQuery = the query
     * @param rootUser = the root user
     * @param rootPass = the root password (needed for creating a basic user)
     * @param user = the new user
     */
    public void createUser(String mysqlQuery, String rootUser, String rootPass, String user){
        PreparedStatement statement;
        try {
            Connection conn = DriverManager.getConnection(getFullURL(), rootUser, rootPass);
            conn.setAutoCommit(false);
            statement = conn.prepareStatement(mysqlQuery);
            statement.setString(1, user);
            statement.executeUpdate();
            conn.commit();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error in creating database: " + mysqlQuery + "\n" + e);
            e.printStackTrace();
        }
    }
    
    /**
     * For creating the basic user
     * @param mysqlQuery = the query
     * @param rootUser = the root user
     * @param rootPass = the root password (needed for creating a basic user)
     * @param user = the new user
     * @param pass = the new user's password.
     */
    public void createUserSetPassword(String mysqlQuery, String rootUser, String rootPass, String user, String pass){
        PreparedStatement statement;
        try {
            Connection conn = DriverManager.getConnection(getFullURL(), rootUser, rootPass);
            conn.setAutoCommit(false);
            statement = conn.prepareStatement(mysqlQuery);
            statement.setString(1, user);
            statement.setString(2, pass);
            statement.executeUpdate();
            conn.commit();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error in creating database: " + mysqlQuery + "\n" + e);
            e.printStackTrace();
        }
    }

    /**
     * Grant permissions to the new user
     * @param mysqlQuery = the query
     * @param rootUser = the root user
     * @param rootPass = the root password
     * @param user = the new user
     */
    public void grantPermissions(String mysqlQuery, String rootUser, String rootPass, String user){
        PreparedStatement statement;
        try {
            Connection conn = DriverManager.getConnection(getFullURL(), rootUser, rootPass);
            conn.setAutoCommit(false);
            statement = conn.prepareStatement(mysqlQuery);
            statement.setString(1, user);
            statement.executeUpdate();
            conn.commit();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error granting permissions: " + mysqlQuery + "\n e");
            e.printStackTrace();
        }
    }

    /**
     * basic query to the database
     * @param mysqlQuery = the query
     * @return return the results of the query.
     */
    public ResultSet queryToDatabase(String mysqlQuery){
        ResultSet rs = null;
        try {
            Connection conn = DriverManager.getConnection(getFullURL(), getUser(), getPass());
            rs = conn.createStatement().executeQuery(mysqlQuery);
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Server Connection Error");
            alert.setHeaderText("Please Restart the server/Raspberry Pi");
            if(e.toString().equals("java.sql.SQLSyntaxErrorException: Table 'test.bfastMenu' doesn't exist")){
                alert.setContentText("Please setup the weekly menu to access this feature.");
            } else {
                alert.setContentText("If the problem still persists inform the person who set it up about:\n" + e.toString());
            }

            alert.showAndWait();
        }
        return rs;
    }

    /**
     * basic query to the database
     * @param mysqlQuery = the query
     * @return return the results of the query.
     */
    public void updateHallName(String mysqlQuery, String hallName, String hallID){
        PreparedStatement statement;
        try {
            Connection conn = DriverManager.getConnection(getFullURL(), getUser(), getPass());
            conn.setAutoCommit(false);
            statement = conn.prepareStatement(mysqlQuery);
            statement.setString(1, hallName);
            statement.setString(2, hallID);
            statement.executeUpdate();
            conn.commit();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Server Connection Error");
            alert.setHeaderText("Please Restart the server/Raspberry Pi");
            alert.setContentText("If the problem still persists inform the person who set it up about:\n" + e);

            alert.showAndWait();
        }
    }

    /**
     *
     * @param mysqlQuery = the query
     * @param itemType
     * @param itemName
     * @param itemDescription
     */
    public void rootQuery(String mysqlQuery, String itemType, String itemName, String itemDescription){
        PreparedStatement statement;
        try {
            Connection conn = DriverManager.getConnection(getFullURL(), getUser(), getPass());
            statement = conn.prepareStatement(mysqlQuery);
            statement.setString(1, itemType);
            statement.setString(2, itemName);
            statement.setString(3, itemDescription);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Server Connection Error");
            alert.setHeaderText("Please Restart the server/Raspberry Pi");
            alert.setContentText("If the problem still persists inform the person who set it up about:\n" + e);

            alert.showAndWait();
        }
    }

    //////////////////////////////////////////////////////////////////////////// FOR UPDATING THE TABLES ///////////////
    /**
     * @param mysqlQuery = the database query
     * @param primaryDish = the primary meal dish that was inputted
     * @param side1 = the first side.
     * @param side2 = the second side.
     * @param side3 = the third side. Not all meals have 3 sides. So these can be left blank if needed for the menu.
     * @param dessert = the dessert
     * @param weekID = weekID (this identifies what week should be updated)
     * @param dayID = dayID (this identifies what day should be updated)
     */
    public void updateMenuValues(String mysqlQuery, String primaryDish, String side1, String side2, String side3, String dessert, String weekID, String dayID){
        PreparedStatement statement;
        try {
            Connection conn = DriverManager.getConnection(getFullURL(), getUser(), getPass());
            conn.setAutoCommit(false);
            statement = conn.prepareStatement(mysqlQuery);
            statement.setString(1, primaryDish);
            statement.setString(2, side1);
            statement.setString(3, side2);
            statement.setString(4, side3);
            statement.setString(5, dessert);
            statement.setInt(6, Integer.parseInt(weekID));
            statement.setInt(7, Integer.parseInt(dayID));
            statement.executeUpdate();
            conn.commit();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error in update table with query: " + mysqlQuery);
            e.printStackTrace();
        }
    }

    /**
     * @param mysqlQuery = the database query
     * @param occupied = an int between 0 and 1. if it is a 0 that means the room is empty if it is a 1 then the room has an occupant.
     * @param fName  = first name
     * @param lName = last name
     * @param foodDiet = any food restrictions. For example. if someone is mech-soft instead of being able to eat regularly.
     * @param liquidDiet = a liquid restriction. If someone cannot have normal liquid then it needs to be thickened up for them. (like nectar thick.)
     * @param otherNotes = any other notes that may be needed. While For example, special drinking cups, modified silverware.
     * @param roomID = we need the room id so we can update the room as needed. 
     */
    public void updateRoomValues(String mysqlQuery, int occupied, String fName, String lName, String foodDiet, String liquidDiet, String otherNotes, String roomID){
        PreparedStatement statement;
        try {
            Connection conn = DriverManager.getConnection(getFullURL(), getUser(), getPass());
            conn.setAutoCommit(false);
            statement = conn.prepareStatement(mysqlQuery);
            statement.setInt(1, occupied);
            statement.setString(2, fName);
            statement.setString(3, lName);
            statement.setString(4, foodDiet);
            statement.setString(5, liquidDiet);
            statement.setString(6, otherNotes);
            statement.setInt(7, Integer.parseInt(roomID));
            statement.executeUpdate();
            conn.commit();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error in update table with query: " + mysqlQuery);
            e.printStackTrace();
        }
    }

    /**
     *
     * @param itemType = The type of item
     * @param itemName = The name of the item
     * @param itemDescription = A description of the item.
     */
    public void updateDailyValues(String mysqlQuery, String itemType, String itemName, String itemDescription, String itemID){
        PreparedStatement statement;
        try {
            Connection conn = DriverManager.getConnection(getFullURL(), getUser(), getPass());
            conn.setAutoCommit(false);
            statement = conn.prepareStatement(mysqlQuery);
            statement.setString(1, itemType);
            statement.setString(2, itemName);
            statement.setString(3, itemDescription);
            statement.setString(4, itemID);
            statement.executeUpdate();
            conn.commit();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error in update table with query: " + mysqlQuery);
            e.printStackTrace();
        }
    }

    /////////////////////////////////////////////////////////////////////// GET VARIABLES AND READ FILES ///////////////
    /**
     * Get the URL and set it up so it is ready to be used right away.
     * @return
     */
    public String getURL(){
        String url = "jdbc:mysql://";
        url = url + readfile("server information.txt", 0);
        return url;
    }

    /**
     * get the raw URL (so no fancy "jdbc" stuff)
     * @return
     */
    public String getRawURL(){
        return readfile("server information.txt", 0);
    }

    /**
     * get the user information
     * @return
     */
    public String getUser(){
        return readfile("server information.txt", 1);
    }

    /**
     * get the user password
     * @return
     */
    public String getPass(){
        return readfile("server information.txt", 2);
    }

    /**
     * get the port.
     * @return
     */
    public String getPort() {
        return readfile("server information.txt", 3);
    }

    /**
     * get the Database name
     * @return
     */
    public String getDbName() {
        return readfile("db name.txt", 0);
    }

    /**
     * Get the full URL (make it easier to remember how the whole connection URL needs to be made if it doesn't need to be remade).
     * @return
     */
    private String getFullURL(){
        return getURL() + ":" + getPort() + "/" + getDbName();
    }

    /////////////////////////////////////////// READ .TXT FILE (mostly used for database connections ///////////////////
    /**
     *
     * @param fileName =  the file name that needs to be read
     * @param dataNum = the line of data that is desired. For example, dataNum 0 of server information.txt is the 1 line if the txt file.
     * @return = return that data that was read from the file.
     */
    private String readfile(String fileName, int dataNum){
        String str = "";
        int i = 0;
        try {
            File file = new File(fileName);
            if (file.exists()) {
                Scanner sc = new Scanner(file);
                str = readLine(sc, i, dataNum);
                sc.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     *
     * @param sc = the loaded text file.
     * @param i = an integer for gathering values.
     * @param dataNum = the specific row of information wanted
     * @return
     */
    private String readLine(Scanner sc, int i, int dataNum){
        while (sc.hasNext()) {
            String str = sc.nextLine();
            if ((i == 0) && (i == dataNum)) {
                // url
                return str;
            } else if ((i == 1) && (i == dataNum)) {
                // user
                return str;
            } else if (i == 2 && i == dataNum) {
                // password
                return str;
            } else if (i == 3 && i == dataNum){
                if (!"".equals(str)) {
                    // port
                    return str;
                }
            }
            i++;
        }
        return "";
    }
}

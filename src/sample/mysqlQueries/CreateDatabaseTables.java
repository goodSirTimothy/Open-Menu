package sample.mysqlQueries;

import javafx.scene.control.Alert;

import java.sql.*;

public class CreateDatabaseTables {

    /**
     * table creation functions. each is separate because that should make it easier to find and adjust code.
     * P.S. Order of these function DOES matter; because of foreign keys.
     */
    public void createTables(String rootUser, String  rootPass){
        DatabaseQueries getInfo = new DatabaseQueries();
        try {
            Connection conn = DriverManager.getConnection(getInfo.getURL() + ":" + getInfo.getPort() + "/" + getInfo.getDbName(), rootUser, rootPass);
            createHallwayTable(conn);
            createRoomTable(conn);
            createMonthlySpecial(conn);
            createWeeklySpecial(conn);
            createDailySpecial(conn);
            createBreakfast(conn);
            createOrders(conn);
        } catch (SQLException e) {
            System.out.println("Error in creating database: \n" + e);
            e.printStackTrace();
        }
    }

    /**
     * CREATE HALLWAY TABLE
     * hallID (primary key, auto incrementing)
     * @param conn = The connection to the database.
     */
    private void createHallwayTable(Connection conn){
        try {
            Statement stmt;
            String query = "CREATE TABLE IF NOT EXISTS hallway ("
                    + "hallID int NOT NULL AUTO_INCREMENT,\n"
                    + "hallName varchar(45) NOT NULL,\n"
                    + "PRIMARY KEY (hallID)" + ");";
            stmt = conn.createStatement();
            stmt.execute(query);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * CREATE ROOM TABLE
     * inherits hallID
     * occupied (TINYINT(1) NOT NULL), TINYINT(1) is like boolean. You can only have one value (0-9)
     * @param conn = The connection to the database.
     */
    private void createRoomTable (Connection conn){
        try {
            Statement stmt;
            String query = "CREATE TABLE IF NOT EXISTS room ("
                    + "roomID int NOT NULL,\n"
                    + "hallID int NOT NULL,\n"
                    + "occupied TINYINT(1) NOT NULL, fName varchar(45), lName varchar(45), foodDiet varchar(45), liquidDiet varchar(45), otherNotes varchar(255),\n"
                    + "PRIMARY KEY(roomID),\n"
                    + "FOREIGN KEY(`hallID`) REFERENCES `hallway`(`hallID`)\n" + ");";
            stmt = conn.createStatement();
            stmt.execute(query);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * A special menu meant for monthly promotions
     * @param conn
     */
    private void createMonthlySpecial (Connection conn){
        try {
            Statement stmt;
            String query = "CREATE TABLE IF NOT EXISTS monthlySpecial ("
                    + "itemID int NOT NULL AUTO_INCREMENT,\n"
                    + "typeOfItem varchar(45) NOT NULL,\n"
                    + "itemName varchar(45), itemDescription varchar(255),\n"
                    + "PRIMARY KEY (itemID)" + ");";
            stmt = conn.createStatement();
            stmt.execute(query);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * A special menu for promotions that will change each day of the week
     * @param conn
     */
    private void createWeeklySpecial (Connection conn){
        try {
            Statement stmt;
            String query = "CREATE TABLE IF NOT EXISTS weeklySpecial ("
                    + "itemID int NOT NULL AUTO_INCREMENT,\n"
                    + "dayID varchar(11) NOT NULL,\n"
                    + "itemName varchar(45), itemDescription varchar(255),\n"
                    + "PRIMARY KEY (itemID)" + ");";
            stmt = conn.createStatement();
            stmt.execute(query);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the daily special menu (this is the menu that people can order daily
     * @param conn
     */
    private void createDailySpecial (Connection conn){
        try {
            Statement stmt;
            String query = "CREATE TABLE IF NOT EXISTS dailySpecial ("
                    + "itemID int NOT NULL AUTO_INCREMENT,\n"
                    + "typeOfItem varchar(45) NOT NULL,\n"
                    + "itemName varchar(45), itemDescription varchar(255),\n"
                    + "PRIMARY KEY (itemID)" + ");";
            stmt = conn.createStatement();
            stmt.execute(query);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Create the daily special menu (this is the menu that people can order daily
     * @param conn
     */
    private void createBreakfast (Connection conn){
        try {
            Statement stmt;
            String query = "CREATE TABLE IF NOT EXISTS breakfast ("
                    + "itemID int NOT NULL AUTO_INCREMENT,\n"
                    + "typeOfItem varchar(45) NOT NULL,\n"
                    + "itemName varchar(45), itemDescription varchar(255),\n"
                    + "PRIMARY KEY (itemID)" + ");";
            stmt = conn.createStatement();
            stmt.execute(query);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Create the daily special menu (this is the menu that people can order daily
     * @param conn
     */
    private void createOrders (Connection conn){
        try {
            Statement stmt;
            String query = "CREATE TABLE IF NOT EXISTS orders ("
                    + "orderID int NOT NULL AUTO_INCREMENT,\n"
                    + "roomID int NOT NULL,\n"
                    + "primaryDish varchar(100), sides varchar(100), drinks varchar(50), desserts varchar(50),\n"
                    + "month int NOT NULL, day int NOT NULL, year int NOT NULL,\n"
                    + "ordered TINYINT(1) NOT NULL, served TINYINT(1) NOT NULL, mealTime varchar(20),\n"
                    + "FOREIGN KEY(`roomID`) REFERENCES `room`(`roomID`),\n"
                    + "PRIMARY KEY (orderID)" + ");";
            stmt = conn.createStatement();
            stmt.execute(query);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This is an if statement to check if the hallway is visible
     * @param hallName = the text field for the hallway name.
     * @param lowNumber = the lowest number for the hallway.
     * @param highNumber = the highest number for the hallway.
     */
    public void hallwayIfVisible(String hallName, String lowNumber, String highNumber){
        DatabaseQueries getInfo = new DatabaseQueries();
        if (!"".equals(hallName)) {
            writeHallInfo(hallName, getInfo.getURL() + ":" + getInfo.getPort() + "/" + getInfo.getDbName(), getInfo.getUser(), getInfo.getPass());
            try {
                int roomLow = Integer.parseInt(lowNumber);
                int roomHigh = Integer.parseInt(highNumber);
                writeRoomInfo(roomLow, roomHigh, hallName, getInfo.getURL() + ":" + getInfo.getPort() + "/" + getInfo.getDbName(), getInfo.getUser(), getInfo.getPass());
            } catch (NumberFormatException ignored) {
                // No need for this because I already check all the inputs in "checkInputs()"
                // but because you cannot parse a string into an int without NumberFormatException, I had to keep this bit of code.
            }
        }
    }

    /**
     * Write the hallway information into the hallway table
     * @param hallInput = the name of the hallway
     * @param url = the location of the database
     * @param user = username
     * @param pass = password
     */
    private void writeHallInfo(String hallInput, String url, String user, String pass){
        PreparedStatement statement;
        try {
            Connection conn = DriverManager.getConnection(url, user, pass);
            conn.setAutoCommit(false);
            statement = conn.prepareStatement("INSERT IGNORE INTO hallway ("
                    + "hallName)"
                    + "VALUES (?)");
            statement.setString(1, hallInput);
            statement.executeUpdate();
            conn.commit();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error in writeHallInformation: ");
            e.printStackTrace();
        }
    }

    /**
     * Write the room information into the room table
     * @param roomLow = the lowest number
     * @param roomHigh = the highest number
     * @param hallInput = room name (for setting the hallID)
     * @param url = the location of the database
     * @param user = username
     * @param pass = password
     */
    private void writeRoomInfo(int roomLow, int roomHigh, String hallInput, String url, String user, String pass){
        PreparedStatement statement;

        for (int roomNum  = roomLow; roomNum <= roomHigh; roomNum++) {
            try {
                Connection conn = DriverManager.getConnection(url, user, pass);
                conn.setAutoCommit(false);
                statement = conn.prepareStatement("INSERT INTO room (roomID, hallID, occupied)"
                        + "VALUES (?, (SELECT hallID FROM hallway WHERE hallName = ?), ?)");
                statement.setInt(1, roomNum);
                statement.setString(2, hallInput);
                statement.setInt(3, 0);
                statement.executeUpdate();
                conn.commit();
                statement.close();
            } catch (SQLException e) {
                System.out.println("Error in writeRoomInfo: ");
                e.printStackTrace();
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////// RESET ////////////////////////////////

    /**
     * If ok is clicked on the alert box.
     * @param rootUser = the root user
     * @param rootPass = the root password
     */
    public void resetAlertOk(String rootUser, String  rootPass) {
        // Reset Tables
        resetTable("room", rootUser, rootPass);
        resetTable("hallway", rootUser, rootPass);
        resetTable("breakfast", rootUser, rootPass);
        resetTable("dailySpecial", rootUser, rootPass);
        resetTable("weeklySpecial", rootUser, rootPass);
        resetTable("monthlySpecial", rootUser, rootPass);
        // Create Tables
        createTables(rootUser, rootPass);
    }

    /**
     * this function deletes the tables. First room and then hallway. Order does matter because of foreign keys.
     * @param tableName = the table name
     * @param rootUser = the root user
     * @param rootPass = the root password
     */
    private void resetTable(String tableName, String rootUser, String rootPass) {
        DatabaseQueries getInfo = new DatabaseQueries();
        try {
            Connection conn = DriverManager.getConnection(getInfo.getURL() + ":" + getInfo.getPort() + "/" + getInfo.getDbName(), rootUser, rootPass);
            DatabaseMetaData dbm = conn.getMetaData();
            // check if "employee" table is there
            ResultSet tables = dbm.getTables(null, null, tableName, null);
            if (tables.next()) {
                Statement stmt;
                String query;
                query = "DROP TABLE IF EXISTS " + tableName + ";";
                stmt = conn.createStatement();
                stmt.execute(query);
                stmt.close();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Reset Complete");
                alert.setHeaderText(null);
                alert.setContentText("All data has been reset.");

                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Reset Error");
                alert.setHeaderText(null);
                alert.setContentText("Could not reset database...");

                alert.showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Unknown Error");
            alert.setHeaderText(null);
            alert.setContentText("Something went wrong with the reset: " + e);

            alert.showAndWait();
            System.out.println("error with: " + tableName);

        }
    }
}

package sample.mysqlQueries;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.sql.*;

public class DatabaseCreateMenuTables {
    /**
     *
     * @param rootUser = the root user
     * @param rootPass = the root password
     * @param tableName = the database name
     * @param num = the number of weeks
     */
    public void mysqlTableCreate(String rootUser, String rootPass, String tableName, int num){
        DatabaseQueries queryURL = new DatabaseQueries();
        try{
            Connection conn = DriverManager.getConnection(queryURL.getURL() + ":" + queryURL.getPort() + "/" + queryURL.getDbName(), rootUser, rootPass);
            Statement stmt;
            String query = "CREATE TABLE IF NOT EXISTS " + tableName + " ("
                    + "weekID int NOT NULL,\n"
                    + "dayID int NOT NULL,\n"
                    + "mainDish varchar(45),\n"
                    + "side1 varchar(45),\n"
                    + "side2 varchar(45),\n"
                    + "side3 varchar(45),\n"
                    + "dessert varchar(45)\n"
                    + ");";
            stmt = conn.createStatement();
            stmt.execute(query);
            stmt.close();

            // a for loop to go through all the days of the week for however many weeks there are. NOTE: I start at 1 so I don't need to add 1 everytime I pass j.
            for (int i = 1; i < num+1; i++) {
                for (int j = 1; j < 7 + 1; j++) {
                    mysqlInsertIntoTable(conn, tableName, i, j);
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Server Connection Error");
            alert.setHeaderText(null);
            alert.setContentText("If the problem still persists inform the person who set it up about:\n" + e);

            alert.showAndWait();
        }
    }

    /**
     *
     * @param conn = the connection
     * @param tableName = the table name
     * @param i = the number for weeks
     * @param j = the number for days
     */
    private void mysqlInsertIntoTable(Connection conn, String tableName, int i, int j){
        // convert the number into the proper day so that it is easier for the user to understand what day they are filling out.
        String day = "";
        if(j==1){
            day = "Sunday";
        } else if (j == 2){
            day = "Monday";
        } else if (j == 3){
            day = "Tuesday";
        } else if (j == 4){
            day = "Wednesday";
        } else if (j == 5){
            day = "Thursday";
        } else if (j == 6){
            day = "Friday";
        } else if (j == 7){
            day = "Saturday";
        }
        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        // A switch case so that
        switch (tableName) {
            case "bfastMenu":
                dialog.setTitle("Breakfast Menu Items");
                dialog.setHeaderText("Week " + i + ": " + day);
                break;
            case "lunchMenu":
                dialog.setTitle("Lunch Menu Items");
                dialog.setHeaderText("To insert the menu items just fill out the text boxes!");
                break;
            case "supperMenu":
                dialog.setTitle("Supper Menu Items");
                dialog.setHeaderText("To insert the menu items just fill out the text boxes!");
                break;
        }

        // Set the button types.
        ButtonType submitButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType);

        // Grid Pane for setting up the layout for the dialog box.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // text fields, prompt text (so the user knows what to input and load any old inputted information.
        TextField feature = new TextField();
        TextField side1 = new TextField();
        TextField side2 = new TextField();
        TextField side3 = new TextField();
        TextField dessert = new TextField();

        // Add labels and text fields to the grid
        if(tableName.equals("bfastMenu")){
            grid.add(new Label("Feature Meal:"), 0, 0);
            grid.add(feature, 1, 0);
            grid.add(new Label("Sides:"), 0, 1);
            grid.add(side1, 1, 1);
            grid.add(side2, 2, 1);
            grid.add(side3, 3, 1);
            // do to the fact that breakfast does not have a dessert, the program will prompt for a drink instead.
            grid.add(new Label("Drink:"), 0, 2);
            grid.add(dessert, 1, 2);

        } else{
            grid.add(new Label("Feature Meal:"), 0, 0);
            grid.add(feature, 1, 0);
            grid.add(new Label("Sides:"), 0, 1);
            grid.add(side1, 1, 1);
            grid.add(side2, 2, 1);
            grid.add(side3, 3, 1);
            grid.add(new Label("Dessert:"), 0, 2);
            grid.add(dessert, 1, 2);
        }

        // add the Grid Pane to the dialog box
        dialog.getDialogPane().setContent(grid);

        // check any buttons pressed on the dialog box
        dialog.setResultConverter(dialogButton -> {
            // update menu information
            if (dialogButton == submitButtonType) {
                mysqlTablePopulate(conn, tableName, i, j, feature.getText(), side1.getText(), side2.getText(), side3.getText(), dessert.getText());
            }
            // Reset the table views so it will load the new data.
            return null;
        });
        // showAndWait() is the last method to be called, because it displays the dialog box.
        dialog.showAndWait();
    }

    /**
     *
     * @param conn = the connection
     * @param tableName = the table name
     * @param i = the number for weeks
     * @param j = the number for days
     * @param feature = the feature meal
     * @param side1 = the side
     * @param side2 = another side
     * @param side3 = the last side
     * @param dessert = the dessert.
     */
    private void mysqlTablePopulate(Connection conn, String tableName, int i, int j, String feature, String side1, String side2, String side3, String dessert){
        PreparedStatement statement;
        // starting at 1 for the for loop because that is easier for display what week it is.
        try {
            statement = conn.prepareStatement("INSERT INTO " + tableName + " (weekID, dayID, mainDish, side1, side2, side3, dessert)"
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)");
            statement.setInt(1, i);
            statement.setInt(2, j);
            statement.setString(3, feature);
            statement.setString(4, side1);
            statement.setString(5, side2);
            statement.setString(6, side3);
            statement.setString(7, dessert);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Could not write data");
            alert.setHeaderText(null);
            alert.setContentText("Oops, the information could not be written. ERROR:\n" + e);

            alert.showAndWait();
        }
    }
}

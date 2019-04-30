package sample.editValues;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import sample.mysqlQueries.DatabaseQueries;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class EditMenuValues  implements Initializable {
    @FXML
    private TableView<MenuModelTable> bfastTable,lunchTable, supperTable;
    @FXML
    private TableColumn<MenuModelTable, String> colBfastWeek, colLunchWeek, colSupperWeek, colBfastDay, colLunchDay, colSupperDay,
            colBfastPrimaryDish,colLunchPrimaryDish,colSupperPrimaryDish,
            colBfastSide1,colLunchSide1,colSupperSide1, colBfastSide2,colLunchSide2,colSupperSide2, colBfastSide3,colLunchSide3,colSupperSide3,
            colBfastDessert,colLunchDessert,colSupperDessert;

    private ObservableList<MenuModelTable> obListBfast = FXCollections.observableArrayList(), obListLunch = FXCollections.observableArrayList(), obListSupper = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadTables();
    }

    /**
     * loadTables is called at during initialize and is also called whenever the dialog box is opened.
     */
    private void loadTables(){
        connectToDatabase();
        setupTables(colBfastWeek, colBfastDay, colBfastPrimaryDish, colBfastSide1, colBfastSide2, colBfastSide3, colBfastDessert, bfastTable, obListBfast);
        setupTables(colLunchWeek, colLunchDay, colLunchPrimaryDish, colLunchSide1, colLunchSide2, colLunchSide3, colLunchDessert, lunchTable, obListLunch);
        setupTables(colSupperWeek, colSupperDay, colSupperPrimaryDish, colSupperSide1, colSupperSide2, colSupperSide3, colSupperDessert, supperTable, obListSupper);
    }

    /**
     * Method to query the database and then load that information into an Observable List.
     */
    private void connectToDatabase(){
        DatabaseQueries query = new DatabaseQueries();
        try {
            ResultSet rs = query.queryToDatabase("SELECT * FROM bfastMenu;");
            while(rs.next()){
                obListBfast.add(new MenuModelTable( "Breakfast", rs.getString("weekID"), rs.getString("dayID"),
                        rs.getString("mainDish"), rs.getString("side1"), rs.getString("side2"),
                        rs.getString("side3"), rs.getString("dessert")));
            }
            rs = query.queryToDatabase("SELECT * FROM lunchMenu;");
            while(rs.next()){
                obListLunch.add(new MenuModelTable("Lunch", rs.getString("weekID"), rs.getString("dayID"),
                        rs.getString("mainDish"), rs.getString("side1"),
                        rs.getString("side2"), rs.getString("side3"),
                        rs.getString("dessert")));
            }
            rs = query.queryToDatabase("SELECT * FROM supperMenu;");
            while(rs.next()){
                obListSupper.add(new MenuModelTable("Supper", rs.getString("weekID"), rs.getString("dayID"),
                        rs.getString("mainDish"), rs.getString("side1"),
                        rs.getString("side2"), rs.getString("side3"),
                        rs.getString("dessert")));
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println("Error: Cannot Connect to Database...");
            e.printStackTrace();
        }
    }

    /**
     * I pass the columns for each of the tables into this meathod so I don't have to write the same code lines of code 3 times
     * @param week = the week column
     * @param day = the day column
     * @param primaryDish = the primary dish column
     * @param side1 = side 1 column
     * @param side2 = side 2 column
     * @param side3 = side 3 column
     * @param dessert = the dessert column
     * @param table = the table view
     * @param obList = the Observable List.
     */
    private void setupTables(TableColumn week, TableColumn day, TableColumn primaryDish,
                             TableColumn side1, TableColumn side2, TableColumn side3,
                             TableColumn dessert, TableView table, ObservableList obList){

        // Set up the tables
        setupTable(week, "week");
        setupTable(day, "day");
        setupTable(primaryDish, "primaryDish");
        setupTable(side1, "side1");
        setupTable(side2, "side2");
        setupTable(side3, "side3");
        setupTable(dessert, "dessert");

        // set the Observable List to the table.
        table.setItems(obList);
        // set on click for the table.
        table.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() > 1) {
                onEdit();
            }
        });
    }

    /**
     *
     * @param column = the column being passed in.
     * @param str = the String that is supposed to be found in MenuModelTable
     */
    private void setupTable(TableColumn column, String str){
        column.setCellValueFactory(new PropertyValueFactory<>(str));
    }

    /**
     * If the table is double clicked then onEdit will start
     */
    private void onEdit() {
        // check the table's selected item and get selected item
        if (bfastTable.getSelectionModel().getSelectedItem() != null) {
            MenuModelTable selectedMenu = bfastTable.getSelectionModel().getSelectedItem();
            dialogBox("bfastMenu", selectedMenu.getWeek(), selectedMenu.getDay(),
                    selectedMenu.getPrimaryDish(), selectedMenu.getSide1(), selectedMenu.getSide2(), selectedMenu.getSide3(), selectedMenu.getDessert());
        }else if (lunchTable.getSelectionModel().getSelectedItem() != null) {
            MenuModelTable selectedMenu = lunchTable.getSelectionModel().getSelectedItem();
            dialogBox("lunchMenu", selectedMenu.getWeek(), selectedMenu.getDay(),
                    selectedMenu.getPrimaryDish(), selectedMenu.getSide1(), selectedMenu.getSide2(), selectedMenu.getSide3(), selectedMenu.getDessert());
        }else if (supperTable.getSelectionModel().getSelectedItem() != null) {
            MenuModelTable selectedMenu = supperTable.getSelectionModel().getSelectedItem();
            dialogBox("supperMenu", selectedMenu.getWeek(), selectedMenu.getDay(),
                    selectedMenu.getPrimaryDish(), selectedMenu.getSide1(), selectedMenu.getSide2(), selectedMenu.getSide3(), selectedMenu.getDessert());
        }
        // I clear the table selections right way just incase there is more then one table selected.
        bfastTable.getSelectionModel().clearSelection();
        lunchTable.getSelectionModel().clearSelection();
        supperTable.getSelectionModel().clearSelection();
    }

    /**
     *
     * @param tableName = the name of the database table.
     * @param weekID = the ID of the week
     * @param dayID = the ID of the day. With these two IDs we can changing the exact MySQL menu data
     * @param strPrimaryDish = the primary dish (if one has been inputted into the database)
     * @param strSide1 = side 1 (if one has been inputted into the database)
     * @param strSide2 = side 2 (if one has been inputted into the database)
     * @param strSide3 = side 3 (if one has been inputted into the database)
     * @param strDessert = the dessert (if one has been inputted into the database)
     */
    private void dialogBox(String tableName, String weekID, String dayID, String strPrimaryDish, String strSide1, String strSide2, String strSide3, String strDessert){
        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Update Menu Items");
        dialog.setHeaderText("To update the menu items just fill out the text boxes!");

        // Set the button types.
        ButtonType submitButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);

        // Grid Pane for setting up the layout for the dialog box.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // text fields, prompt text (so the user knows what to input and load any old inputted information.
        TextField feature = new TextField();
        feature.setPromptText("Feature Meal");
        feature.setText(strPrimaryDish);
        TextField side1 = new TextField();
        side1.setPromptText("Side 1");
        side1.setText(strSide1);
        TextField side2 = new TextField();
        side2.setPromptText("Side 2");
        side2.setText(strSide2);
        TextField side3 = new TextField();
        side3.setPromptText("Side 3");
        side3.setText(strSide3);
        TextField dessert = new TextField();
        dessert.setPromptText("Dessert");
        dessert.setText(strDessert);

        // Add labels and text fields to the grid
        grid.add(new Label("Feature Meal:"), 0, 0);
        grid.add(feature, 1, 0);
        grid.add(new Label("Sides:"), 0, 1);
        grid.add(side1, 1, 1);
        grid.add(side2, 2, 1);
        grid.add(side3, 3, 1);
        grid.add(new Label("Dessert:"), 0, 2);
        grid.add(dessert, 1, 2);

        // add the Grid Pane to the dialog box
        dialog.getDialogPane().setContent(grid);

        // check any buttons pressed on the dialog box
        dialog.setResultConverter(dialogButton -> {
            // update menu information
            if (dialogButton == submitButtonType) {
                String mysqlQuery = "UPDATE " + tableName + " SET mainDish = ?, side1 = ?, side2 = ?, side3 = ?, dessert = ? WHERE weekID = ? AND dayID = ?";
                DatabaseQueries query = new DatabaseQueries();
                query.updateMenuValues(mysqlQuery, feature.getText(), side1.getText(), side2.getText(), side3.getText(), dessert.getText(), weekID, dayID);
            }
            // Reset the table views so it will load the new data.
            bfastTable.getItems().clear();
            lunchTable.getItems().clear();
            supperTable.getItems().clear();
            loadTables();
            return null;
        });
        // showAndWait() is the last method to be called, because it displays the dialog box.
        dialog.showAndWait();
    }
}

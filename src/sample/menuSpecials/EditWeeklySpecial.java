package sample.menuSpecials;

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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class EditWeeklySpecial implements Initializable {
    @FXML
    private TableView<WeeklySpecialsModelTable> weeklyTable;
    @FXML
    private TableColumn<WeeklySpecialsModelTable, String> colItemType, colItemName, colDescription;
    private ObservableList<WeeklySpecialsModelTable> obListItems = FXCollections.observableArrayList();

    // this set if Strings and int/boolean are if the moving selection has been pressed. This will save the old database information
    // so that the two rooms can be switched.
    private String moveDayID ="", moveItemName ="", moveDescription ="", moveItemID = "";
    private boolean completeMove = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadTables();
    }

    /**
     * loadTables is called at during initialize and is also called whenever the dialog box is opened.
     */
    private void loadTables(){
        connectToDatabase();
        setupTables(colItemType, colItemName, colDescription, weeklyTable, obListItems);
    }

    /**
     * Method to query the database and then load that information into an Observable List.
     */
    private void connectToDatabase(){
        DatabaseQueries query = new DatabaseQueries();
        try {
            // Send query to database. This asks for the hallway name linked to the hallwayID found in roomID.
            ResultSet rs = query.queryToDatabase("SELECT * " +
                    "FROM weeklySpecial " +
                    ";");
            while(rs.next()){
                obListItems.add(new WeeklySpecialsModelTable(rs.getString("itemID"), rs.getString("dayID"), rs.getString("itemName"), rs.getString("itemDescription")));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param dayID = The ID of the day.
     * @param ItemName = the room number.
     * @param Description = the occupied string (Yes or No).
     * @param table = the GUI table.
     * @param obList = the Observable List.
     */
    private void setupTables(TableColumn dayID, TableColumn ItemName, TableColumn Description,
                             TableView table, ObservableList obList){

        // Set up the tables
        setupTable(dayID, "dayID");
        setupTable(ItemName, "itemName");
        setupTable(Description, "itemDescription");

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
        if (weeklyTable.getSelectionModel().getSelectedItem() != null) {
            WeeklySpecialsModelTable selectedItem = weeklyTable.getSelectionModel().getSelectedItem();
            dialogWeeklySpecial(selectedItem.getDayID(), selectedItem.getItemName(), selectedItem.getItemDescription(), selectedItem.getItemID(), 1);
            // clear the selection of the table (technically not needed since this is only one table.
            weeklyTable.getSelectionModel().clearSelection();
        }
    }

    /**
     *
     * @param dayID = The ID of the day.
     * @param itemName = the name of the item
     * @param itemDescription = a discription (if needed)
     * @param itemID = the ID of the item (just for database stuff. Not for the user to see)
     * @param controller = whether or not there will need to be an admin login. 0 == root, 1 == normal user
     */
    private void dialogWeeklySpecial(String dayID, String itemName, String itemDescription, String itemID, int controller){
        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Update Room");
        dialog.setHeaderText("To update the room information just fill out the text boxes!");

        // Set the button types.
        ButtonType submitButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        ButtonType deleteButtonType = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
        ButtonType moveButtonType = new ButtonType("Move", ButtonBar.ButtonData.OK_DONE);
        ButtonType completeMoveButtonType = new ButtonType("Complete Move", ButtonBar.ButtonData.OK_DONE);

        // logic for what buttons to display.
        if(controller == 0) {
            dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);
        } else {
            if (completeMove) {
                dialog.getDialogPane().getButtonTypes().addAll(completeMoveButtonType, moveButtonType, deleteButtonType, ButtonType.CANCEL);
            } else {
                dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, moveButtonType, deleteButtonType, ButtonType.CANCEL);
            }
        }

        // Grid Pane for setting up the layout for the dialog box.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // text fields, prompt text (so the user knows what to input and load any old inputted information.
        ComboBox<String> dropBox = new ComboBox<>();
        dropBox.getItems().addAll("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");
        dropBox.getSelectionModel().select(dayID);

        TextField name = new TextField();
        setTextFieldInformation(name, "Item Name", itemName);

        TextField description = new TextField();
        setTextFieldInformation(description, "Item Description", itemDescription);

        // Add labels and text fields to the grid
        grid.add(new Label("Type Name:"), 0, 0);
        grid.add(dropBox, 1, 0);
        grid.add(new Label("Item Name:"), 0, 1);
        grid.add(name, 1, 1);
        grid.add(new Label("Item Description:"), 0, 2);
        grid.add(description, 1, 2);

        // add the Grid Pane to the dialog box
        dialog.getDialogPane().setContent(grid);

        // check any buttons pressed on the dialog box
        dialog.setResultConverter(dialogButton -> {
            // update the room information
            if (dialogButton == submitButtonType) {
                if(controller == 0) {
                    DatabaseQueries query = new DatabaseQueries();
                    String mysqlQuery = "INSERT IGNORE INTO weeklySpecial(dayID, itemName, itemDescription) " +
                            "VALUES (?, ?, ?)";
                    query.rootQuery(mysqlQuery, dropBox.getValue(), name.getText(), description.getText());
                } else {
                    submitPressed(dropBox.getValue(), name.getText(), description.getText(), itemID);
                }
            }
            // delete room information
            else if (dialogButton == deleteButtonType){
                deleteAlertBox(itemID);
            }
            // if move is pressed, backup all old information
            else if (dialogButton == moveButtonType) {
                moveOptionClicked(dayID, itemName, itemDescription, itemID);
            }
            // complete the room move/transfer (not sure which word to use)
            else if (dialogButton == completeMoveButtonType){
                completeMove(dayID, itemName, itemDescription, itemID);
            }
            // Reset the table view so it will load the new data.
            weeklyTable.getItems().clear();
            loadTables();
            return null;
        });
        // showAndWait() is the last method to be called, because it displays the dialog box.
        dialog.showAndWait();
    }

    /**
     * So I don't need to repeat the same code, I made a method to setup the text fields.
     * @param textField = the text field
     * @param prompt = the prompt text to inform the user what to input
     * @param oldInfo = any old room information that needs to be passed.
     */
    private void setTextFieldInformation(TextField textField, String prompt, String oldInfo){
        textField.setPromptText(prompt);
        textField.setText(oldInfo);
    }

    ////////////////////////////////////////////////////////////////// LOGIC FOR BUTTONS PRESSED IN THE DIALOG BOX ////

    /**
     * All of these come from the TextFields so I keep the same names as the text fields were called
     * @param dayID = The ID of the day.
     * @param itemName = The name of the item
     * @param itemDescription = A description of the item.
     */
    private void submitPressed(String dayID, String itemName, String itemDescription, String itemID){
        String mysqlQuery = "UPDATE " + "weeklySpecial " + "SET dayID = ?, itemName = ?, itemDescription = ? WHERE itemID = ?";
        DatabaseQueries query = new DatabaseQueries();
        query.updateDailyValues(mysqlQuery, dayID, itemName, itemDescription, itemID);
    }

    /**
     *
     * @param itemID = The ID of the item
     */
    private void deleteAlertBox(String itemID){
        // make an alert to confirm if the user wants to actually delete the information
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Room Information");
        alert.setHeaderText("Delete Room information");
        alert.setContentText("Do you want to delete room information? This cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            // update to delete all information and reset the occupied status to 0.
            String mysqlQuery = "UPDATE " + "weeklySpecial " + "SET dayID = ?, itemName = ?, itemDescription = ? WHERE itemID = ?";
            DatabaseQueries query = new DatabaseQueries();
            query.updateDailyValues(mysqlQuery, "Not Set", "", "", itemID);
        } else {
            // do nothing
        }
    }

    /**
     *
     * @param dayID = The ID of the day.
     * @param itemName = The name of the item
     * @param itemDescription = A description of the item.
     */
    private void moveOptionClicked(String dayID, String itemName, String itemDescription, String itemID){
        moveDayID = dayID;
        moveItemName = itemName;
        moveDescription = itemDescription;
        moveItemID = itemID;
        completeMove = true;
    }

    /**
     *
     * @param dayID = The ID of the day.
     * @param itemName = The name of the item
     * @param itemDescription = A description of the item.
     */
    private void completeMove(String dayID, String itemName, String itemDescription, String itemID){
        String mysqlQuery = "UPDATE " + "weeklySpecial " + "SET dayID = ?, itemName = ?, itemDescription = ? WHERE itemID = ?";
        DatabaseQueries query = new DatabaseQueries();
        // send query twice to switch rooms.
        query.updateDailyValues(mysqlQuery, moveDayID, itemName, itemDescription, moveItemID);
        query.updateDailyValues(mysqlQuery, dayID, moveItemName, moveDescription, itemID);
        completeMove = false;
    }

    public void addSpecialClicked() {
    	dialogWeeklySpecial("", "", "", "", 0);
    }
}
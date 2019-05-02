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

public class EditDailySpecial implements Initializable {
    @FXML
    private TableView<DailySpecialsModelTable> dailyTable;
    @FXML
    private TableColumn<DailySpecialsModelTable, String> colItemType, colItemName, colDescription;
    private ObservableList<DailySpecialsModelTable> obListItems = FXCollections.observableArrayList();

    // this set if Strings and int/boolean are if the moving selection has been pressed. This will save the old database information
    // so that the two rooms can be switched.
    private String moveItemType ="", moveItemName ="", moveDescription ="", moveItemID = "";
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
        setupTables(colItemType, colItemName, colDescription, dailyTable, obListItems);
    }

    /**
     * Method to query the database and then load that information into an Observable List.
     */
    private void connectToDatabase(){
        DatabaseQueries query = new DatabaseQueries();
        try {
            // Send query to database. This asks for the hallway name linked to the hallwayID found in roomID.
            ResultSet rs = query.queryToDatabase("SELECT * " +
                    "FROM dailySpecial " +
                    ";");
            while(rs.next()){
                obListItems.add(new DailySpecialsModelTable(rs.getString("itemID"), rs.getString("typeOfItem"), rs.getString("itemName"), rs.getString("itemDescription")));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param ItemType = The hallway name.
     * @param ItemName = the room number.
     * @param Description = the occupied string (Yes or No).
     * @param table = the GUI table.
     * @param obList = the Observable List.
     */
    private void setupTables(TableColumn ItemType, TableColumn ItemName, TableColumn Description,
                             TableView table, ObservableList obList){

        // Set up the tables
        setupTable(ItemType, "itemType");
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
        if (dailyTable.getSelectionModel().getSelectedItem() != null) {
            DailySpecialsModelTable selectedItem = dailyTable.getSelectionModel().getSelectedItem();
            dialogEditRoom(selectedItem.getItemType(), selectedItem.getItemName(), selectedItem.getItemDescription(), selectedItem.getItemID(), 1);
            // clear the selection of the table (technically not needed since this is only one table.
            dailyTable.getSelectionModel().clearSelection();
        }
    }

    /**
     *
     * @param itemType = the type of food (soups, entrees, drinks etc.)
     * @param itemName = the name of the item
     * @param itemDescription = a discription (if needed)
     * @param itemID = the ID of the item (just for database stuff. Not for the user to see)
     * @param controller = whether or not there will need to be an admin login. 0 == root, 1 == normal user
     */
    private void dialogEditRoom(String itemType, String itemName, String itemDescription, String itemID, int controller){
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
        dropBox.getItems().addAll("Entrees", "Sandwich", "Soups", "Sides", "Drinks", "Desserts");
        if(itemType.equals("")){
            dropBox.getSelectionModel().select(0);
        } else {
            dropBox.getSelectionModel().select(itemType);
        }


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
                    dialogAdminLogin(dropBox.getValue(), name.getText(), description.getText());
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
                moveOptionClicked(itemType, itemName, itemDescription, itemID);
            }
            // complete the room move/transfer (not sure which word to use)
            else if (dialogButton == completeMoveButtonType){
                completeMove(itemType, itemName, itemDescription, itemID);
            }
            // Reset the table view so it will load the new data.
            dailyTable.getItems().clear();
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
     * @param itemType = The type of item
     * @param itemName = The name of the item
     * @param itemDescription = A description of the item.
     */
    private void submitPressed(String itemType, String itemName, String itemDescription, String itemID){
        String mysqlQuery = "UPDATE " + "dailySpecial " + "SET typeOfItem = ?, itemName = ?, itemDescription = ? WHERE itemID = ?";
        DatabaseQueries query = new DatabaseQueries();
        query.updateDailyValues(mysqlQuery, itemType, itemName, itemDescription, itemID);
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
            String mysqlQuery = "UPDATE " + "dailySpecial " + "SET typeOfItem = ?, itemName = ?, itemDescription = ? WHERE itemID = ?";
            DatabaseQueries query = new DatabaseQueries();
            query.updateDailyValues(mysqlQuery, "", "", "", itemID);
        } else {
            // do nothing
        }
    }

    /**
     *
     * @param itemType = The type of item
     * @param itemName = The name of the item
     * @param itemDescription = A description of the item.
     */
    private void moveOptionClicked(String itemType, String itemName, String itemDescription, String itemID){
        moveItemType = itemType;
        moveItemName = itemName;
        moveDescription = itemDescription;
        moveItemID = itemID;
        completeMove = true;
    }

    /**
     *
     * @param itemType = The type of item
     * @param itemName = The name of the item
     * @param itemDescription = A description of the item.
     */
    private void completeMove(String itemType, String itemName, String itemDescription, String itemID){
        String mysqlQuery = "UPDATE " + "dailySpecial " + "SET typeOfItem = ?, itemName = ?, itemDescription = ? WHERE itemID = ?";
        DatabaseQueries query = new DatabaseQueries();
        // send query twice to switch rooms.
        query.updateDailyValues(mysqlQuery, itemType, itemName, itemDescription, moveItemID);
        query.updateDailyValues(mysqlQuery, moveItemType, moveItemName, moveDescription, itemID);
        completeMove = false;
    }

    public void addSpecialClicked() {
        dialogEditRoom("", "", "", "", 0);
    }
    private void dialogAdminLogin(String itemType, String itemName, String itemDescription){
        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Root Input");
        dialog.setHeaderText("Please input Root information");

        // Set the button types.
        ButtonType submitButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);


        // Grid Pane for setting up the layout for the dialog box.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // text fields, prompt text (so the user knows what to input and load any old inputted information.
        TextField rootUser = new TextField();
        PasswordField rootPass = new PasswordField();

        // Add labels and text fields to the grid
        grid.add(new Label("Username:"), 0, 0);
        grid.add(rootUser, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(rootPass, 1, 1);

        // add the Grid Pane to the dialog box
        dialog.getDialogPane().setContent(grid);

        // check any buttons pressed on the dialog box
        dialog.setResultConverter(dialogButton -> {
            // update the room information
            if (dialogButton == submitButtonType) {
                DatabaseQueries query = new DatabaseQueries();
                String mysqlQuery = "INSERT IGNORE INTO dailySpecial(typeOfItem, itemName, itemDescription) " +
                        "VALUES (?, ?, ?)";
                query.rootQuery(mysqlQuery, rootUser.getText(), rootPass.getText(), itemType, itemName, itemDescription);
            }
            return null;
        });
        // showAndWait() is the last method to be called, because it displays the dialog box.
        dialog.showAndWait();
    }
}
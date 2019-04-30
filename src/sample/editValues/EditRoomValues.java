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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class EditRoomValues implements Initializable {
    @FXML
    private TableView<RoomModelTable> roomTable;
    @FXML
    private TableColumn<RoomModelTable, String> colHallName, colRoomNum, colOccupied, colFName, colLName, colFoodDiet, colLiquidDiet, colOther;
    private ObservableList<RoomModelTable> obListRoom = FXCollections.observableArrayList();

    // this set if Strings and int/boolean are if the moving selection has been pressed. This will save the old database information
    // so that the two rooms can be switched.
    private String moveRoomNum ="", movefName ="", movelName ="", moveFoodDiet ="", moveLiquidDiet ="", moveOtherNotes ="";
    private int MoveOccupied = 0;
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
        setupTables(colHallName, colRoomNum, colOccupied, colFName,colLName,colFoodDiet,colLiquidDiet, colOther, roomTable,obListRoom);
    }

    /**
     * Method to query the database and then load that information into an Observable List.
     */
    private void connectToDatabase(){
        DatabaseQueries query = new DatabaseQueries();
        try {
            // Send query to database. This asks for the hallway name linked to the hallwayID found in roomID.
            ResultSet rs = query.queryToDatabase("SELECT hallway.hallName, room.roomID, room.occupied, " +
                    "room.fName,room.lName,room.foodDiet, room.liquidDiet,room.otherNotes " +
                    "FROM hallway " +
                    "INNER JOIN room ON hallway.hallID=room.hallID;");
            while(rs.next()){
                int occupied = Integer.parseInt(rs.getString("occupied"));
                String strOccupied;
                // check if the room has a 1 or 0. It wouldn't make sense to the user to see a 1/0 so I place Yas and No instead.
                if(occupied == 1){
                    strOccupied = "Yes";
                } else {
                    strOccupied = "No";
                }
                obListRoom.add(new RoomModelTable(rs.getString("hallName"), rs.getString("roomID"),
                        strOccupied, rs.getString("fName"),
                        rs.getString("lName"), rs.getString("foodDiet"),
                        rs.getString("liquidDiet"), rs.getString("otherNotes")));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param hallName = The hallway name.
     * @param roomNum = the room number.
     * @param occupied = the occupied string (Yes or No).
     * @param fName = the first name.
     * @param lName = the last name.
     * @param foodDiet = the food restriction.
     * @param liquidDiet = the liquid restrictions.
     * @param otherNotes = any other notes needing to be made.
     * @param table = the GUI table.
     * @param obList = the Observable List.
     */
    private void setupTables(TableColumn hallName, TableColumn roomNum, TableColumn occupied,
                             TableColumn fName, TableColumn lName, TableColumn foodDiet,
                             TableColumn liquidDiet, TableColumn otherNotes,TableView table, ObservableList obList){

        // Set up the tables
        setupTable(hallName, "hallName");
        setupTable(roomNum, "roomNum");
        setupTable(occupied, "occupied");
        setupTable(fName, "firstName");
        setupTable(lName, "lastName");
        setupTable(foodDiet, "foodDiet");
        setupTable(liquidDiet, "liquidDiet");
        setupTable(otherNotes, "otherNotes");

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
        if (roomTable.getSelectionModel().getSelectedItem() != null) {
            RoomModelTable selectedRoom = roomTable.getSelectionModel().getSelectedItem();
            dialogEditRoom(selectedRoom.getRoomNum(), selectedRoom.getFirstName(), selectedRoom.getLastName(),
                    selectedRoom.getFoodDiet(), selectedRoom.getLiquidDiet(), selectedRoom.getOtherNotes(), selectedRoom.getOccupied());
            // clear the selection of the table (technically not needed since this is only one table.
            roomTable.getSelectionModel().clearSelection();
        }
    }

    /**
     *
     * @param roomID = The Room ID number
     * @param fName = First name
     * @param lName = Last name
     * @param foodDiet = food restriction
     * @param liquidDiet = liquid restrictions
     * @param otherNotes = any other notes
     * @param occupied = if the room is occupied
     */
    private void dialogEditRoom(String roomID, String fName, String lName, String foodDiet, String liquidDiet, String otherNotes, String occupied){
        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Update Room");
        dialog.setHeaderText("To update the room information just fill out the text boxes!");

        // Set the button types.
        ButtonType submitButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        ButtonType deleteButtonType = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
        ButtonType moveButtonType = new ButtonType("Move", ButtonBar.ButtonData.OK_DONE);
        ButtonType completeMoveButtonType = new ButtonType("Complete Move", ButtonBar.ButtonData.OK_DONE);
        if(completeMove){
            dialog.getDialogPane().getButtonTypes().addAll(completeMoveButtonType, moveButtonType, deleteButtonType, ButtonType.CANCEL);
        } else {
            dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, moveButtonType, deleteButtonType, ButtonType.CANCEL);
        }

        // Grid Pane for setting up the layout for the dialog box.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // text fields, prompt text (so the user knows what to input and load any old inputted information.
        TextField firstName = new TextField();
        setTextFieldInformation(firstName, "First Name", fName);

        TextField lastName = new TextField();
        setTextFieldInformation(lastName, "Last Name", lName);

        TextField food = new TextField();
        setTextFieldInformation(food, "Food Diet", foodDiet);

        TextField liquid = new TextField();
        setTextFieldInformation(liquid, "Liquid Diet", liquidDiet);

        TextField notes = new TextField();
        setTextFieldInformation(notes, "Other Notes and restrictions", otherNotes);

        // Add labels and text fields to the grid
        grid.add(new Label("First Name:"), 0, 0);
        grid.add(firstName, 1, 0);
        grid.add(new Label("Last Name:"), 0, 1);
        grid.add(lastName, 1, 1);
        grid.add(new Label("Diet Restrictions:"), 0, 2);
        grid.add(food, 1, 2);
        grid.add(liquid, 2, 2);
        grid.add(new Label("Other Notes:"), 0, 3);
        grid.add(notes, 1, 3);

        // add the Grid Pane to the dialog box
        dialog.getDialogPane().setContent(grid);

        // check any buttons pressed on the dialog box
        dialog.setResultConverter(dialogButton -> {
            // update the room information
            if (dialogButton == submitButtonType) {
                submitPressed(firstName.getText(), lastName.getText(), food.getText(), liquid.getText(), notes.getText(), roomID);
            }
            // delete room information
            else if (dialogButton == deleteButtonType){
                deleteAlertBox(roomID);
            }
            // if move is pressed, backup all old information
            else if (dialogButton == moveButtonType) {
                moveOptionClicked(fName, lName, foodDiet, liquidDiet, otherNotes, roomID, occupied);
            }
            // complete the room move/transfer (not sure which word to use)
            else if (dialogButton == completeMoveButtonType){
                completeMove(fName, lName, foodDiet, liquidDiet, otherNotes, roomID, occupied);
            }
            // Reset the table view so it will load the new data.
            roomTable.getItems().clear();
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
     * @param firstName = first name
     * @param lastName = last name
     * @param food = food diet/restriction
     * @param liquid = liquid diet/restriction
     * @param notes = other notes
     * @param roomID = roomID (the only one that is the original value.
     */
    private void submitPressed(String firstName, String lastName, String food, String liquid, String notes, String roomID){
        String mysqlQuery = "UPDATE " + "room" + " SET occupied = ?, fName = ?, lName = ?, foodDiet = ?, liquidDiet = ?, otherNotes = ? WHERE roomID = ?";
        DatabaseQueries query = new DatabaseQueries();
        query.updateRoomValues(mysqlQuery, 1, firstName, lastName, food, liquid, notes, roomID);
    }

    /**
     * The only information needed for delete room information is the room ID.
     * @param roomID = the Room ID
     */
    private void deleteAlertBox(String roomID){
        // make an alert to confirm if the user wants to actually delete the information
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Room Information");
        alert.setHeaderText("Delete Room information");
        alert.setContentText("Do you want to delete room information? This cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            // update to delete all information and reset the occupied status to 0.
            String mysqlQuery = "UPDATE " + "room" + " SET occupied = ?, fName = ?, lName = ?, foodDiet = ?, liquidDiet = ?, otherNotes = ? WHERE roomID = ?";
            DatabaseQueries query = new DatabaseQueries();
            query.updateRoomValues(mysqlQuery, 0, "", "", "", "", "", roomID);
        } else {
            // do nothing
        }
    }

    /**
     *
     * @param fName = first name
     * @param lName = last name
     * @param foodDiet = food diet/restriction
     * @param liquidDiet = liquid diet/restriction
     * @param otherNotes = any other notes needed.
     * @param roomID = room ID
     * @param occupied = occupied status
     */
    private void moveOptionClicked(String fName, String lName, String foodDiet, String liquidDiet, String otherNotes, String roomID, String occupied){
        movefName = fName;
        movelName = lName;
        moveFoodDiet = foodDiet;
        moveLiquidDiet = liquidDiet;
        moveOtherNotes = otherNotes;
        moveRoomNum = roomID;
        // check if the room is occupied
        if (occupied.equals("Yes")){
            MoveOccupied = 1;
        } else {
            MoveOccupied = 0;
        }
        completeMove = true;
    }

    /**
     *
     * @param fName = first name
     * @param lName = last name
     * @param foodDiet = food diet/restriction
     * @param liquidDiet = liquid diet/restriction
     * @param otherNotes = other notes needed
     * @param roomID = room ID
     * @param occupied = occupied status
     */
    private void completeMove(String fName, String lName, String foodDiet, String liquidDiet, String otherNotes, String roomID, String occupied){
        String mysqlQuery = "UPDATE " + "room" + " SET occupied = ?, fName = ?, lName = ?, foodDiet = ?, liquidDiet = ?, otherNotes = ? WHERE roomID = ?";
        DatabaseQueries query = new DatabaseQueries();
        // int newOccupied doesn't make much sense with the name. But it's the occupied states for the room that the old room information is being moved to.
        int newOccupied;
        // The only input that needs to be checked is occupied. It's important to know if occupied needs to be updated.
        if(occupied.equals("Yes")){
            newOccupied = 1;
        } else {
            newOccupied = 0;
        }
        // send query twice to switch rooms.
        query.updateRoomValues(mysqlQuery, newOccupied, fName, lName, foodDiet, liquidDiet, otherNotes, moveRoomNum);
        query.updateRoomValues(mysqlQuery, MoveOccupied, movefName, movelName, moveFoodDiet, moveLiquidDiet, moveOtherNotes, roomID);
        completeMove = false;
    }
}

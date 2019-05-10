package sample.settings;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import sample.mysqlQueries.CreateDatabaseTables;
import sample.mysqlQueries.DatabaseQueries;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class CreateHallways implements Initializable {
    @FXML
    Label tvError;
    @FXML
    private TableView<HallModelTable> hallTable;
    @FXML
    private TableColumn<HallModelTable, String> colHallID, colHallName;
    private ObservableList<HallModelTable> obListHallway = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadTables();
    }

    /**
     * loadTables is called at during initialize and is also called whenever the dialog box is opened.
     */
    private void loadTables(){
        connectToDatabase();
        setupTables(colHallID, colHallName, hallTable, obListHallway);
    }

    private void connectToDatabase(){
        DatabaseQueries query = new DatabaseQueries();
        try {
            // Send query to database. This asks for the hallway name linked to the hallwayID found in roomID.
            ResultSet rs = query.queryToDatabase("SELECT * FROM hallway;");
            while(rs.next()){
                obListHallway.add(new HallModelTable(rs.getString("hallID"), rs.getString("hallName")));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param hallID = the room number.
     * @param hallName = The hallway name.
     * @param table = the GUI table.
     * @param obList = the Observable List.
     */
    private void setupTables(TableColumn hallID, TableColumn hallName, TableView table, ObservableList obList){

        // Set up the tables
        setupTable(hallID, "hallID");
        setupTable(hallName, "hallName");

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
        if (hallTable.getSelectionModel().getSelectedItem() != null) {
            HallModelTable selectedHall = hallTable.getSelectionModel().getSelectedItem();
            dialogEditHallway(selectedHall.getHallID(), selectedHall.getHallName());
            // clear the selection of the table (technically not needed since this is only one table.
            hallTable.getSelectionModel().clearSelection();
        }
    }

    private void dialogEditHallway(String hallID, String hallName){
        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Update Room");
        dialog.setHeaderText("To update the room information just fill out the text boxes!");

        // Set the button types.
        ButtonType submitButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);

        // Grid Pane for setting up the layout for the dialog box.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // text fields, prompt text (so the user knows what to input and load any old inputted information.
        TextField hallwayName = new TextField(hallName);

        // Add labels and text fields to the grid
        grid.add(new Label("Hallway Name:"), 0, 0);
        grid.add(hallwayName, 1, 0);

        // add the Grid Pane to the dialog box
        dialog.getDialogPane().setContent(grid);

        // check any buttons pressed on the dialog box
        dialog.setResultConverter(dialogButton -> {
            // update the room information
            if (dialogButton == submitButtonType) {
                String mysqlQuery = "UPDATE " + "hallway " + "SET hallName = ? WHERE hallID = ?";
                    tvError.setText("Database Updated");
                    tvError.setTextFill(Color.BLACK);
                    DatabaseQueries query = new DatabaseQueries();
                query.updateHallName(mysqlQuery, hallwayName.getText(), hallID);
            }
            // Reset the table view so it will load the new data.
            hallTable.getItems().clear();
            loadTables();
            return null;
        });
        // showAndWait() is the last method to be called, because it displays the dialog box.
        dialog.showAndWait();
    }

    //////////////////////////////////////////////////////////////////////////////////// SUBMIT CLICKED ///////////////////////////////////////////////////////////////
    /**
     * When submit is clicked.
     */
    public void submitClicked() {
        dialogSubmit();
    }
    /**
     *
     */
    private void dialogSubmit(){
        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Update Room");
        dialog.setHeaderText("To update the room information just fill out the text boxes!");

        // Set the button types.
        ButtonType submitButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);

        // Grid Pane for setting up the layout for the dialog box.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // text fields, prompt text (so the user knows what to input and load any old inputted information.
        TextField hallName = new TextField();
        TextField lowNumber = new TextField();
        TextField highNumber = new TextField();

        // Add labels and text fields to the grid
        grid.add(new Label("Hallway Name:"), 0, 0);
        grid.add(hallName, 1, 0);
        grid.add(new Label("Lowest Room Number:"), 0, 1);
        grid.add(lowNumber, 1, 1);
        grid.add(new Label("Highest Room Number:"), 0, 2);
        grid.add(highNumber, 1, 2);

        // add the Grid Pane to the dialog box
        dialog.getDialogPane().setContent(grid);

        // check any buttons pressed on the dialog box
        dialog.setResultConverter(dialogButton -> {
            // update the room information
            if (dialogButton == submitButtonType) {
                boolean inputError = checkInputs(hallName, lowNumber, highNumber);
                System.out.println("inputError = " + inputError);
                if(!inputError){
                    tvError.setText("Database Updated");
                    tvError.setTextFill(Color.BLACK);
                    CreateDatabaseTables create = new CreateDatabaseTables();
                    create.hallwayIfVisible(hallName.getText(), lowNumber.getText(), highNumber.getText());
                }
            }
            // Reset the table view so it will load the new data.
            hallTable.getItems().clear();
            loadTables();
            return null;
        });
        // showAndWait() is the last method to be called, because it displays the dialog box.
        dialog.showAndWait();
    }

    private boolean checkInputs(TextField etHall, TextField hallLowNum, TextField hallHighNum){
        if("".equals(etHall.getText())){
            tvError.setTextFill(Color.web("#FF0000"));
            tvError.setText("Name of hallway is blank");
            return true;
        }
        try {
            int roomLow = Integer.parseInt(hallLowNum.getText());
            int roomHigh = Integer.parseInt(hallHighNum.getText());
            boolean ifLess = roomLow < roomHigh;
            if(!ifLess){
                tvError.setTextFill(Color.web("#FF0000"));
                tvError.setText("low number in hallway is greater then or equal to the highest number");
                return true;
            }
        } catch (NumberFormatException e) {
            tvError.setTextFill(Color.web("#FF0000"));
            tvError.setText("Error: Room number input does not contain numbers");
            return true;
        }
        return false;
    }
}
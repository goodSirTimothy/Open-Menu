package sample.settings;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import sample.mysqlQueries.CreateDatabaseTables;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class CreateHallways implements Initializable {
    private ObservableList<Integer> list = FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7);
    @FXML
    public ChoiceBox<Integer> hallDropBox = new ChoiceBox<>();
    // Name text fields
    @FXML
    TextField etHall1, etHall2, etHall3, etHall4, etHall5, etHall6, etHall7;
    // low room number text fields
    @FXML
    TextField hall1LowNum, hall2LowNum, hall3LowNum, hall4LowNum, hall5LowNum, hall6LowNum, hall7LowNum;
    // high room number text fields
    @FXML
    TextField hall1HighNum, hall2HighNum, hall3HighNum, hall4HighNum, hall5HighNum, hall6HighNum, hall7HighNum;
    // The HBox's are for hiding the hallways.
    @FXML
    HBox hall1, hall2, hall3, hall4, hall5, hall6, hall7;
    @FXML
    Label tvError;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hideHallways();
        hallDropBox.setValue(1);
        hallDropBox.setItems(list);
        forLoopForShowing(hallDropBox.getValue());
    }

    public void dropBox(){
        hideHallways();
        forLoopForShowing(hallDropBox.getValue());
    }

    private void hideHallways(){
        hall1.setVisible(false);
        hall2.setVisible(false);
        hall3.setVisible(false);
        hall4.setVisible(false);
        hall5.setVisible(false);
        hall6.setVisible(false);
        hall7.setVisible(false);
    }

    /**
     * This loop goes threw all of the HBoxes and sets the ones needed to visible.
     * @param value = number of boxes that should be on screen
     */
    private void forLoopForShowing(int value){
        for(int i = 0; i < value; i++) {
            if(i == 0)
                hall1.setVisible(true);
            if(i == 1)
                hall2.setVisible(true);
            if(i == 2)
                hall3.setVisible(true);
            if(i == 3)
                hall4.setVisible(true);
            if(i == 4)
                hall5.setVisible(true);
            if(i == 5)
                hall6.setVisible(true);
            if(i == 6)
                hall7.setVisible(true);
        }
    }
    //////////////////////////////////////////////////////////////////////////////////// SUBMIT CLICKED ///////////////////////////////////////////////////////////////
    /**
     * When submit is clicked.
     */
    public void submitClicked() {
        dialogAdminLogin(0);
    }

    /**
     *
     */
    private void dialogAdminLogin(int optionControl){
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
                CreateDatabaseTables create = new CreateDatabaseTables();
                if(optionControl == 1){
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Reset Database?");
                    alert.setHeaderText("Are you sure you want to reset the database?");
                    alert.setContentText("All information will be removed.");

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        create.resetAlertOk(rootUser.getText(), rootPass.getText());
                    }
                } else if (optionControl == 2){
                	
                }
            }
            return null;
        });
        // showAndWait() is the last method to be called, because it displays the dialog box.
        dialog.showAndWait();
    }

    /**
     *
     */
    private void populateTables(String rUser, String rPass){
        boolean inputError = false;
        // Make checks to see if there are any bad inputs. If the hallway is visible.
        if(hall1.isVisible())
            inputError = checkInputs(inputError, etHall1, hall1LowNum, hall1HighNum, 1);
        if(hall2.isVisible())
            inputError = checkInputs(inputError, etHall2, hall2LowNum, hall2HighNum, 2);
        if(hall3.isVisible())
            inputError = checkInputs(inputError, etHall3, hall3LowNum, hall3HighNum, 3);
        if(hall4.isVisible())
            inputError = checkInputs(inputError, etHall4, hall4LowNum, hall4HighNum, 4);
        if(hall5.isVisible())
            inputError = checkInputs(inputError, etHall5, hall5LowNum, hall5HighNum, 5);
        if(hall6.isVisible())
            inputError = checkInputs(inputError, etHall6, hall6LowNum, hall6HighNum, 6);
        if(hall7.isVisible())
            inputError = checkInputs(inputError, etHall7, hall7LowNum, hall7HighNum, 7);
        System.out.println("inputError = " + inputError);
        if (!inputError) {
            CreateDatabaseTables create = new CreateDatabaseTables();
            create.hallwayIfVisible(hall1, etHall1, hall1LowNum, hall1HighNum, rUser, rPass);
            create.hallwayIfVisible(hall2, etHall2, hall2LowNum, hall2HighNum, rUser, rPass);
            create.hallwayIfVisible(hall3, etHall3, hall3LowNum, hall3HighNum, rUser, rPass);
            create.hallwayIfVisible(hall4, etHall4, hall4LowNum, hall4HighNum, rUser, rPass);
            create.hallwayIfVisible(hall5, etHall5, hall5LowNum, hall5HighNum, rUser, rPass);
            create.hallwayIfVisible(hall6, etHall6, hall6LowNum, hall6HighNum, rUser, rPass);
            create.hallwayIfVisible(hall7, etHall7, hall7LowNum, hall7HighNum, rUser, rPass);
            tvError.setText("Database Updated");
            tvError.setTextFill(Color.BLACK);
        }
    }

    private boolean checkInputs(boolean inputError, TextField etHall, TextField hallLowNum, TextField hallHighNum, int hallNumber){
        if(inputError){
            return true;
        }
        if("".equals(etHall.getText())){
            tvError.setTextFill(Color.web("#FF0000"));
            tvError.setText("Name of hallway " + hallNumber + ", is blank");
            return true;
        }
        try {
            int roomLow = Integer.parseInt(hallLowNum.getText());
            int roomHigh = Integer.parseInt(hallHighNum.getText());
            boolean ifLess = roomLow < roomHigh;
            if(!ifLess){
                tvError.setTextFill(Color.web("#FF0000"));
                tvError.setText("low number in hallway " + hallNumber + ", is greater then or equal to the highest number");
                return true;
            }
        } catch (NumberFormatException e) {
            tvError.setTextFill(Color.web("#FF0000"));
            tvError.setText("low number in hallway " + hallNumber + ", is greater then the highest number");
            return true;
        }
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////////////// RESET CLICKED ///////////////////////////////////////////////////////////////
    /**
     * If reset is clicked, check with the user if they meant to click it. If so, delete the tables and recreate the tables.
     * The reason why the program deletes the tables, because of foreign keys, the TRUNCATE command will not delete the values in hallway.
     * So the way to solve this problem is to just delete the the tables (in proper order) and then recreate the tables.
     */
    public void resetClicked(){
        dialogAdminLogin(1);
    }
}
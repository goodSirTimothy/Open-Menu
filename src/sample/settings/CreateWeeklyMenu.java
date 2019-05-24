package sample.settings;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import sample.mysqlQueries.DatabaseCreateMenuTables;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

public class CreateWeeklyMenu implements Initializable {
    @FXML
    TextField etNumOfWeeks;
    @FXML
    Label error;

    private String rUser, rPass;

    public void submitClicked(){
        int num = 0;
        try{
            num = Integer.parseInt(etNumOfWeeks.getText());
        } catch (NumberFormatException e){
            error.setText("Error: There was a character or letter found instead of just numbers...");
        }
        if(num > 0) {
            boolean login = dialogAdminLogin();
            if(login){
                DatabaseCreateMenuTables query = new DatabaseCreateMenuTables();
                query.mysqlTableCreate(rUser, rPass, "bfastMenu", num);
                query.mysqlTableCreate(rUser, rPass, "lunchMenu", num);
                query.mysqlTableCreate(rUser, rPass, "supperMenu", num);
            }
        } else {
            error.setText("Number input is 0 or less");
        }
    }

    private boolean dialogAdminLogin(){
        AtomicBoolean login = new AtomicBoolean(false);
        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Root User Login");
        dialog.setHeaderText("Login as the root user to add the weekly menu.");

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
            // submit contunies with the
            if (dialogButton == submitButtonType) {
                rUser = rootUser.getText();
                rPass = rootPass.getText();
                login.set(true);
            }
            return null;
        });
        // showAndWait() is the last method to be called, because it displays the dialog box.
        dialog.showAndWait();
        return login.get();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}

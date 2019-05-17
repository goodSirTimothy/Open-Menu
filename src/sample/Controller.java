package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import sample.mysqlQueries.DatabaseQueries;


import java.io.IOException;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    public BorderPane borderpane;

    @FXML
    private TreeView<String> navigation;

    /**
     * initialize sets up the tree values from the start. 
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTreeView();
    }

    public void pingClicked(){
        String connectionStatus = "No Connection.";
        DatabaseQueries query = new DatabaseQueries();
        int i = 0;
        while(i<3 && connectionStatus.equals("No Connection.")) {
            try {
                DriverManager.getConnection(query.getURL() + ":" + query.getPort(), query.getUser(), query.getPass());
                connectionStatus = "Connected.";
            } catch (SQLException e) {
                connectionStatus = "No Connection.";
                e.printStackTrace();
            }
            i++;
        }
        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Ping");
        dialog.setHeaderText("Pinging Database");

        // Set the button types.
        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(closeButton);

        // Grid Pane for setting up the layout for the dialog box.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Add labels and text fields to the grid
        grid.add(new Label("Database Connection:"), 0, 0);
        grid.add(new Label(connectionStatus), 1, 0);

        // add the Grid Pane to the dialog box
        dialog.getDialogPane().setContent(grid);

        // check any buttons pressed on the dialog box
        dialog.setResultConverter(dialogButton -> {
            // update the room information
            return null;
        });
        // showAndWait() is the last method to be called, because it displays the dialog box.
        dialog.showAndWait();
    }

    public void setupClicked(){
        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Ping");
        dialog.setHeaderText("Pinging Database");

        // Set the button types.
        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(closeButton);

        // Grid Pane for setting up the layout for the dialog box.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Add labels and text fields to the grid
        grid.add(new Label("Setup Database:"), 0, 0);
        grid.add(new Label("This is where you will setup the database IP, database name, port,\nand create the user for this app to connect to the database with."), 1, 0);
        grid.add(new Label("Create Hallway:"), 0, 1);
        grid.add(new Label("You can add as many hallways as needed to the database.\nSimply click \"Add Hallway\" to add a hallway to the database.\nDouble click a hallway on the table to rename it."), 1, 1);
        grid.add(new Label("Create Weekly Menu:"), 0, 2);
        grid.add(new Label("Enter in how many weeks you would like for the weekly menu.\nClick submit and you will be prompted to input information for each meal."), 1, 2);

        // add the Grid Pane to the dialog box
        dialog.getDialogPane().setContent(grid);

        // check any buttons pressed on the dialog box
        dialog.setResultConverter(dialogButton -> {
            // update the room information
            return null;
        });
        // showAndWait() is the last method to be called, because it displays the dialog box.
        dialog.showAndWait();
    }


    /////////////////////////////////////////////////////////////////////////// TREE VIEW SETUP AND CLICK LOGIC ////////
    private void setupTreeView(){
        TreeItem<String> root, about, settings, editValues, editMenuSpecials;
        root = new TreeItem<>();
        root.setExpanded(true);

        editValues = makeBranch("CHANGE INFORMATION", root);
        makeBranch("Edit Weekly Menu", editValues);
        makeBranch("Edit Room", editValues);

        editMenuSpecials = makeBranch("MENU SPECIALS", root);
        makeBranch("Edit Breakfast Menu", editMenuSpecials);
        makeBranch("Edit Daily Specials", editMenuSpecials);
        makeBranch("Edit Weekly Specials", editMenuSpecials);
        makeBranch("Edit Monthly Specials", editMenuSpecials);

        about = makeBranch("ABOUT", root);

        settings = makeBranch("SETTINGS", root);
        makeBranch("Setup Database", settings);
        makeBranch("Create Hallway", settings);
        makeBranch("Create Weekly Menu", settings);

        // because settings is just for set up, setExpanded = false
        settings.setExpanded(false);

        navigation.setRoot(root);
        navigation.setShowRoot(false);
        navigation.getSelectionModel().selectedItemProperty() .addListener((v, oldValue, newValue) -> {
            treeClick(newValue.toString());
        });
    }

    /**
     * Makes the branches of the tree.
     * @param title = the name of the branch.
     * @param parent = the parent.
     * @return the item of the tree.
     */
    private TreeItem<String> makeBranch(String title, TreeItem<String> parent){
        TreeItem<String> item = new TreeItem<>(title);
        item.setExpanded(true);
        parent.getChildren().add(item);
        return item;
    }

    /**
     * Check TreeItem values for navigation.
     * @param value = TreeItem [ value: ___ ]
     */
    private void treeClick(String value){
        if(value!=null){
            switch (value) {
                case "TreeItem [ value: Edit Weekly Menu ]":
                    loadUI("editValues/editMenuValues");
                    System.out.println(value);
                    break;
                case "TreeItem [ value: Edit Room ]":
                    loadUI("editValues/editRoomValues");
                    System.out.println(value);
                    break;

                case "TreeItem [ value: Edit Breakfast Menu ]":
                    loadUI("menuSpecials/editBreakfast");
                    System.out.println(value);
                    break;
                case "TreeItem [ value: Edit Daily Specials ]":
                    loadUI("menuSpecials/editDailySpecial");
                    System.out.println(value);
                    break;
                case "TreeItem [ value: Edit Weekly Specials ]":
                    loadUI("menuSpecials/editWeeklySpecial");
                    System.out.println(value);
                    break;
                case "TreeItem [ value: Edit Monthly Specials ]":
                    loadUI("menuSpecials/editMonthlySpecial");
                    System.out.println(value);
                    break;

                //
                case "TreeItem [ value: ABOUT ]":
                    loadUI("about/about");
                    System.out.println(value);
                    break;

                // Case for setting up the database
                case "TreeItem [ value: Setup Database ]":
                    loadUI("settings/settingsDatabase");
                    System.out.println(value);
                    break;
                case "TreeItem [ value: Create Hallway ]":
                    loadUI("settings/createHallways");
                    System.out.println(value);
                    break;
                case "TreeItem [ value: Create Weekly Menu ]":
                    loadUI("settings/createWeeklyMenu");
                    System.out.println(value);
                    break;
                default:
                    System.out.println(value);
                    break;
            }
        }
    }

    /**
     * Moves Application back to the main page.
     * @param event = is for allowing the FXMLLoader to properly execute.
     * @throws IOException These are for the FXMLLoader.
     */
    public void homeClicked(MouseEvent event) throws IOException {
        Parent tableViewParent = FXMLLoader.load(getClass().getResource("sample" + ".fxml"));
        Scene tableViewScene = new Scene(tableViewParent, 800, 500);
        //This line gets the Stage information
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(tableViewScene);
        window.show();
        System.out.println("settings Clicked");
    }

    /**
     * These 2 functions are for changing the view on click.
     */
    public void settingsClicked() {
        loadUI("settings");
    }

    // Reference for how to use setCenter() https://www.youtube.com/watch?v=ttD35jEo-f0&ab_channel=RashidCoder
    private void loadUI (String set) {
        Parent loadView;
        try {
            loadView = FXMLLoader.load(getClass().getResource(set + ".fxml"));
            borderpane.setCenter(loadView);
        } catch (IOException e) {
            System.out.println("An error has happened: " + e);
            e.printStackTrace();
        }

    }
}

package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
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
        TreeItem<String> root, about, settings, editValues, editMenuSpecials;
        root = new TreeItem<>();
        root.setExpanded(true);

        editValues = makeBranch("CHANGE INFORMATION", root);
        makeBranch("Edit Weekly Menu", editValues);
        makeBranch("Edit Room", editValues);

        editMenuSpecials = makeBranch("MENU SPECIALS", root);
        makeBranch("Edit Monthly Specials", editMenuSpecials);
        makeBranch("Edit Weekly Specials", editMenuSpecials);
        makeBranch("Edit Daily Specials", editMenuSpecials);
        makeBranch("Edit Breakfast Menu", editMenuSpecials);

        about = makeBranch("ABOUT", root);

        settings = makeBranch("SETTINGS", root);
        makeBranch("Setup Database", settings);
        makeBranch("Create Database", settings);
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

                case "TreeItem [ value: Edit Monthly Specials ]":
                    loadUI("menuSpecials/editMonthlySpecial");
                    System.out.println(value);
                    break;
                case "TreeItem [ value: Edit Weekly Specials ]":
                    loadUI("menuSpecials/editWeeklySpecial");
                    System.out.println(value);
                    break;
                case "TreeItem [ value: Edit Daily Specials ]":
                    loadUI("menuSpecials/editDailySpecial");
                    System.out.println(value);
                    break;
                case "TreeItem [ value: Edit Breakfast Menu ]":
                    loadUI("menuSpecials/editBreakfast");
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
                case "TreeItem [ value: Create Database ]":
                    loadUI("settings/createHallways");
                    System.out.println(value);
                    break;
                case "TreeItem [ value: Create Weekly Menu ]":
                    loadUI("settings/CreateWeeklyMenu");
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

package sample.about;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class About implements Initializable {

    @FXML
    Label aboutSoftware, aboutApplication, warnings;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        aboutApplication.setText("This is the admin application for the Open Menu software. With this app, you can update rooms and hallways. " +
                "If needed you can include diet restrictions, you can add a weekly feature menu. You can also add Monthly, Weekly, and Daily specials which " +
                "are different from the feature menu." +
                "\n\nAll is stored in a database to simplify the application and to make it easier for other devices to access the information.");
        aboutSoftware.setText("This software requires access to a MySQL database." +
                "\nYou can input the server address, username, password, and port, and the application will handle the queries to create the database.");
        warnings.setText("Warning, this application in it's most basic form does not encrypt any MySQL information and creates a local .txt file." +
                "\nIf you want to use this application for professional reasons; please ensure you add some sort of encryption for this application when saving files.");
    }
}

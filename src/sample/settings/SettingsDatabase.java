package sample.settings;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import sample.mysqlQueries.CreateDatabaseTables;
import sample.mysqlQueries.DatabaseQueries;

import java.io.*;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SettingsDatabase implements Initializable {
    private String fileName = "server information.txt";
    @FXML
    TextField dbName, serverURL, username, portNum;
    @FXML
    PasswordField password;
    @FXML
    Label dbStatus;

    private String rUser, rPass;

    // Reference for JDBC: https://stackoverflow.com/questions/5809239/query-a-mysql-db-using-java
    // Reference for finding bind-address: https://stackoverflow.com/questions/50449665/raspberry-pi-mysql-remote-connection-error-10061
    public void submitClicked() {
        dialogAdminLogin();
        createDatabaseAndUser();
    }

    private void checkConnectionAndWriteFiles(String rootUser, String rootPass){
        try {
            String server = serverURL.getText();
            // Connection conn = DriverManager.getConnection("jdbc:mysql://" + server, user, pass) ;
            DriverManager.getConnection("jdbc:mysql://" + server, rootUser, rootPass) ;
            dbStatus.setText("Connected");
            PrintWriter writer = new PrintWriter(fileName, "UTF-8");
            writer.println(serverURL.getText());
            writer.println(username.getText());
            writer.println(password.getText());
            writer.println(portNum.getText());
            writer.close();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Server Connection Error");
            alert.setHeaderText("Did you type something in wrong?");
            alert.setContentText("Check the your inputs for any errors...\n\nError: " + e);
            alert.showAndWait();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("File Not Found");
            alert.setHeaderText("File not found");
            alert.setContentText("Sorry we could not find the file.\n\nError: " + e);
            alert.showAndWait();
        } catch (UnsupportedEncodingException e) {
            dbStatus.setText("Error: " + e);
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERROR");
            alert.setHeaderText("Uh-Oh.");
            alert.setContentText("Please contact the installer about Error:\n" + e);
            alert.showAndWait();
        }
    }

    private void createDatabaseAndUser(){
        // if dbName isn't empty, set up the database.
        if(!"".equals(dbName.getText())) {
            DatabaseQueries query = new DatabaseQueries();
            // remove all spaces so that the database can be created without an error. Example, database name cannot have a space like "test DB" it must be "testDB"
            String db = dbName.getText().replaceAll("\\s+","");
            // Creates the database on the MySQL server.
            query.createDB("CREATE DATABASE IF NOT EXISTS " + db, rUser, rPass);
            // save the file
            saveFile(db);
            // create the user. query.getRawURL()
            query.createUser("CREATE USER IF NOT EXISTS ?@'%';", rUser, rPass, username.getText());
            query.createUserSetPassword("SET PASSWORD FOR ?@'%' = ?;", rUser, rPass, username.getText(), password.getText());
            query.grantPermissions("GRANT SELECT, UPDATE, INSERT ON " + db + ".* TO ?@'%'", rUser, rPass, username.getText());
            // Query to grant permissions on all databases: "GRANT SELECT, UPDATE ON *.* TO ?@'%'", rUser, rPass, username.getText()
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Database Empty");
            alert.setHeaderText(null);
            alert.setContentText("You did not input a database name.\nPlease input the database name");
            alert.showAndWait();
        }
    }

    private void dialogAdminLogin(){
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
                checkConnectionAndWriteFiles(rootUser.getText(), rootPass.getText());
                // create the tables in the database.
                CreateDatabaseTables create = new CreateDatabaseTables();
                create.createTables(rootUser.getText(), rootPass.getText());
                rUser = rootUser.getText();
                rPass = rootPass.getText();
            }
            return null;
        });
        // showAndWait() is the last method to be called, because it displays the dialog box.
        dialog.showAndWait();
    }

    /**
     * Save the file
     * @param db = the name of the database to be saved.
     */
    private void saveFile(String db){
        try {
            PrintWriter writer = new PrintWriter("db name.txt", "UTF-8");
            writer.println(db);
            writer.close();
            System.out.println("File data written.");
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e);
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            System.out.println("Error: " + e);
            e.printStackTrace();
        }
    }

    ///////////////////////////////////////////////////////////// INITIALIZE ///////////////////////////////////////////
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DatabaseQueries query = new DatabaseQueries();
        serverURL.setText(query.getRawURL());
        username.setText(query.getUser());
        dbName.setText(query.getDbName());

        if(query.getRawURL().equals("")){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("First Setup");
            alert.setHeaderText("Please Note");
            alert.setContentText("Do not enter the root user into the input fields.\n" +
                    "For anything needing root user permissions you will be prompted for an input.");
            alert.showAndWait();
        } else {
            try {
                DriverManager.getConnection(query.getURL() + ":" + query.getPort(), query.getUser(), query.getPass());
                dbStatus.setText("Connected...");
            } catch (SQLException e) {
                dbStatus.setText("No Connection...");
                e.printStackTrace();
            }
        }
    }
}

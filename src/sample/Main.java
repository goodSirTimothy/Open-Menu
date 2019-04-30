package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;

public class Main extends Application {
    public static void main(String[] args) {
        String fileName1 = "server information.txt";
        // Create the server information file at the start of the program.
        File file = new File(fileName1);
        if (!file.exists()){
            try {
                PrintWriter writer = new PrintWriter(fileName1, "UTF-8");
                // write the default IP for the Raspberry Pi
                writer.println("192.168.0.120");
                writer.close();
            } catch (FileNotFoundException e) {
                System.out.println("File not found: " + e);
                e.printStackTrace();
            }
            catch (UnsupportedEncodingException e) {
                System.out.println("Something else has happened" + e);
                e.printStackTrace();
            }
        }
        // Start the GUI.
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Open Menu Admin Application");
        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.show();
    }

    public void onClick(ActionEvent event, int i, String set) throws IOException {
        if (i==1){
            Parent tableViewParent = FXMLLoader.load(getClass().getResource(set + ".fxml"));
            Scene tableViewScene = new Scene(tableViewParent, 800, 500);
            //This line gets the Stage information
            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            window.setScene(tableViewScene);
            window.show();
            System.out.println("settings Clicked");
        }
    }
}

/*
                                                     References
                Writing, Reading, and checking files:
    Writing/creating text file:     https://stackoverflow.com/questions/2885173/how-do-i-create-a-file-and-write-to-it-in-java
    reading from text file:         https://www.geeksforgeeks.org/different-ways-reading-text-file-java/
    If the file exists:             https://alvinalexander.com/java/java-file-exists-directory-exists

                MySQL statement references:
    MySQL, CREATE ____ example:     http://www.sqlitetutorial.net/sqlite-java/create-table/
    MySQL, query _____ example:     https://docs.oracle.com/javase/tutorial/jdbc/basics/processingsqlstatements.html
    MySQL, Insert values:           https://www.tutorialspoint.com/jdbc/jdbc-insert-records.htm
    MySQL, Prepared Statements:     https://docs.oracle.com/javase/tutorial/jdbc/basics/prepared.html
    MySQL, prevent injection:       http://bobby-tables.com/java
    MySQL, check if table exists:   https://stackoverflow.com/questions/2942788/check-if-table-exists
    MySQL, TRUNCATE Table:          https://stackoverflow.com/questions/5452760/how-to-truncate-a-foreign-key-constrained-table/8074510#8074510
    MySQL, Delete Table:            http://www.mysqltutorial.org/mysql-drop-table

    MySQL, Primary Keys:            https://www.w3schools.com/sql/sql_primarykey.asp
           Foreign Keys:            https://www.w3schools.com/sql/sql_foreignkey.asp
           How to set up boolean:   https://stackoverflow.com/questions/289727/which-mysql-data-type-to-use-for-storing-boolean-values

    MySQL, setup remote user:       https://stackoverflow.com/questions/16287559/mysql-adding-user-for-remote-access

                Logic References:
    Logic for Not.equal():          https://stackoverflow.com/questions/8484668/java-does-not-equal-not-working
    Alert Box:                      https://stackoverflow.com/questions/44101426/javafx-alert-box-on-button-click
    All Types of Alert Boxes:       https://code.makery.ch/blog/javafx-dialogs-official/
 */
package sample;

import javafx.event.ActionEvent;

import java.io.IOException;

public class Settings {
    Main main = new Main();

    public void backClicked(ActionEvent event){
        try {
            main.onClick(event,1, "sample");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

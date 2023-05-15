package view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class MainWindowController {

    @FXML
    private Button helpButton;

    @FXML
    private Button restartButton;

    @FXML
    private GridPane gameBoard;

    private TextField[][] slots;

    @FXML
    public void initialize() {
        slots = new TextField[15][15];
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                TextField tf = new TextField();
                tf.setPrefWidth(30);  // Set preferred width as per your requirement
                tf.setPrefHeight(30); // Set preferred height as per your requirement
                slots[i][j] = tf;
                gameBoard.add(tf, j, i);
            }
        }
    }

    @FXML
    public void showHelp() {
        // logic to display help here
    }

    @FXML
    public void restartGame() {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                slots[i][j].clear();
            }
        }
        // other logic for resetting the game here
    }
}

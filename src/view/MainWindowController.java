package view;

import java.util.Observable;
import java.util.Observer;

import java.util.Observable;
import java.util.Observer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ListView;
import javafx.scene.input.*;
import javafx.collections.FXCollections;
import javafx.scene.layout.GridPane;

import viewmodel.ViewModel;

public class MainWindowController implements Observer  {
    ViewModel vm;
    
    @FXML
    private Button helpButton;

    @FXML
    private Button restartButton;
    
    @FXML
    private ListView<String> letterList;
    
    @FXML
    private GridPane gameBoard;

//    @FXML
//    private TextField varX;
    @FXML
    private Label resLabel;

    @FXML
    private Label letterSelected;
    private TextField[][] slots;

    @FXML
    public void initialize() {
        slots = new TextField[15][15];
        
        // Let's say these are the letters available
        letterList.setItems(FXCollections.observableArrayList("A", "B", "C", "D", "E"));
        
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                TextField tf = new TextField();
                tf.setPrefWidth(30);  // Set preferred width as per your requirement
                tf.setPrefHeight(30); // Set preferred height as per your requirement
                slots[i][j] = tf;
                gameBoard.add(tf, j, i);

                // Allow dropping a letter onto the text field
                tf.setOnDragOver(event -> {
                    if (event.getGestureSource() != tf && event.getDragboard().hasString()) {
                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    }
                    event.consume();
                });
                
                tf.setOnDragDropped(event -> {
                    System.out.println("Dropped");
                    Dragboard db = event.getDragboard();
                    boolean success = false;
                    if (db.hasString()) {
                        tf.setText(db.getString());
                        success = true;
                    }
                    event.setDropCompleted(success);
                    event.consume();
                });
            }
        }
        
        // Allow dragging a letter from the list
        letterList.setOnDragDetected(event -> {
            String letter = letterList.getSelectionModel().getSelectedItem();
            System.out.println("Drag detected: " + letter);

            if (letter != null) {
                this.showLetterSelected(letter);
                Dragboard db = letterList.startDragAndDrop(TransferMode.ANY);
                ClipboardContent content = new ClipboardContent();
                content.putString(letter);
                db.setContent(content);
            }
            event.consume();
            gameBoard.requestLayout(); // Refresh the layout of the GridPane

        });


    }
    
    public void setViewModel(ViewModel vm) {
        this.vm = vm;
        resLabel.textProperty().bind(vm.res); // binds reslabel to the res string in the viewmodel
        letterSelected.textProperty().bind(vm.letter); // binds letterSelected to the letterSelected string in the viewmodel
        vm.addObserver(this);
    }



    @FXML
    public void showHelp() {
        // logic to display help here
        System.out.println("Help button pressed"); // just a check to see if the button works
        vm.applyString(); // activates the applyString function from the viewmodel
    }

    public void showLetterSelected(String letter) {
        vm.letterSelected(letter);
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

	
    @Override
    public void update(Observable o, Object arg) {
    	
	    }
}

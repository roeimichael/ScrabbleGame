package view;

import java.util.Observable;
import java.util.Observer;


import javafx.beans.property.IntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ListView;
import javafx.scene.input.*;
import javafx.collections.FXCollections;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;

import javafx.scene.paint.Color;
import viewmodel.ViewModel;

public class MainWindowController extends Observable implements Observer   {
    ViewModel vm;
    
    @FXML
    private Button helpButton;

    @FXML
    private Button restartButton;

    @FXML
    private Button confirmButton;

    @FXML
    private ListView<String> letterList;
    
    @FXML
    private GridPane gameBoard;

    @FXML
    private Label resLabel;

    @FXML
    private Label letterSelected;
    @FXML
    private Label confirmSelected;
    @FXML
    private Label wordAdded;
    @FXML
    private Label rowSelected;
    @FXML
    private Label colSelected;
    @FXML
    private Label wordDirection;

    private TextField[][] slots;

    private IntegerProperty[][] bonusData;
    private String clean="clean";

    public void setViewModel(ViewModel vm) {
        this.vm = vm;

        bonusData = new IntegerProperty[15][15]; // creates a new array of integers for the bonus
        bonusData = vm.getBonus_vm(); // gets the bonus array from the viewmodel
        showBonus(); // shows the bonus tiles on the board

        confirmSelected.textProperty().bind(vm.confirm); // binds confirm to the confirm string in the viewmodel
        resLabel.textProperty().bind(vm.res); // binds reslabel to the res string in the viewmodel
        letterSelected.textProperty().bind(vm.letter); // binds letterSelected to the letterSelected string in the viewmodel
        wordAdded.textProperty().bind(vm.wordSelected); // binds wordAdded to the wordSelected string in the viewmodel
        colSelected.textProperty().bind(vm.col); // binds indexSelected to the x string in the viewmodel
        rowSelected.textProperty().bind(vm.row); // binds indexSelected to the x string in the viewmodel
        wordDirection.textProperty().bind(vm.wordDirection); // binds indexSelected to the x string in the viewmodel
        vm.addObserver(this);
    }
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
                        // Set the background color of the text field to indicate that a letter has been dropped
                        this.showLetterSelected(tf.getText(), GridPane.getRowIndex(tf), GridPane.getColumnIndex(tf));
                        slots[GridPane.getRowIndex(tf)][GridPane.getColumnIndex(tf)].setBackground(new Background(new BackgroundFill(Color.LIGHTYELLOW, null, null)));
                    }
                    event.setDropCompleted(success);
                    event.consume();
                });
            }
        }
        gameBoard.setGridLinesVisible(true); // Add this line to make grid lines visible

        // Allow dragging a letter from the list
        letterList.setOnDragDetected(event -> {

            String letter = letterList.getSelectionModel().getSelectedItem();
            System.out.println("Drag detected: " + letter);

            if (letter != null) {
                //this.showLetterSelected(letter);
                Dragboard db = letterList.startDragAndDrop(TransferMode.ANY);
                ClipboardContent content = new ClipboardContent();
                content.putString(letter);
                db.setContent(content);
            }
            event.consume();
            gameBoard.requestLayout(); // Refresh the layout of the GridPane

        });


    }
    


    public void showBonus() // shows the bonus tiles on the board
    {
        for(int i=0; i<15; i++)
        {
            for(int j=0; j<15; j++)
            {
                if(bonusData[i][j].getValue() == 2)
                {
                    slots[i][j].setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, null, null)));
                }
                else if(bonusData[i][j].getValue() == 3)
                {
                    slots[i][j].setBackground(new Background(new BackgroundFill(Color.DARKBLUE, null, null)));
                }
                else if(bonusData[i][j].getValue() == 20)
                {
                    slots[i][j].setBackground(new Background(new BackgroundFill(Color.LIGHTPINK, null, null)));
                }
                else if(bonusData[i][j].getValue() == 30)
                {
                    slots[i][j].setBackground(new Background(new BackgroundFill(Color.DARKRED, null, null)));
                }
            }
        }
    }


    @FXML
    public void showHelp() {
        // logic to display help here
        System.out.println("Help button pressed"); // just a check to see if the button works
        vm.applyString(); // activates the applyString function from the viewmodel
    }
    @FXML
    public void showConfirm() {
        // logic to display random number here
        System.out.println("Confirm button pressed"); // just a check to see if the button works
        vm.confirmSelected(); // activates the applyString function from the viewmodel
    }

    public void showLetterSelected(String letter, int row, int col) {
        System.out.println("letter:"+ letter);
        System.out.println(" index: " + row + ", " + col);
        vm.letterSelected(letter, row, col);
    }

    @FXML
    public void restartGame() {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                slots[i][j].clear();
                if(bonusData[i][j].getValue() == 2)
                {
                    slots[i][j].setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, null, null)));
                }
                else if(bonusData[i][j].getValue() == 3)
                {
                    slots[i][j].setBackground(new Background(new BackgroundFill(Color.DARKBLUE, null, null)));
                }
                else if(bonusData[i][j].getValue() == 20)
                {
                    slots[i][j].setBackground(new Background(new BackgroundFill(Color.LIGHTPINK, null, null)));
                }
                else if(bonusData[i][j].getValue() == 30)
                {
                    slots[i][j].setBackground(new Background(new BackgroundFill(Color.DARKRED, null, null)));
                }
                else
                    slots[i][j].setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
            }
        }
        gameBoard.setGridLinesVisible(true); // Add this line to make grid lines visible
        // let the viewmodel and view know that the game is restarting
        setChanged();
        notifyObservers("restart");

        // other logic for resetting the game here
    }

	
    @Override
    public void update(Observable o, Object arg) {
    	
	    }
}

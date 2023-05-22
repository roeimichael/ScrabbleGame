package view;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import test.CharacterData;
import viewmodel.ViewModel;
import javafx.fxml.FXMLLoader;

public class MainWindowController extends Observable implements Observer   {
    ViewModel vm;

    @FXML
    private Button helpButton, restartButton, confirmButton, undoButton, PassButton;
    @FXML
    private ListView<String> letterList; // shows the letters the user has in his hand, right upper corner
    @FXML
    private GridPane gameBoard;
    @FXML
    private Label tilesLeft, letterSelected, confirmSelected, wordAdded, rowSelected, colSelected, wordDirection;
    private TextField[][] slots;
    private IntegerProperty[][] bonusData;
    private ListProperty<String>  currentHand = new SimpleListProperty<>(); // list of all the letters the user has in his hand, binds to letterList in vm
    private String clean="clean";
    private boolean legal;

    private ListProperty<CharacterData> userInput = new SimpleListProperty<>();// list of all the letters the user has selected in the last turn
    public void setViewModel(ViewModel vm) {
        this.vm = vm;
        bonusData = new IntegerProperty[15][15]; // creates a new array of integers for the bonus
        bonusData = vm.getBonus_vm(); // gets the bonus array from the viewmodel
        restartGame(); // shows the bonus tiles on the board

        confirmSelected.textProperty().bind(vm.confirm); // binds confirm to the confirm string in the viewmodel
        tilesLeft.textProperty().bind(vm.tilesLeft); // binds reslabel to the tilesLeft string in the viewmodel
        letterSelected.textProperty().bind(vm.letter); // binds letterSelected to the letterSelected string in the viewmodel
        wordAdded.textProperty().bind(vm.wordSelected); // binds wordAdded to the wordSelected string in the viewmodel
        colSelected.textProperty().bind(vm.col); // binds indexSelected to the x string in the viewmodel
        rowSelected.textProperty().bind(vm.row); // binds indexSelected to the x string in the viewmodel
        wordDirection.textProperty().bind(vm.wordDirection); // binds indexSelected to the x string in the viewmodel
        userInput.bind(vm.userInput); // binds legal to the legal boolean in the viewmodel
        letterList.itemsProperty().bind(vm.letterList); // binds the letterList to the letterList in the viewmodel
        //letterList.setItems(currentHand.get());

        for(int i=0;i<15;i++)
            for(int j=0;j<15;j++)
            {
                slots[i][j].textProperty().bind(vm.board[i][j]); // binds the text in the textfield to the board array in the viewmodel
                slots[i][j].backgroundProperty().bind(vm.background[i][j]); // binds the background in the textfield to the background array in the viewmodel
            }

        vm.addObserver(this);
    }
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
                tf.setOnDragOver(event -> {
                    if (event.getGestureSource() != tf && event.getDragboard().hasString()) {
                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    }
                    event.consume();
                });
                tf.setOnDragDropped(event -> {
                    Dragboard db = event.getDragboard();
                    boolean success = false;
                    if (db.hasString() && tf.getText()==null || tf.getText().isBlank()) {
                        char letter = db.getString().charAt(0);
                        this.showLetterSelected(letter, GridPane.getRowIndex(tf), GridPane.getColumnIndex(tf));
                        success = true;
                    }
                    event.setDropCompleted(success);
                    event.consume();
                });
            }
        }
        gameBoard.setGridLinesVisible(true); // Add this line to make grid lines visible
        letterList.setOnDragDetected(event -> {
            String letter = letterList.getSelectionModel().getSelectedItem();
            if (letter != null) {
                Dragboard db = letterList.startDragAndDrop(TransferMode.ANY);
                ClipboardContent content = new ClipboardContent();
                content.putString(letter);
                db.setContent(content);
            }
            event.consume();
            gameBoard.requestLayout(); // Refresh the layout of the GridPane
        });
    }

    @FXML
    public void showHelp() {
            // Creating new window
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("HelpWindowController.fxml"));
                Parent root1 = (Parent) fxmlLoader.load();
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);  // this will block interaction with the parent window until this one is closed
                stage.setTitle("Help Menu");
                stage.setScene(new Scene(root1, 500,500)); // Use the width and height of the mainStage
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }

    }
    @FXML
    public void showConfirm() {
        System.out.println("Confirm button pressed"); // just a check to see if the button works
        vm.confirmSelected(); // activates the confirmSelected function from the viewmodel

        userInput.clear(); // clears the list of letters the user has selected
        //letterList.setItems(currentHand.get());

    }
    @FXML
    public void undo() {
        System.out.println("Undo button pressed"); // just a check to see if the button works
        vm.undoSelected(); // activates the undoSelected function from the viewmodel
    }
    @FXML
    public void pass()
    {
        System.out.println("Pass button pressed"); // just a check to see if the button works

        vm.passSelected(); // activates the undoSelected function from the viewmodel

    }


    public void showLetterSelected(char letter, int row, int col) {
        System.out.println("letter:"+ letter);
        System.out.println(" index: " + row + ", " + col);
        vm.letterSelected(letter, row, col);
    }

    @FXML
    public void restartGame() {
        vm.restartGame();
    }
    @Override
    public void update(Observable o, Object arg) {
	    }
}

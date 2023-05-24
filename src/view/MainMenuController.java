package view;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import viewmodel.ViewModel;
import javafx.fxml.FXMLLoader;


public class MainMenuController extends Observable implements Observer {

    private Stage stage;
    ViewModel vm;

    public void setStage(Stage stage) {
        this.stage = stage;

    }

    @FXML
    public void startGame() {
        try {

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
            Parent root1 =  fxmlLoader.load();  // Changed Parent to VBox
            MainWindowController gameController = fxmlLoader.getController();
            gameController.setStage(this.stage);
            gameController.setViewModel(this.vm); // assuming you pass the ViewModel to MainMenuController
            this.stage.setScene(new Scene(root1));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showHelp() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("HelpWindowController.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            HelpMenuController helpMenuController = fxmlLoader.getController();
            helpMenuController.setStage(stage);
            helpMenuController.setMainMenuController(this);
            helpMenuController.setReturnTo("MainMenu");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showStatistics() {
        // load statistics scene
    }

    public void setViewModel(ViewModel vm) {
        this.vm = vm;

    }

    @Override
    public void update(Observable o, Object arg) {

    }
}


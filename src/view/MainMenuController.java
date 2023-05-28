package view;

import java.util.Observable;
import java.util.Observer;
import javafx.fxml.FXML;
import viewmodel.ViewModel;

public class MainMenuController implements Observer {

    private SceneController sceneController;
    private ViewModel vm;

    public void setSceneController(SceneController sceneController) {
        this.sceneController = sceneController;
    }

    @FXML
    public void startGame() {
        sceneController.showGame();

    }

    public void joinGame() {
        // todo - change
        // join already started game
        sceneController.joinGame();
    }

    @FXML
    public void showHelp() {
        sceneController.showHelpMenu();
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

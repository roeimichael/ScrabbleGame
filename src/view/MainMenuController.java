package view;

import java.util.Observable;
import java.util.Observer;

import NOT_USED.newServer;
import javafx.fxml.FXML;
import model.Model;
import viewmodel.ViewModel;

public class MainMenuController implements Observer {

    private SceneController sceneController;
    private newServer server;
    private ViewModel vm;
    private Model model;

    public void setSceneController(SceneController sceneController) {
        this.sceneController = sceneController;
    }

    @FXML
    public void startGame() {

        sceneController.showGame();
    }
    @FXML
    public void showHostWaitingRoom() {

        server = newServer.get();

        this.model.connectToServer();
        vm.getNumPlayers();
        sceneController.showHostWaitingRoom();
    }
    @FXML
    public void showGuestWaitingRoom() {
        this.model.connectToServer();
        vm.getNumPlayers();

        sceneController.showGuestWaitingRoom();
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

    public void setModel(Model m) {
        this.model = m;
    }
}

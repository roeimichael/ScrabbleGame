package view;

import NewServer.newServer;
import NewServer.protocols;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import viewmodel.ViewModel;

import java.util.Observable;
import java.util.Observer;

public class GuestWindowController extends Observable implements Observer {
    private SceneController sceneController;
    private Stage stage;

    @FXML
    private Button startGameButton;

    @FXML
    private Label numberOfPlayersLabel;
    public static newServer server;
    private ViewModel viewModel;

    public void setSceneController(SceneController sceneController) {
        this.sceneController = sceneController;
    }
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML


    public void setViewModel(ViewModel viewModel) {
        this.viewModel = viewModel;
        viewModel.gameStartedProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                System.out.println("game started");
                if (newValue) {
                    sceneController.showGame();
                }
            });

        });
    }


    @Override
    public void update(Observable o, Object arg) {

    }
}

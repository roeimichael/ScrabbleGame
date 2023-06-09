package view;
import NewServer.newServer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import viewmodel.ViewModel;
import NewServer.protocols;
import java.util.Observable;
import java.util.Observer;

public class HostWindowController extends Observable implements Observer {
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
    public void startGame() {
        server = newServer.get();
        System.out.println("Starting game");
        sceneController.showGame();
        server.sendMessagesToAllClients(protocols.START_GAME);
        //
    }

    public void setViewModel(ViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}

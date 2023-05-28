package view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Model;
import model.ScrabblePlayer;
import server.Client;
import server.protocols;
import test.GameManager;
import viewmodel.ViewModel;

import java.io.IOException;

public class SceneController {
    private Stage primaryStage;
    private ViewModel viewModel;
    private Model model;

    private GameManager gameManager;

    private Scene mainMenuScene;
    private Scene helpMenuScene;
    private Scene gameScene;

    public SceneController(Stage primaryStage) {
        this.primaryStage = primaryStage;

        this.model = new Model();
        this.viewModel = new ViewModel(model);
        this.gameManager = GameManager.getInstance();
        model.addObserver(viewModel);
        gameManager.addObserver(viewModel);
        this.primaryStage.setWidth(620);  // Width
        this.primaryStage.setHeight(620);  // Height
        this.primaryStage.setResizable(false); // Making sure the window is not resizable
    }

    public void loadScenes() throws IOException {
        // Load main menu scene
        FXMLLoader mainMenuLoader = new FXMLLoader(getClass().getResource("MainMenu.fxml"));
        Parent mainMenuRoot = mainMenuLoader.load();
        MainMenuController mainMenuController = mainMenuLoader.getController();
        mainMenuController.setViewModel(viewModel);
        mainMenuController.setSceneController(this);
        viewModel.addObserver(mainMenuController);
        mainMenuScene = new Scene(mainMenuRoot);

        // Load help menu scene
        FXMLLoader helpMenuLoader = new FXMLLoader(getClass().getResource("HelpWindow.fxml"));
        Parent helpMenuRoot = helpMenuLoader.load();
        HelpMenuController helpMenuController = helpMenuLoader.getController();
        helpMenuController.setViewModel(viewModel);
        helpMenuController.setSceneController(this);
        viewModel.addObserver(helpMenuController);
        helpMenuScene = new Scene(helpMenuRoot);

        // Load game scene
        FXMLLoader gameLoader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
        Parent gameRoot = gameLoader.load();
        MainWindowController gameController = gameLoader.getController();
        gameController.setViewModel(viewModel);
        gameController.setSceneController(this);
        viewModel.addObserver(gameController);
        gameScene = new Scene(gameRoot);
    }

    public void showMainMenu() {
        primaryStage.setScene(mainMenuScene);
        primaryStage.show();
    }

    public void showHelpMenu() {
        primaryStage.setScene(helpMenuScene);
        primaryStage.show();
    }

    public void showGame() {
        System.out.println("Host button pressed");

        Client sp = new Client();
        sp.startServer();
        sp.sendMessage(protocols.NEW_GAME_AS_HOST);
        primaryStage.setScene(gameScene);
        primaryStage.show();
        viewModel.restartGame();
    }

    public void joinGame() {
        System.out.println("Join button pressed");
        Client sp = new Client();
        sp.connectToServer();
        sp.sendMessage(protocols.JOIN_GAME_AS_CLIENT);
//        if (viewModel.getGameStarted()) {
//            System.out.println("Game already started");
//            return;
//        }
        primaryStage.setScene(gameScene);
        primaryStage.show();

    }
}

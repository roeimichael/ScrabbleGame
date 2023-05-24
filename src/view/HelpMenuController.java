package view;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import viewmodel.ViewModel;
import javafx.fxml.FXMLLoader;

public class HelpMenuController extends Observable implements Observer {
    @FXML
    private Button closeButton;
    private MainWindowController mainWindowController;
    private MainMenuController mainMenuController;
    private String returnTo = "";
    private Stage stage;
    ViewModel vm;
    public void setReturnTo(String returnTo) {
        this.returnTo = returnTo;
    }
    public void setMainMenuController(MainMenuController mainMenuController) {
        this.mainMenuController = mainMenuController;
    }

    public void setMainWindowController(MainWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;
    }
    @FXML
    public void closeHelp() {
        try {
            FXMLLoader fxmlLoader;
            Parent root1;
            if ("MainMenu".equals(returnTo)) {
                fxmlLoader = new FXMLLoader(getClass().getResource("MainMenu.fxml"));
                root1 = (Parent) fxmlLoader.load();
                mainMenuController = fxmlLoader.getController();
                mainMenuController.setStage(stage);
            } else {
                fxmlLoader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
                root1 = (Parent) fxmlLoader.load();
                mainWindowController = fxmlLoader.getController();
                mainWindowController.setStage(stage);
            }
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setViewModel(ViewModel vm) {
        this.vm = vm;

    }
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void update(Observable o, Object arg) {

    }


}
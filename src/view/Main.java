package view;
	
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;
import model.Model;
import viewmodel.ViewModel;


import java.util.ArrayList;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader fxl = new FXMLLoader();
			Parent root = fxl.load(getClass().getResource("MainMenu.fxml").openStream());
			MainMenuController mmw = fxl.getController(); // view, getting the controller
			mmw.setStage(primaryStage);
			Model m = new Model(); // modelModel m= new Model(); // our model
			ViewModel vm = new ViewModel(m); // our view model
			m.addObserver(vm); // adding the view model as an observer to the model
			mmw.setViewModel(vm); // setting the view model in the view
			vm.addObserver(mmw); // adding the view as an observer to the view model
			Scene scene = new Scene(root,600,600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}

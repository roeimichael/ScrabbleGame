package view;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;
import model.Model;
import viewmodel.ViewModel;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {

			FXMLLoader fxl = new FXMLLoader();
			BorderPane root = (BorderPane)fxl.load(getClass().getResource("MainWindow.fxml").openStream());

			MainWindowController mwc = fxl.getController(); // view, getting the controller
			Model m = new Model(); // modelModel m= new Model(); // our model
			ViewModel vm = new ViewModel(m); // our view model

			m.addObserver(vm); // adding the view model as an observer to the model
			mwc.setViewModel(vm); // setting the view model in the view
			vm.addObserver(mwc); // adding the view as an observer to the view model

			Scene scene = new Scene(root,500,500);

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

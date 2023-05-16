package viewmodel;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import java.util.Observable;
import java.util.Observer;
import model.Model;
import view.MainWindowController;

public class ViewModel implements Observer {
	Model m;
	  public IntegerProperty inputkey = new SimpleIntegerProperty();
	public ViewModel(Model m) {
		this.m = m;
		m.addObserver(this);
		
		inputkey.addListener((o,ov,nv)->m.setInputKey((int)nv));
	}
	 public void makeMove(String move) {
	        m.updateBoardState(move);
	    }

	 @Override
	    public void update(Observable obs, Object obj) {
	        // Update the view model based on changes from the game model
	        // obj should be the new board state
	        String newBoardState = (String) obj;
	        // Process the new state as needed for the view
	    }
	
}

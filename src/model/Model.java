package model;
import java.util.Observable;
import java.util.Observer;

public class Model extends Observable {
	   private String boardState;

	    public Model() {
	        this.boardState = "";
	    }

	    public void updateBoardState(String newBoardState) {
	        this.boardState = newBoardState;
	        setChanged();
	        notifyObservers(this.boardState);
	    }
	}

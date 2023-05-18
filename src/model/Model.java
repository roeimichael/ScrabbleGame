package model;
import java.util.Observable;
import test.Board;

public class Model extends Observable {
		private String boardState;
		String result;
		String letter;
		private Board board;


	public Model() {
	    this.board = new Board();
		this.boardState = "";
	    }

	    public void updateBoardState(String newBoardState) {
	        this.boardState = newBoardState;
	        setChanged();
	        notifyObservers(this.boardState);
	    }
		public void applyString(String s) // apply string s to result and notify observers
		{
			result = s;
			setChanged();
			notifyObservers(result);
		}
		public String getResult() {
			return result;
		}
		public String getLetter() {
			return letter;
		}

		public byte[][] getBonus() {
			return board.getBonus();
		}
	public void letterSelected(String letter) {
		this.letter = letter;
		setChanged();
		notifyObservers(this.letter);
	}
}

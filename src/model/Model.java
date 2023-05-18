package model;
import java.util.Observable;
import test.*;
import java.util.ArrayList;


public class Model extends Observable {
	private String boardState;
	private String result;
	private String letter;
	private Board board;
	private ArrayList<Player> players;
	private GameManager gameManager;
	private Tile.Bag tileBag;


	public Model() {
	    this.board = new Board();
		this.boardState = "";
		this.tileBag = new Tile.Bag();
		this.players = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			players.add(new Player(i, tileBag));
		}
		this.gameManager = new GameManager(board, players, tileBag);

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

package viewmodel;
import javafx.beans.property.*;

import java.util.Observable;
import java.util.Observer;
import java.util.stream.Collectors;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import model.Model;
import server.ConnectionHandler;
import test.CharacterData;

public class ViewModel extends Observable implements Observer {

	Model m;
	final byte dl=2;	// double letter
	final byte tl=3;	// triple letter
	final byte dw=20;	// double word
	final byte tw=30;	// triple word
	private byte[][] bonus= {
			{tw,0,0,dl,0,0,0,tw,0,0,0,dl,0,0,tw},
			{0,dw,0,0,0,tl,0,0,0,tl,0,0,0,dw,0},
			{0,0,dw,0,0,0,dl,0,dl,0,0,0,dw,0,0},
			{dl,0,0,dw,0,0,0,dl,0,0,0,dw,0,0,dl},
			{0,0,0,0,dw,0,0,0,0,0,dw,0,0,0,0},
			{0,tl,0,0,0,tl,0,0,0,tl,0,0,0,tl,0},
			{0,0,dl,0,0,0,dl,0,dl,0,0,0,dl,0,0},
			{tw,0,0,dl,0,0,0,dw,0,0,0,dl,0,0,tw},
			{0,0,dl,0,0,0,dl,0,dl,0,0,0,dl,0,0},
			{0,tl,0,0,0,tl,0,0,0,tl,0,0,0,tl,0},
			{0,0,0,0,dw,0,0,0,0,0,dw,0,0,0,0},
			{dl,0,0,dw,0,0,0,dl,0,0,0,dw,0,0,dl},
			{0,0,dw,0,0,0,dl,0,dl,0,0,0,dw,0,0},
			{0,dw,0,0,0,tl,0,0,0,tl,0,0,0,dw,0},
			{tw,0,0,dl,0,0,0,tw,0,0,0,dl,0,0,tw}
	};
	public int PlayerIndex;
	public IntegerProperty[][] bonus_vm; // saves the bonus tiles
	public StringProperty wordSelected, tilesLeft, letter, confirm, row, col, wordDirection, playerPoints, turn,numPlayersConnected; // all strings that binded to labels in the view
	public SimpleStringProperty[][] board; // saves the letters on the board
	public ObjectProperty<Background>[][] background; // saves the background of the board
	public ListProperty<String> letterList; // saves the 7 letters in the user's hand, binds to currentHand in the view
	public ListProperty<CharacterData> userInput; // saves the letters the user has selected in the current turn
	public ListProperty<CharacterData> lastEntry; // saves the letters the user has selected in the last turn

	public ViewModel(Model m) {
		this.m = m;
		m.addObserver(this);
		initializeProperties();
	}

	public void makeMove(String move) {
		m.updateBoardState(move);
	}

	public void applyString() {
		System.out.println("ViewModel: applyString");
		m.applyString("just put the letters on the board"); // activates the applyString function in the model
	}

	public void letterSelected(char letter, int row, int col) {
		board[row][col].set(Character.toString(letter)); // updates the board with the letter the user has selected
		userInput.add(new CharacterData(letter, row, col)); // adds the letter to the list of letters the user has selected
		background[row][col].set(new Background(new BackgroundFill(Color.LIGHTYELLOW, null, null))); // changes the background of the letter to light blue
		//userBoardList.add(Character.toString(letter)); // adds the letter to the list of letters the user has selected
		letterList.remove(Character.toString(letter)); // removes the letter from the list of letters the user has in his hand
		m.letterSelected(letter, row, col);
	}

	public void confirmSelected() {
		m.confirmSelected();
	}

	public void undoSelected() {
		m.undoSelected();
	}
	public void passSelected() {
		m.passSelected();
	}

	public void setBonus_vm(byte[][] bonus) {
		for (int i = 0; i < 15; i++)
			for (int j = 0; j < 15; j++) {
				bonus_vm[i][j] = new SimpleIntegerProperty();
				//bonus_vm[i][j].set(bonus[i][j]);
				bonus_vm[i][j].set(bonus[i][j]);
			}

	}

	public IntegerProperty[][] getBonus_vm() {
		return bonus_vm;
	}

	@Override
	public void update(Observable obs, Object obj) {
		if (obs != m) {
			return; // If the notification is not from Model, we simply return.
		}

		String newBoardState = getBoardState(obj);
		setChanged();
		notifyObservers(newBoardState);
		System.out.println("ViewModel: " + newBoardState);

		switch (newBoardState) {
			//case "help" -> handleHelpRequest();
			case "clear" -> handleClearRequest();
			case "confirmed" -> handleConfirmation();
			case "pass" -> handlePass();
			case "undo" -> handleUndoRequest();
			case "restart" -> handleRestartRequest();
			case "join" -> handleJoinRequest();
			case "challenge accepted" -> handleChallengeAccepted();
			case "host" -> handleHost();

			default -> {
				if (obj instanceof Character) {
					handleLetterSelection();
				}
			}
		}
	}

	private void handleHost() {
		setBonus_vm(m.getBonus());

	}

	private void handleJoinRequest() {
		confirm.set("Join Request Sent");

	}

	private void handleChallengeAccepted() {
		confirm.set("Challenge Accepted");
		wordSelected.set("");
		System.out.println("lastEntry: " + lastEntry.get());
		int wordSize = lastEntry.size();
		for (int i = 0; i < wordSize; i++) {
			board[lastEntry.get(i).getRow()][lastEntry.get(i).getColumn()].set("");
			setBackground(lastEntry.get(i).getRow(), lastEntry.get(i).getColumn());
		}

		playerPoints.set(m.getPlayerScore());
	}

	private void updateLetterList() {
		// update the letters the player see
		letterList.set(FXCollections.observableArrayList(m.getplayerbyid(PlayerIndex).gethand().stream().map(test.Tile::toString).collect(Collectors.toList())));
	}

	private String getBoardState(Object obj) {
		if (obj instanceof Integer) {
			return Integer.toString((int) obj);
		} else if (obj instanceof Character) {
			return Character.toString((char) obj);
		} else {
			return (String) obj;
		}
	}

	private void getTilesLeft() {
		tilesLeft.set(m.getTilesLeft());
	}

	private void handleClearRequest() {
		confirm.set("");
		wordSelected.set("");
		row.set("");
		col.set("");
		wordDirection.set("");
	}

	private void handleConfirmation() {
		confirm.set(m.getConfirm());
		wordSelected.set("");
		wordSelected.set(m.getWordSelected());
		row.set(m.getRow());
		col.set(m.getCol());
		System.out.println("word selected: " + wordSelected.get());

		if (!wordSelected.get().equals("") )
		{
			wordDirection.set(m.getWordDirection());
		}
		else {
			int wordSize = userInput.size();
			for (int i = 0; i < wordSize; i++) {
				m.undoSelected();
			}
		}
		turn.set(m.getTurn());
		lastEntry.clear();
		lastEntry.addAll(userInput);
		userInput.clear();
		m.cleanList();
		updateLetterList();
		getTilesLeft();
		playerPoints.set(m.getPlayerScore());
	}

	public void handlePass()
	{
		confirm.set("Passed Turn");
		wordSelected.set("");
		row.set("");
		col.set("");
		wordDirection.set("");
		int wordSize = userInput.size();
		for (int i = 0; i < wordSize; i++) {
			m.undoSelected();
		}
		userInput.clear();
		m.cleanList();
		updateLetterList();
		//getTilesLeft();
	}


	private void handleUndoRequest() {
		if (userInput.size() > 0) {
			confirm.set("");
			wordSelected.set("");
			row.set("");
			col.set("");
			wordDirection.set("");
			int index = userInput.size() - 1;
			int i = userInput.get(index).getRow();
			int j = userInput.get(index).getColumn();
			letterList.add(Character.toString(userInput.get(index).getLetter()));
			userInput.remove(index);
			board[i][j].set("");
			setBackground(i, j);
		}
	}
	private void handleRestartRequest() {
		System.out.println("restart");
		updateLetterList();
		confirm.set("");
		wordSelected.set("");
		row.set("");
		col.set("");
		wordDirection.set("");
		userInput.clear();
		m.cleanList();
		getTilesLeft();
		playerPoints.set(m.getPlayerScore());

	}

	private void handleLetterSelection() {
		letter.set(String.valueOf(m.getLetter()));
	}

	private void setBackground(int i, int j) {
		switch (bonus_vm[i][j].get()) {
			case 2:
				background[i][j].set(new Background(new BackgroundFill(Color.LIGHTBLUE, null, null)));
				break;
			case 3:
				background[i][j].set(new Background(new BackgroundFill(Color.DARKBLUE, null, null)));
				break;
			case 20:
				background[i][j].set(new Background(new BackgroundFill(Color.LIGHTPINK, null, null)));
				break;
			case 30:
				background[i][j].set(new Background(new BackgroundFill(Color.DARKRED, null, null)));
				break;
			default:
				background[i][j].set(new Background(new BackgroundFill(Color.WHITE, null, null)));
		}
	}

	public void loadGame() {
		resetGameBoard();
		handleRestartRequest();
	}
	public void restartGame() {
		m.restart();
		resetGameBoard();
	}
	private void resetGameBoard() {
		for (int i = 0; i < 15; i++) {
			for (int j = 0; j < 15; j++) {
				board[i][j].set("");
				switch (bonus[i][j]) { //.getValue()
					case 2 -> background[i][j].set(new Background(new BackgroundFill(Color.LIGHTBLUE, null, null)));
					case 3 -> background[i][j].set(new Background(new BackgroundFill(Color.DARKBLUE, null, null)));
					case 20 -> background[i][j].set(new Background(new BackgroundFill(Color.LIGHTPINK, null, null)));
					case 30 -> background[i][j].set(new Background(new BackgroundFill(Color.DARKRED, null, null)));
					default -> background[i][j].set(new Background(new BackgroundFill(Color.WHITE, null, null)));
				}
			}
		}
	}

	private void initializeProperties() {
		board = new SimpleStringProperty[15][15];
		confirm = new SimpleStringProperty();
		tilesLeft = new SimpleStringProperty();
		letter = new SimpleStringProperty();
		wordSelected = new SimpleStringProperty();
		row = new SimpleStringProperty();
		col = new SimpleStringProperty();
		wordDirection = new SimpleStringProperty();
		playerPoints = new SimpleStringProperty();
		userInput = new SimpleListProperty<CharacterData>(FXCollections.observableArrayList());
		lastEntry = new SimpleListProperty<CharacterData>(FXCollections.observableArrayList());
		letterList = new SimpleListProperty<String>();
		turn = new SimpleStringProperty();
		numPlayersConnected = new SimpleStringProperty("0");
		//userBoardList = new ArrayList<>();
		bonus_vm = new IntegerProperty[15][15];
		background = new ObjectProperty[15][15];
		for (int i = 0; i < 15; i++)
			for (int j = 0; j < 15; j++) {
				board[i][j] = new SimpleStringProperty();
				background[i][j] = new SimpleObjectProperty<>();
			}
		//		inputkey.addListener((o,ov,nv)->m.setInputKey((int)nv)
	}


	public void challengeSelected() {
		if(!m.challenge())
		{
			turn.set(m.getTurn());
		}
	}

    public void startSelected() {
		m.start();
    }

	public void setPlayerID(String index) {
		PlayerIndex = Integer.parseInt(index);
	}
}

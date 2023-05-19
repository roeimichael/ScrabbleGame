package viewmodel;
import javafx.beans.property.*;

import java.util.Observable;
import java.util.Observer;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import model.Model;
import test.CharacterData;

public class ViewModel extends Observable implements Observer {
	public ListProperty<CharacterData> userInput;
	Model m;
	public IntegerProperty inputkey = new SimpleIntegerProperty();
	public IntegerProperty[][] bonus_vm;
	public StringProperty wordSelected, res, letter, confirm, row, col, wordDirection;
	public SimpleStringProperty[][] board;
	public ObjectProperty<Background>[][] background;


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
		m.letterSelected(letter, row, col);
	}

	public void confirmSelected() {
		m.confirmSelected();
	}

	public void undoSelected() {
		m.undoSelected();

	}

	public void setBonus_vm(byte[][] bonus) {
		for (int i = 0; i < 15; i++)
			for (int j = 0; j < 15; j++) {
				bonus_vm[i][j] = new SimpleIntegerProperty();
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
			case "help":
				handleHelpRequest();
				break;
			case "clear":
				handleClearRequest();
				break;
			case "confirmed":
				handleConfirmation();
				break;
			case "undo":
				handleUndoRequest();
				break;
			default:
				if (obj instanceof Character) {
					handleLetterSelection();
				}
		}
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

	private void handleHelpRequest() {
		res.set(m.getHelp());
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
		wordSelected.set(m.getWordSelected());
		row.set(m.getRow());
		col.set(m.getCol());
		System.out.println("word selected: " + wordSelected.get());

		if (!wordSelected.get().equals("") ) {
			wordDirection.set(m.getWordDirection());
		} else {
			int wordSize = userInput.size();
			for (int i = 0; i < wordSize; i++) {
				m.undoSelected();
			}
		}
		userInput.clear();
		m.cleanList();
	}

	private void handleUndoRequest() {
		if (userInput.size() > 0) {
			int index = userInput.size() - 1;
			int i = userInput.get(index).getRow();
			int j = userInput.get(index).getColumn();
			userInput.remove(index);
			board[i][j].set("");
			setBackground(i, j);
		}
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

	public void restartGame() {
		m.restart();
		resetGameBoard();
	}
	private void resetGameBoard() {
		for (int i = 0; i < 15; i++) {
			for (int j = 0; j < 15; j++) {
				board[i][j].set("");
				switch (bonus_vm[i][j].getValue()) {
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
		res = new SimpleStringProperty();
		letter = new SimpleStringProperty();
		wordSelected = new SimpleStringProperty();
		row = new SimpleStringProperty();
		col = new SimpleStringProperty();
		wordDirection = new SimpleStringProperty();
		userInput = new SimpleListProperty<CharacterData>(FXCollections.observableArrayList());
		bonus_vm = new IntegerProperty[15][15];
		background = new ObjectProperty[15][15];
		setBonus_vm(m.getBonus());
		for (int i = 0; i < 15; i++)
			for (int j = 0; j < 15; j++) {
				board[i][j] = new SimpleStringProperty();
				background[i][j] = new SimpleObjectProperty<>();
			}
		//		inputkey.addListener((o,ov,nv)->m.setInputKey((int)nv)
	}
}

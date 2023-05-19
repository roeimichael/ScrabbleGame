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
		board = new SimpleStringProperty[15][15];
		confirm = new SimpleStringProperty(); // contains the random number
		res = new SimpleStringProperty(); // contains the text for the help button
		letter = new SimpleStringProperty(); // contains the text for the letter button
		wordSelected = new SimpleStringProperty(); // contains the text for the word button
		row = new SimpleStringProperty(); // contains the text for the row button
		col = new SimpleStringProperty(); // contains the text for the col button
		wordDirection = new SimpleStringProperty(); // contains the text for the col button
		userInput = new SimpleListProperty<CharacterData>(FXCollections.observableArrayList());
		bonus_vm = new IntegerProperty[15][15];
		background = new ObjectProperty[15][15];
		setBonus_vm(m.getBonus());
		for (int i = 0; i < 15; i++)
			for (int j = 0; j < 15; j++) {
				board[i][j] = new SimpleStringProperty();
				background[i][j] = new SimpleObjectProperty<>();
			}

		//		inputkey.addListener((o,ov,nv)->m.setInputKey((int)nv));
	}

	public void makeMove(String move) {
		m.updateBoardState(move);
	}

	public void applyString() {
		System.out.println("ViewModel: applyString");
		m.applyString("just put the letters on the board"); // activates the applyString function in the model
	}

	public void letterSelected(char letter, int row, int col) {
		//System.out.println("letter selected:"+letter);
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
		String newBoardState;
		if (obj.getClass() == Integer.class)
			newBoardState = Integer.toString((int) obj);
		else {
			if (obj instanceof Character)
				newBoardState = Character.toString((char) obj);
			else {
				newBoardState = (String) obj;

			}
		}
		setChanged();
		notifyObservers(newBoardState);

		if (obs == m) // indicating that the model has changed and sent a notification
		{
			System.out.println("ViewModel: "+newBoardState);

			if (newBoardState.equals("help")) // when the help button is pressed in the model it sends a "help" notification to the viewmodel
			{
				res.set(m.getHelp()); // if model changed, update the help text
			} else if (newBoardState.length() == 1 && obj.getClass() != Integer.class) {// when a letter button is pressed in the model it sends a notification with the letter to the viewmodel
				letter.set(String.valueOf(m.getLetter())); // if model changed, update the letter
			} else if (newBoardState.equals("clear"))
			// when a new letter is dropped after confirmation, the model sends a notification to clear the messages from the older word
			{
				confirm.set(""); // if model changed, update the confirmation
				wordSelected.set("");
				row.set("");
				col.set("");
				wordDirection.set("");
			} else if (newBoardState.equals("confirmed")) { // means that the model has changed and sent a notification with the confirmation message
				// so we need to update the game accordingly

				confirm.set(m.getConfirm()); // if model changed, update the confirmation
				wordSelected.set(m.getWordSelected());
				row.set(m.getRow());
				col.set(m.getCol());
				System.out.println("word selected: " + wordSelected.get());
				int wordSize = userInput.size();
				if (!wordSelected.get().equals("") ) {
					wordDirection.set(m.getWordDirection());

					//m.removeItemsFromUserInput();
				} else
				// we got an illegal word so we need to update the GUI board accordingly.
				// the real board shouldnt be affected by this
				{
					for (int i = 0; i < wordSize; i++) {
//						int row = userInput.get(0).getRow();
//						int col = userInput.get(0).getColumn();
//						userInput.remove(0);
//						board[row][col].set("");
						m.undoSelected();


					}

				}
				userInput.clear();
				m.cleanList();

			} else if (newBoardState.equals("undo")) {
				if (userInput.size() > 0) {
					System.out.println("userInput: " + userInput);
					int index = userInput.size() - 1;
					int i = userInput.get(index).getRow();
					int j = userInput.get(index).getColumn();
					userInput.remove(index); // removes the last letter the user has selected
					board[i][j].set("");
					switch (bonus_vm[i][j].get()) {
						case 2 -> background[i][j].set(new Background(new BackgroundFill(Color.LIGHTBLUE, null, null)));
						case 3 -> background[i][j].set(new Background(new BackgroundFill(Color.DARKBLUE, null, null)));
						case 20 -> background[i][j].set(new Background(new BackgroundFill(Color.LIGHTPINK, null, null)));
						case 30 -> background[i][j].set(new Background(new BackgroundFill(Color.DARKRED, null, null)));
						default -> background[i][j].set(new Background(new BackgroundFill(Color.WHITE, null, null)));

					}

				}

			} else {
				// means we got an unexpected notification from the model
				// that we cant handle
			}
		} else // indicating that the view has changed and sent a notification
		{

		}
	}

	public void restartGame() {
		m.restart();
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
			//gameBoard.setGridLinesVisible(true); // Add this line to make grid lines visible

		}


	}
}

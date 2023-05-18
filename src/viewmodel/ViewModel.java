package viewmodel;
import javafx.beans.property.*;

import java.util.Observable;
import java.util.Observer;

import javafx.beans.value.ObservableValue;
import model.Model;

public class ViewModel extends Observable implements Observer {
	Model m;
	public IntegerProperty inputkey = new SimpleIntegerProperty();
	public IntegerProperty[][] bonus_vm;
	public StringProperty wordSelected, res, letter, confirm, row, col, wordDirection;
	public StringProperty rand_vm;


	public ViewModel(Model m) {
		this.m = m;
		m.addObserver(this);

		confirm = new SimpleStringProperty(); // contains the random number
		res = new SimpleStringProperty(); // contains the text for the help button
		letter = new SimpleStringProperty(); // contains the text for the letter button
		wordSelected = new SimpleStringProperty(); // contains the text for the word button
		row = new SimpleStringProperty(); // contains the text for the row button
		col = new SimpleStringProperty(); // contains the text for the col button
		wordDirection = new SimpleStringProperty(); // contains the text for the col button

		bonus_vm = new IntegerProperty[15][15];
		setBonus_vm(m.getBonus());

	//		inputkey.addListener((o,ov,nv)->m.setInputKey((int)nv));
	}
	 public void makeMove(String move) {
			m.updateBoardState(move);
		}

	public void applyString() {
		System.out.println("ViewModel: applyString");
		m.applyString("just put the letters on the board"); // activates the applyString function in the model
	}

	public void letterSelected(String letter, int row, int col) {
		//System.out.println("letter selected:"+letter);
		m.letterSelected(letter, row, col);
	}
	public void confirmSelected() {
		m.confirmSelected();
	}

	public void setBonus_vm(byte[][] bonus) {
		for(int i=0;i<15;i++)
			for(int j=0;j<15;j++)
			{
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
				newBoardState =Integer.toString((int)obj);
			else {
				newBoardState = (String) obj;
			}
			setChanged();
			notifyObservers(newBoardState);

			if(obs==m) // indicating that the model has changed and sent a notification
			{
				System.out.println("ViewModel: "+newBoardState);

				if (newBoardState.equals("help")) // when the help button is pressed in the model it sends a "help" notification to the viewmodel
				{
					res.set(m.getHelp()); // if model changed, update the help text
				}
				else if (newBoardState.length()==1 && obj.getClass() != Integer.class)
				{// when a letter button is pressed in the model it sends a notification with the letter to the viewmodel
					letter.set(m.getLetter()); // if model changed, update the letter
				}
				else if (newBoardState.equals("clear"))
				// when a new letter is dropped after confirmation, the model sends a notification to clear the messages from the older word
				{
					confirm.set(""); // if model changed, update the confirmation
					wordSelected.set("");
					row.set("");
					col.set("");
					wordDirection.set("");
				}
				else if (newBoardState.equals("confirmed"))
				{ // means that the model has changed and sent a notification with the confirmation message
					// so we need to update the game accordingly

					confirm.set(m.getConfirm()); // if model changed, update the confirmation
					wordSelected.set(m.getWordSelected());
					row.set(m.getRow());
					col.set(m.getCol());
					wordDirection.set(m.getWordDirection());
					m.cleanList();
				}
				else {
					// means we got an unexpected notification from the model
					// that we cant handle
				}
			}
			else // indicating that the view has changed and sent a notification
			{
				if (newBoardState.equals("restart")) // we want to clean the word selected
				{
					m.restart();
				}


			}
		}

	}

package viewmodel;
import javafx.beans.property.*;

import java.util.Observable;
import java.util.Observer;
import model.Model;
import view.MainWindowController;

public class ViewModel extends Observable implements Observer {
	Model m;
	public IntegerProperty inputkey = new SimpleIntegerProperty();
	public IntegerProperty[][] bonus_vm;
	public StringProperty x, res, letter;

	public ViewModel(Model m) {
		this.m = m;
		m.addObserver(this);

		//x = new SimpleStringProperty();
		res = new SimpleStringProperty(); // contains the text for the help button
		letter = new SimpleStringProperty(); // contains the text for the letter button
		bonus_vm = new IntegerProperty[15][15];
		setBonus_vm(m.getBonus());

	//		inputkey.addListener((o,ov,nv)->m.setInputKey((int)nv));
	}
	 public void makeMove(String move) {
			m.updateBoardState(move);
		}

	public void applyString() {
		m.applyString("this is help"); // activates the applyString function in the model
	}

	public void letterSelected(String letter) {
		//System.out.println("letter selected:"+letter);
		m.letterSelected(letter);
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
			String newBoardState = (String) obj;
			setChanged();
			notifyObservers(newBoardState);
			if(obs==m)
			{
				res.set(m.getResult()); // if model changed, update the result
				letter.set(m.getLetter()); // if model changed, update the letter
			}
		}

	}

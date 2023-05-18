package model;
import java.util.ArrayList; // import the ArrayList class

import test.Board;
import test.CharacterData; // this is the class that holds the string and its location

import java.util.Comparator;
import java.util.Observable;

public class Model extends Observable {
		private String boardState;

		private Board board;
		String help, letter, confirm;

		// characterList will hold all the characters of the word selected by the user with their indices
		ArrayList<CharacterData> characterList = new ArrayList<>();

		// the following are used to save the word selected by the user
		String wordSelected="";
		int row=-1, col=-1;
		int rowCur=-1, colCur=-1;


		int rand;

	    public Model() {
	        board = new Board();
			this.boardState = "";
	    }

	    public void updateBoardState(String newBoardState) {
	        this.boardState = newBoardState;
	        setChanged();
	        notifyObservers(this.boardState);
	    }
		public void applyString(String s) // apply string s to help and notify observers
		{
			help = s;
			setChanged();
			notifyObservers("help");
		}
		public void addLetter(CharacterData cd)
		{
			// check if the letter is already in the list
			for (CharacterData c : characterList)
				if (c.compareIndex(cd))
				{
					c.setLetter(cd.getLetter());
					return;
				}
			// if not then add it
			characterList.add(cd);
		}
		public void letterSelected(String letter, int row, int col) {
			addLetter(new CharacterData(letter, row, col)); // adding the new letter to the list
			//characterList.add(new CharacterData(letter, row, col));
			System.out.println("letter selected:"+characterList.size());
			this.letter = letter;
			if(this.rowCur == -1 && this.colCur == -1)
			{ // means that is the first letter so we need to save the row and col
				this.rowCur = row;
				this.colCur = col;
				setChanged();
				notifyObservers("clear");
			}
			setChanged();
			notifyObservers(this.letter);
		}
		public void confirmSelected() {
			this.confirm = "confirmed";
			setChanged();
			notifyObservers(this.confirm);
		}

		public String getHelp() {
			return help;
		}
		public String getLetter() {
			return letter;
		}

		public String getConfirm() {
			return confirm;
		}
		public String getWordSelected() {
			// transfer the word from the list to the actual word
			if (characterList.size()==0)
				return "";
			// sort the list according to the row and col
			if(getWordDirection().equals("horizontal"))
				characterList.sort(Comparator.comparing(CharacterData::getColumn));
			else if(getWordDirection().equals("vertical"))
			{
				characterList.sort(Comparator.comparing(CharacterData::getRow));
			}
			else{
				// not continuous word
				//characterList.clear();
				return "";
			}
			for (CharacterData ch : characterList) {
				wordSelected += ch.getLetter();
			}
			return wordSelected;
		}
		public String getRow() {
			if(characterList.size()==0)
				return "";
			else
				row=characterList.get(0).getRow();
			rowCur=-1;
			return ""+row;
		}
		public String getCol() {
			if(characterList.size()==0)
				return "";
			else
				col=characterList.get(0).getColumn();
			colCur=-1;
			return ""+col;
		}

		public byte[][] getBonus() {
			return board.getBonus();
		}


	public void restart() {
		// function that restarts the model variables of the current state of the board
		characterList.clear();
		wordSelected="";
		row=-1; col=-1;
		rowCur=-1; colCur=-1;

	}

	private static boolean isHorizontalWord(ArrayList<CharacterData> characterList) {
		// return true if the word is horizontal
		int row = characterList.get(0).getRow();
		for (CharacterData data : characterList) {
			if (data.getRow() != row) {
				return false;
			}
		}
		return true;
	}

	private static boolean isVerticalWord(ArrayList<CharacterData> characterList) {
		// return true if the word is vertical
		int column = characterList.get(0).getColumn();
		for (CharacterData data : characterList) {
			if (data.getColumn() != column) {
				return false;
			}
		}
		return true;
	}

	private static boolean isContinuous(ArrayList<CharacterData> characterList, boolean direction) {
		// param: direction - true if the word is horizontal, false if the word is vertical
		if (direction) {
			// word is horizontal so we need to check if the letters are continuous horizontally
			characterList.sort(Comparator.comparingInt(CharacterData::getRow));
			int previousColumn = characterList.get(0).getColumn();

			for (int i = 1; i < characterList.size(); i++) {
				int currentColumn = characterList.get(i).getColumn();

				if (currentColumn != previousColumn + 1) {
					return false;
				}
				previousColumn = currentColumn;
			}
		} else {
			// word is vertical so we need to check if the letters are continuous vertically
			characterList.sort(Comparator.comparingInt(CharacterData::getColumn));
			int previousRow = characterList.get(0).getRow();
			for (int i = 1; i < characterList.size(); i++) {
				int currentRow = characterList.get(i).getRow();
				if (currentRow != previousRow + 1) {
					return false;
				}
				previousRow = currentRow;
			}
		}
		return true;
	}

	public String getWordDirection() {
			// return the direction of the word
			if(isHorizontalWord(characterList)) {
				if(isContinuous(characterList, true)) {
					return "horizontal";
				}
			} else if(isVerticalWord(characterList)) {
				if (isContinuous(characterList, false)) {
					return "vertical";
				}
			}
		return "not accepted";

	}

	public void cleanList() {
		// function that cleans the list of the characters of the word
		wordSelected="";
		characterList.clear();
	}
}

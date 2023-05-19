package model;
import java.util.*;

import test.Board;
import test.CharacterData;
import test.Tile;
import test.Word;

public class Model extends Observable {
	private String boardState, help, confirm;
	private char letter;
	private Board board;
	private HashMap<Character, Integer> letterScores = new HashMap<>();
	private ArrayList<CharacterData> characterList = new ArrayList<>();
	private Vector<Tile> wordTiles = new Vector<>();
	private int rowCur = -1, colCur = -1;
	String wordSelected="";

	public Model() {
		board = new Board();
		assignLetterScores();
		this.boardState = "";
	}

	private void assignLetterScores() {
		letterScores.put('A', 1);
		letterScores.put('B', 3);
		letterScores.put('C', 3);
		letterScores.put('D', 2);
		letterScores.put('E',1);
		letterScores.put('G',2);
		letterScores.put('H',4);
		letterScores.put('I',1);
		letterScores.put('J',8);
		letterScores.put('K',5);
		letterScores.put('L',1);
		letterScores.put('M',3);
		letterScores.put('N',1);
		letterScores.put('O',1);
		letterScores.put('P',3);
		letterScores.put('Q',10);
		letterScores.put('R',1);
		letterScores.put('S',1);
		letterScores.put('T',1);
		letterScores.put('U',1);
		letterScores.put('V',4);
		letterScores.put('W',4);
		letterScores.put('X',8);
		letterScores.put('Y',4);
		letterScores.put('Z',10);
		letterScores.put('F',4);
	}

	public void updateBoardState(String newBoardState) {
		this.boardState = newBoardState;
		setChanged();
		notifyObservers(this.boardState);
	}

	public void applyString(String s) {
		help = s;
		setChanged();
		notifyObservers("help");
	}

	public void addLetter(CharacterData cd) {
		characterList.stream()
				.filter(c -> c.compareIndex(cd))
				.findFirst()
				.ifPresentOrElse(
						existingCharacter -> existingCharacter.setLetter(cd.getLetter()),
						() -> characterList.add(cd)
				);
	}

	public void letterSelected(char letter, int row, int col) {
		addLetter(new CharacterData(letter, row, col));
		System.out.println(characterList);
		this.letter = letter;
		if(this.rowCur == -1 && this.colCur == -1) {
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
	public char getLetter() {
		return letter;
	}
	public String getConfirm() {
		return confirm;
	}
	public String getRow() {
		if(characterList.size() != 0) {
			return String.valueOf(characterList.get(0).getRow());
		}
		return "";
	}
	public String getCol() {
		if(characterList.size() != 0) {
			return String.valueOf(characterList.get(0).getColumn());
		}
		return "";
	}
	public byte[][] getBonus() {
		return board.getBonus();
	}

	public void cleanList() {
		characterList.clear();
		wordTiles.clear();
	}


	public void undoSelected() {
		if(characterList.size() > 0) {
			characterList.remove(characterList.size() - 1);
		}
		setChanged();
		notifyObservers("undo");
	}

	public String getWordSelected() {
		// transfer the word from the list to the actual word
		if (characterList.size()==0)
			return "";

		// checck if the list is horizontal or vertical
		if(getWordDirection().equals("horizontal"))
		{
			for (int i=0; i<characterList.size()-1; i++)
			{
				CharacterData ch = characterList.get(i); // get the i item in the list
				CharacterData ch1 = characterList.get(i+1); // get the i+1 item in the list
				Tile t = new Tile(ch.getLetter(), letterScores.get(ch.getLetter()));
				wordTiles.add(t);
				wordSelected += String.valueOf(ch.getLetter());
				if (ch.getColumn()+1 != ch1.getColumn())
				{
					wordTiles.add(null); // means that there is an already existing letter in the board in the word
				}
			}
			CharacterData ch = characterList.get(characterList.size()-1); // get the last item in the list
			Tile t = new Tile(ch.getLetter(), letterScores.get(ch.getLetter()));
			wordTiles.add(t);
			wordSelected += String.valueOf(ch.getLetter());

		}
		else if(getWordDirection().equals("vertical"))
		{
			for (int i=0; i<characterList.size()-1; i++)
			{
				CharacterData ch = characterList.get(i); // get the i item in the list
				CharacterData ch1 = characterList.get(i+1); // get the i+1 item in the list
				Tile t = new Tile(ch.getLetter(), letterScores.get(ch.getLetter()));
				wordTiles.add(t);
				wordSelected += String.valueOf(ch.getLetter());
				if (ch.getRow()+1 != ch1.getRow())
				{
					wordTiles.add(null); // means that there is an already existing letter in the board in the word
				}
			}
			CharacterData ch = characterList.get(characterList.size()-1); // get the last item in the list
			Tile t = new Tile(ch.getLetter(), letterScores.get(ch.getLetter()));
			wordTiles.add(t);
			wordSelected += String.valueOf(ch.getLetter());
		}
		else{
			// not continuous word
			return "";
		}
		System.out.println(wordTiles);

		Tile[] array = new Tile[wordTiles.size()];
		wordTiles.toArray(array);
		Word word = new Word(array, characterList.get(0).getRow(), characterList.get(0).getColumn(), isVerticalWord(characterList));
		int score = board.tryPlaceWord(word);// if score is 0 then the word is not valid
		if (score==0) {
			for(int i=0; i<wordTiles.size(); i++)
			{
				setChanged();
				notifyObservers("undo");
			}
		}
		board.print();

		return wordSelected;
	}

	public void restart() {
		// function that restarts the model variables of the current state of the board
		System.out.println("New Game");
		characterList.clear();
		wordSelected="";
		rowCur=-1; colCur=-1;
		this.board = new Board();

	}

	private static boolean isHorizontalWord(ArrayList<CharacterData> characterList) {
		// return true if the word is horizontal
		characterList.sort(Comparator.comparing(CharacterData::getColumn));
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
		characterList.sort(Comparator.comparing(CharacterData::getRow));
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
				//if(isContinuous(characterList, true)) {
					return "horizontal";
				//}
			} else if(isVerticalWord(characterList)) {
				//if (isContinuous(characterList, false)) {
					return "vertical";
				//}
			}
		return "Illegal";

	}




}

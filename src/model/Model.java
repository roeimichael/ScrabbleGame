package model;
import java.util.*;
import test.*;

public class Model extends Observable {
	private String boardState, help, confirm;
	private char letter;

	private HashMap<Character, Integer> letterScores = new HashMap<>(); // temporary saves the letter and its score
	private ArrayList<CharacterData> characterList = new ArrayList<>(); // saves the letters the user has selected to put on the board in the current turn
	private Vector<Tile> wordTiles = new Vector<>(); // saves the tiles that are part of the word the user has selected
	private int rowCur = -1, colCur = -1;
	String wordSelected=""; // saves the word the user has selected

//	if(isHost)
//	{
	private static GameManager gameManager;
	private Board board;
//	}

	public Model() {
		gameManager = new GameManager();
		gameManager.addPlayer(new Player(1));
		gameManager.addPlayer(new Player(2));
		gameManager.restartGame();
		board = gameManager.getBoard();
		assignLetterScores();
		this.boardState = "";
	}

	private void assignLetterScores() {
		letterScores.put('A', 1);
		letterScores.put('B', 3);
		letterScores.put('C', 3);
		letterScores.put('D', 2);
		letterScores.put('E',1);
		letterScores.put('F',4);
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
		this.rowCur = -1;
		this.colCur = -1;
		setChanged();
		notifyObservers(this.confirm);
	}
	public void passSelected() {
		gameManager.passTurn();
		setChanged();
		notifyObservers("pass");


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
		return gameManager.getBonusBoard();
	}

	public void cleanList() {
		characterList.clear();
		wordTiles.clear();
		gameManager.refillBag();
	}


	public void undoSelected() {
		if(characterList.size() > 0) {
			characterList.remove(characterList.size() - 1);
		}
		setChanged();
		notifyObservers("undo");
	}

	public String getWordSelected() {
		// transform the word from characterList to actual word
		if (characterList.size()==0)
			// now word selected
			return "";

		// checck if the list is horizontal or vertical
		// then add the letters to the wordSelected
		if(getWordDirection().equals("horizontal"))
		{
			for (int i=0; i<characterList.size()-1; i++) {
				CharacterData ch = characterList.get(i); // get the i item in the list
				CharacterData ch1 = characterList.get(i + 1); // get the i+1 item in the list
				Tile t = new Tile(ch.getLetter(), letterScores.get(ch.getLetter()));
				wordTiles.add(t);
				wordSelected += String.valueOf(ch.getLetter());
				if (ch.getColumn() + 1 != ch1.getColumn()) {
					int numOfNulls = ch1.getColumn() - ch.getColumn() - 1;
					for (int j = 0; j < numOfNulls; j++)
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
					int numOfNulls = ch1.getRow() - ch.getRow() - 1;
					for (int j=0; j<numOfNulls; j++)
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
		// need to make a tile array from the wordTiles
		Tile[] array = new Tile[wordTiles.size()];
		wordTiles.toArray(array);
		Word word = new Word(array, characterList.get(0).getRow(), characterList.get(0).getColumn(), isVerticalWord(characterList));

		int score =gameManager.placeWord(word);// if score is 0 then the word is not valid
		if (score==0) {
			for(int i=0; i<wordTiles.size(); i++)
			{
				setChanged();
				notifyObservers("undo");
			}
		}
		else {
			gameManager.endTurn(score,word);
		}
		gameManager.printBoard();
		gameManager.printScores();
		return wordSelected;
	}

	public void restart() {
		System.out.println("New Game");
		characterList.clear();
		wordSelected="";
		rowCur=-1; colCur=-1;
		gameManager.restartGame();
		setChanged();
		notifyObservers("started");
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

//	private static boolean isContinuous(ArrayList<CharacterData> characterList, boolean direction) {
//		// param: direction - true if the word is horizontal, false if the word is vertical
//		if (direction) {
//			// word is horizontal so we need to check if the letters are continuous horizontally
//			characterList.sort(Comparator.comparingInt(CharacterData::getRow));
//			int previousColumn = characterList.get(0).getColumn();
//
//			for (int i = 1; i < characterList.size(); i++) {
//				int currentColumn = characterList.get(i).getColumn();
//
//				if (currentColumn != previousColumn + 1) {
//					return false;
//				}
//				previousColumn = currentColumn;
//			}
//		} else {
//			// word is vertical so we need to check if the letters are continuous vertically
//			characterList.sort(Comparator.comparingInt(CharacterData::getColumn));
//			int previousRow = characterList.get(0).getRow();
//			for (int i = 1; i < characterList.size(); i++) {
//				int currentRow = characterList.get(i).getRow();
//				if (currentRow != previousRow + 1) {
//					return false;
//				}
//				previousRow = currentRow;
//			}
//		}
//		return true;
//	}

	public String getWordDirection() {
			// return the direction of the word
			if(isHorizontalWord(characterList)) {
					return "horizontal";

			} else if(isVerticalWord(characterList)) {
					return "vertical";

			}
		return "Illegal";

	}


	public Player getCurrentPlayer() {
		return gameManager.getCurrentPlayer();
	}

	public String getTilesLeft() {
		return ""+gameManager.getTilesLeftInBag();
	}


}

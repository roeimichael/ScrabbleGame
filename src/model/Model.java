package model;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

import test.*;

public class Model extends Observable {
	// client attributes
	private int id;
	private String ip;
	private int port;
	private Socket client;
	private BufferedReader in;
	private PrintWriter out;
	private boolean isHost = false;
	private boolean exit = false;

	// game attributes
	private String letterList;
	private ArrayList<String> newLetterList = new ArrayList<>();
	private static GameManager gameManager;
	private String[][] updatedBoard;
	// labels
	private String boardState, help, confirm;
	private char letter;
	private int rowCur = -1, colCur = -1;
	String wordSelected=""; // saves the word the user has selected

	// helper attributes
	private HashMap<Character, Integer> letterScores = new HashMap<>(); // temporary saves the letter and its score
	private ArrayList<CharacterData> characterList = new ArrayList<>(); // saves the letters the user has selected to put on the board in the current turn
	private ArrayList<CharacterData> lastEntry = new ArrayList<>(); // saves the letters the user has selected int his previous turn in order to undo in a later turn
	private Vector<Tile> wordTiles = new Vector<>(); // saves the tiles that are part of the word the user has selected


//	}

	public Model(String ip, int port) {
//		gameManager = new GameManager();
//		gameManager.addPlayer(new Player(1));
//		gameManager.addPlayer(new Player(2));
//		gameManager.restartGame();
//		board = gameManager.getBoard();
		assignLetterScores();
		this.boardState = "";
		this.ip = ip;
		this.port = port;
		updatedBoard = new String[15][15];

	}
	public void connectToServer() {
		try {
			System.out.println("Connecting to server...");
			client = new Socket("127.0.0.1", 9999);
			out = new PrintWriter(client.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));

			// first step: when the client connects to the server, the server sends him his id
			this.id = Integer.parseInt(in.readLine());
			System.out.println("Connected to server, client " + id);
			isHost = id == 0;

			// second step: sending it back to the playerHandler
			out.println(id);
			String msgFromServer = null;
			Thread getMsgFromServer = new Thread(()->{ // listen to the server, and acts accordingly
				while(true)
				{

					//communication protocol: newServer -> model -> playerHandler
					// 1. server sends message type
					// 2. model sends message to playehandler requesting the content
					// 3. playerhandler sends content to player

					try {
						String msg = in.readLine();
						System.out.println("msg from server: " + msg);
						switch (msg) {

							// protocol: server -> model -> playerHandler

							case protocols.START_GAME:
								out.println(protocols.NEW_GAME);
								break;

							case protocols.BOARD_CHANGED:
								//from server: notification that the board is changed
								// to playehandler: get the updated board from the server
								// gets the updated board from the playerhandler
								//this.updateBoard();
								out.println(protocols.GET_BOARD);
								break;
							case protocols.HAND_CHANGED://hand sent from server

								break;

							// protocol: playerHandler -> model
							case protocols.NEW_GAME: // playerHandler sends the player's hand
								msg = in.readLine(); // now msg includes the hand
								this.updateHand(msg);

								this.setGameStarted();
								break;
							case protocols.END_GAME://message for game end sent from server
								exit = true;
								break;
							case protocols.GET_SCORE://message containing score from server
								msg = in.readLine();
								this.updateScore(msg);
								break;
							case protocols.GET_HAND://message containing hand from server
								msg = in.readLine();
								this.updateHand(msg);
								break;
							case protocols.GET_BOARD://message containing board from server
								msg = in.readLine();
								System.out.println("msg from server in get_board: " + msg);
								this.updateBoard(msg);
								break;

						}

					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			});
			getMsgFromServer.start();
		} catch (IOException e) {
			System.out.println(e);
			System.out.println("Server is not running");

		}


	}




	// update methods that change the view
	private void updateBoard(String msg) {
		// msg is 15 strings, each string is 15 characters combined into one string
		// we translate it to a 2d array of 15X15
		updatedBoard = new String[15][15];
		for(int i=0; i<15; i++)
		{
			for(int j=0; j<15; j++)
			{
				updatedBoard[i][j] = msg.substring(i*15+j, i*15+j+1);
			}
		}
		setChanged();
		notifyObservers(protocols.BOARD_CHANGED);

	}
	private void updateHand(String msg) {
		letterList=msg;
	}
	private void updateScore(String msg) {
	}

	public void setGameStarted() {
		setChanged();
		notifyObservers(protocols.START_GAME);

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
		// need to check if game is over
		this.confirm = "confirmed";
		this.rowCur = -1;
		this.colCur = -1;
		setChanged();
		notifyObservers(this.confirm);
	}
	public void passSelected() {
		// need to check if game is over

		gameManager.passTurn();
		//gameManager.isGameOver();
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
		//gameManager.refillBag();
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
		wordSelected = "";
		System.out.println(" characterList: " + characterList.toString());

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
				wordSelected = "";
			}
		}
		else {
			lastEntry = new ArrayList<>(characterList);
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
		notifyObservers("restart");
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
		out.println(protocols.GET_CURRENT_PLAYER);
		return gameManager.getCurrentPlayer();
	}

	public String getTilesLeft() {
		return "Tiles left in the bag: "+gameManager.getTilesLeftInBag();
	}
	public String getPlayerScore() {
		return gameManager.getScores();
	}


	public boolean challenge() {
		//********** this method is not complete yet **********
		// need to make it possible to run only once every turn
		//*****************************************************
		// returns true if the word doesnt exist
		// returns false if the word does exist
		System.out.println("character list: "+characterList.toString());
		System.out.println("last entry: "+lastEntry.toString());
		if(gameManager.challenge())
		{
			// need to update GUI board accordingly
			for(int i=0; i<lastEntry.size(); i++)
			{
				setChanged();
				notifyObservers("challenge accepted");
				wordSelected = "";
				gameManager.printBoard();

			}


			return true;
		}
		else
		{
			return false;

		}
	}

	public String getTurn() {
		return "Player "+getCurrentPlayer().getId() + "'s turn";
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

	public String getNumPlayers() {
		gameManager = GameManager.get();
		return ""+gameManager.getNumPlayers();
	}

	public List<String> getLetterList() {

		return new ArrayList<>(Arrays.asList(letterList.split(",")));

	}

	public String[][] getUpdateBoard() {
		return this.updatedBoard;
	}
}

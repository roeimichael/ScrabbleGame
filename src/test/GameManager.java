package test;

import model.ScrabblePlayer;
import server.Client;
import server.ConnectionHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Observable;

public class GameManager extends Observable {
    private static GameManager instance = null;
    private Board board; // the current board state
    private Board lastTurnBoard; // the board state after the last turn
    //private ArrayList<Player> players;
    //public static ArrayList<ScrabblePlayer> players;
    public static ArrayList<ConnectionHandler> players;
    private int currentPlayerIndex; // index of the current player's turn
    private Tile.Bag tileBag; // the bag of tiles for the game
    private BookScrabbleHandler bookScrabbleHandler;// the bookscrabble handler will be used to check if a word is legal
    private int lastScore; // the score of the last word placed
    private int numPassed; // number of players who have passed their turn

    public GameManager() {
        this.board =  new Board();
        players = new ArrayList<>();
        //connections = new ArrayList<>();
        this.currentPlayerIndex = 0;
        this.tileBag =  new Tile.Bag();
        this.numPassed=0;
    }

    public static GameManager getInstance() {
        if(instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public byte[][] getBonusBoard(){
        return board.getBonus();
    }
    public void updateBoard(Board board) {
        this.board = board;
    }
//    public void addPlayer(Player player) {
//        players.add(player);
//    }
//    public void addPlayer(ScrabblePlayer player) {
//        players.add(player);
//        for(int i=0; i<players.size();i++)
//            System.out.println(players.get(i));
//    }
    public void addPlayer(ConnectionHandler player) {
        players.add(player);
        for(int i=0; i<players.size();i++)
            System.out.println(i+":"+players.get(i));
    }
    public void removePlayer(Player player) {
        players.remove(player);
    }
    public int getNumPlayers() {
        return players.size();
    }
    public String getScores()
    {
        String scores="";
        for(ConnectionHandler p:players)
        {
            scores+="Player "+p.getId()+" score: "+p.getScore()+"\n";
        }
        return scores;
    }
    public void restartGame(){

        board = new Board();
        tileBag = new Tile.Bag();
        lastScore = 0;
        for (ConnectionHandler player : players) {
            player.removeTiles();
            player.resetScore();
            player.refillBag(tileBag);
            System.out.println("Player " + player.getId() + player.gethand());
            setChanged();
            notifyObservers("restart");
        }
    }

    public void joinGame(ConnectionHandler player) {
        players.add(player);
        player.refillBag(tileBag);
        System.out.println("Player " + player.getId() + " has joined the game");
        setChanged();
        notifyObservers("join");
    }



    public int placeWord(Word word) {
        // need to check if the word is legal
        // if it is legal then place the word and return true
        lastTurnBoard = new Board(board);
        int score=0;
        score = board.tryPlaceWord(word);
        if(score == 0){
            board = lastTurnBoard;
        }
        lastScore = score;
        return score;
    }
    public void endTurn(int score, Word word){
        lastScore = score;
        players.get(currentPlayerIndex).incrementScore(score);
        players.get(currentPlayerIndex).removeWord(word);
        players.get(currentPlayerIndex).refillBag(tileBag);
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public void passTurn(){
        System.out.println("Player " + players.get(currentPlayerIndex).getId() + " has passed their turn");
        lastScore=0;
        numPassed++;
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }
    public ConnectionHandler getCurrentPlayer() {
        System.out.println("Current player is " + players.get(currentPlayerIndex).getId());
        return players.get(currentPlayerIndex);
    }
    public ConnectionHandler getNextPlayer() {
        return players.get((currentPlayerIndex + 1) % players.size());
    }

    public Board getBoard() {
        return board;
    }

    public boolean isGameOver()
    { // need to check if any of the players have a word to place
        if(numPassed == players.size())
            return true;
        if(tileBag.size()==0)
            return true;
        for(ConnectionHandler p:players)
        {
            if(p.gethand().size()==0)
                return true;

        }
        return false;
    }



    private ConnectionHandler determineWinner() {
        ConnectionHandler winner = players.get(0);
        for (ConnectionHandler player : players) {
            if (player.getScore() > winner.getScore()) {
                winner = player;
            }
        }
        return winner;
    }

    public void refillBag()
    {
        players.get(currentPlayerIndex).refillBag(tileBag);
    }
    public void printScores()
    {
        System.out.println("**************************SCORES*********************************");

        for(ConnectionHandler p:players)
        {
            System.out.println("Player " + p.getId() + " has a score of " + p.getScore());
        }
        System.out.println("***********************************************************");

    }
    public void printBoard()
    {
        board.print();
    }


//    public void runGame()
//    { // main function that runs a scrabble game
//        restartGame();
//        while(!isGameOver())
//        {
//            // get the current player
//            ConnectionHandler currentPlayer = getCurrentPlayer();
//            board.print();
//            if(currentPlayer.choice()==1)
//            {
//                Word word = currentPlayer.getWord();
//                while(placeWord(word)==0)
//                {
//                    word = currentPlayer.getWord();
//                }
//                numPassed=0;
//            }
//            else
//            {
//                numPassed++;
//                if(numPassed==players.size())
//                {
//                    // if all players pass then the game is over
//                    break;
//                }
//            }
//            printScores();
//        }
//        ScrabblePlayer winner = determineWinner();
//        System.out.println("The winner is player " + winner.getId() + " with a score of " + winner.getScore());
//    }

    public int getTilesLeftInBag() {
        return tileBag.size();
    }

    public boolean challenge() {
        // returns true if the word doesnt exist
        // returns false if the word does exist
        if(board.challenge()) // word doesnt exist
        {
            // challenger loses his turn due to unsuccessful challenge
            players.get((currentPlayerIndex + players.size() - 1) % players.size()).incrementScore(-lastScore);
            board = lastTurnBoard;
            return true;
        }
        else // word does exist
        {
            // last player get a score of 0 for his last turn
            // i think that the player won't get back his tiles and will just get new ones instead
            // need to update the board accordingly
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            return false;

        }
    }

    public String getNumPlayersConnected() {
        return ""+players.size();
    }

    public String toString() {
        String s = "";
        for (ConnectionHandler player : players) {
            s += player.toString() + "\n";
        }
        return s;
    }
}
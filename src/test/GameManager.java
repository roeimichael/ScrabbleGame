package test;

import java.util.ArrayList;

public class GameManager {

    private Board board;
    private ArrayList<Player> players;
    private int currentPlayerIndex; // index of the current player's turn
    private Tile.Bag tileBag; // the bag of tiles for the game

    private int numPassed; // number of players who have passed their turn
    public GameManager() {
        this.board =  new Board();
        this.players = new ArrayList<>();
        this.currentPlayerIndex = 0;
        this.tileBag =  new Tile.Bag();
        this.numPassed=0;
    }
    public byte[][] getBonusBoard(){
        return board.getBonus();
    }
    public void updateBoard(Board board) {
        this.board = board;
    }
    public void addPlayer(Player player) {
        players.add(player);
    }
    public void removePlayer(Player player) {
        players.remove(player);
    }
    public void startGame() {
        for (Player player : players) {
            player.refillBag(tileBag);
        }
    }
    public void restartGame(){
        board = new Board();
        tileBag = new Tile.Bag();
        for (Player player : players) {
            player.resetScore();
            player.refillBag(tileBag);
        }
    }


    public int placeWord( Word word) {
        // need to check if the word is legal
        // if it is legal then place the word and return true
        int score=0;
        score = board.tryPlaceWord(word);
        return score;
    }
    public void endTurn(int score, Word word){
        players.get(currentPlayerIndex).incrementScore(score);
        players.get(currentPlayerIndex).removeWord(word);
        players.get(currentPlayerIndex).refillBag(tileBag);
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public void passTurn(){
        System.out.println("Player " + players.get(currentPlayerIndex).getId() + " has passed their turn");
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public Board getBoard() {
        return board;
    }

    private boolean isGameOver()
    { // need to check if any of the players have a word to place
        if(numPassed == (2*players.size()))
            return true;
        if(tileBag.size()==0)
            return true;
        for(Player p:players)
        {
            if(p.gethand().size()==0)
                return true;

        }
        return false;
    }



    private Player determineWinner() {
        Player winner = players.get(0);
        for (Player player : players) {
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

        for(Player p:players)
        {
            System.out.println("Player " + p.getId() + " has a score of " + p.getScore());
        }
        System.out.println("***********************************************************");

    }
    public void printBoard()
    {
        board.print();
    }


    public void runGame()
    { // main function that runs a scrabble game
        restartGame();
        while(!isGameOver())
        {
            // get the current player
            Player currentPlayer = getCurrentPlayer();
            board.print();
            if(currentPlayer.choice()==1)
            {
                Word word = currentPlayer.getWord();
                while(placeWord(word)==0)
                {
                    word = currentPlayer.getWord();
                }
                numPassed=0;
            }
            else
            {
                numPassed++;
                if(numPassed==players.size())
                {
                    // if all players pass then the game is over
                    break;
                }
            }
            printScores();
        }
        Player winner = determineWinner();
        System.out.println("The winner is player " + winner.getId() + " with a score of " + winner.getScore());
    }

    public int getTilesLeftInBag() {
        return tileBag.size();
    }
}
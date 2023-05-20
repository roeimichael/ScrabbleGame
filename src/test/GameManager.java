package test;

import java.util.ArrayList;

public class GameManager {

    private Board board;
    private ArrayList<Player> players;
    private int currentPlayerIndex; // index of the current player's turn
    private Tile.Bag tileBag; // the bag of tiles for the game

    public GameManager() {
        this.board =  new Board();
        this.players = new ArrayList<>();
        this.currentPlayerIndex = 0;
        this.tileBag =  new Tile.Bag();
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

    public void nextTurn() {
        players.get(currentPlayerIndex).refillBag(tileBag);
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public boolean placeWord( Word word) {
        // need to check if the word is legal
        // if it is legal then place the word and return true
        if (board.boardLegal(word)) {
            int score = board.tryPlaceWord(word);
            if (score != 0) {
                players.get(currentPlayerIndex).incrementScore(score);
                players.get(currentPlayerIndex).removeWord(word);
                nextTurn();
                return true;
            }
        }
        return false;
    }
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public Board getBoard() {
        return board;
    }

    private boolean isGameOver()
    { // need to check if any of the players have a word to place
        for(Player p:players)
        {
            if(p.gethand().size()==0)
                return true;
        }
        return false;
    }

    public void runGame()
    {
        restartGame();

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

    public void refillBag() {
        players.get(currentPlayerIndex).refillBag(tileBag);
    }
}
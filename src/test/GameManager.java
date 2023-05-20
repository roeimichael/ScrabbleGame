package test;

import java.util.ArrayList;

public class GameManager {

    private Board board;
    private ArrayList<Player> players;
    private int currentPlayerIndex;
    private Tile.Bag tileBag;

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
        Player currentPlayer = players.get(currentPlayerIndex);
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        currentPlayer = players.get(currentPlayerIndex);
        currentPlayer.refillBag(tileBag);
    }

    public boolean placeWord(Player player, Word word) {
        if (board.boardLegal(word)) {
            int score = board.tryPlaceWord(word);
            if (score != 0) {
                player.incrementScore(score);
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
}
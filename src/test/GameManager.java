package test;

import java.util.ArrayList;

public class GameManager {

    private Board board;
    private ArrayList<Player> players;
    private int currentPlayerIndex;
    private Tile.Bag tileBag;

    public GameManager(Board board, ArrayList<Player> players, Tile.Bag tileBag) {
        this.board = board;
        this.players = players;
        this.currentPlayerIndex = 0;
        this.tileBag = tileBag;

    }

    public void nextTurn() {
        Player currentPlayer = players.get(currentPlayerIndex);
        currentPlayer.setTurn(false);

        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        currentPlayer = players.get(currentPlayerIndex);
        currentPlayer.refillBag(tileBag);

        currentPlayer.setTurn(true);
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

    // Other game related methods...
}

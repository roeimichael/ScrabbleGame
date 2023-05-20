package test;

import java.util.ArrayList;

public class Player {

    private int id;
    private int score;
    private ArrayList<Tile> hand;
    private boolean isTurn;

    public Player(int id) {
        this.id = id;
        this.score = 0;
        this.hand = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public int getScore() {
        return score;
    }

    public void incrementScore(int score) {
        this.score += score;
    }

    public ArrayList<Tile> gethand() {
        return hand;
    }

    public void refillBag(Tile.Bag tileBag) {
        while (hand.size() < 7) {
            Tile tile = tileBag.getRand();
            if (tile != null) {
                hand.add(tile);
            } else {
                break; // If no more tiles left in the bag
            }
        }
    }

    public void resetScore() {
        score = 0;
    }
}
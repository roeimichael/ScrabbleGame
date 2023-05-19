package test;

import java.util.ArrayList;

public class Player {

    private int id;
    private int score;
    private ArrayList<Tile> bag;
    private boolean isTurn;

    public Player(int id, Tile.Bag tileBag) {
        this.id = id;
        this.score = 0;
        this.bag = new ArrayList<>();
        this.isTurn = false;
        fillBag(tileBag);
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

    public ArrayList<Tile> getBag() {
        return bag;
    }

    public boolean isTurn() {
        return isTurn;
    }

    public void setTurn(boolean isTurn) {
        this.isTurn = isTurn;
    }
    private void fillBag(Tile.Bag tileBag) {
        for (int i = 0; i < 7; i++) {
            Tile tile = tileBag.getRand();
            if (tile != null) {
                bag.add(tile);
            }
        }
    }

    public void refillBag(Tile.Bag tileBag) {
        while (bag.size() < 7) {
            Tile tile = tileBag.getRand();
            if (tile != null) {
                bag.add(tile);
            } else {
                break; // If no more tiles left in the bag
            }
        }
    }
}
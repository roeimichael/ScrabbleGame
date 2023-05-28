package realTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;
import test.*;

class PlayerTest {
    private Player player;
    private Tile.Bag tileBag;

    @BeforeEach
    void setUp() {
        player = new Player(1);
        tileBag = new Tile.Bag();

    }

    @Test
    void testIncrementScore() {
        player.incrementScore(10);
        assertEquals(10, player.getScore());
    }

    @Test
    void testRemoveTiles() {
        player.gethand().add(new Tile('L',2)); // assuming a Tile class exists
        player.removeTiles();
        assertEquals(0, player.gethand().size());
    }

    @Test
    void testResetScore() {
        player.incrementScore(10);
        player.resetScore();
        assertEquals(0, player.getScore());
    }
    @Test
    void testRefillBag() {
        player.refillBag(tileBag);
        assertEquals(7, player.gethand().size());
    }

    @Test
    void testRefillBagWithEmptyBag() {
        tileBag = new Tile.Bag();
        player.refillBag(tileBag);
        assertEquals(7, player.gethand().size());
    }

    @Test
    void testRemoveWord() {
        player.gethand().add(new Tile('H', 4));
        player.gethand().add(new Tile('E', 1));
        player.gethand().add(new Tile('L', 1));
        player.gethand().add(new Tile('L', 1));
        player.gethand().add(new Tile('O', 1));

        Word word = new Word(player.gethand().toArray(new Tile[0]), 0, 0, false);
        player.removeWord(word);
        assertEquals(0, player.gethand().size());
    }

    @Test
    void testRemoveWordNotInHand() {
        player.gethand().add(new Tile('H', 4));
        player.gethand().add(new Tile('E', 1));

        Word word = new Word(new Tile[]{ new Tile('L', 1), new Tile('O', 1) }, 0, 0, false);
        player.removeWord(word);
        assertEquals(2, player.gethand().size());
    }

    @Test
    void testId() {
        assertEquals(1, player.getId());
    }
}

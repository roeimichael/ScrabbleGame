package realTest;
import test.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class GameManagerTest {
    private GameManager gameManager;
    private Player player1;
    private Player player2;
    @BeforeEach
    void setUp() {
        gameManager = new GameManager();
        player1 = new Player(1);
        player2 = new Player(2);
    }

    @Test
    void testAddRemovePlayer() {
        Player mockPlayer = new Player(1);
        gameManager.addPlayer(mockPlayer);
        assertEquals(1, gameManager.getNumPlayers());
        gameManager.removePlayer(mockPlayer);
        assertEquals(0, gameManager.getNumPlayers());
    }

    @Test
    void testGetScores() {
        gameManager.addPlayer(player1);
        gameManager.addPlayer(player2);
        player1.incrementScore(10);
        player2.incrementScore(20);
        String scores = gameManager.getScores();
        assertTrue(scores.contains("Player 1 score: 10"));
        assertTrue(scores.contains("Player 2 score: 20"));
    }

    @Test
    void testEndTurn() {
        gameManager.addPlayer(player1);
        gameManager.addPlayer(player2);
        Word mockWord = new Word(new Tile[]{new Tile('H', 4)}, 0, 0, false);
        gameManager.endTurn(10, mockWord);
        assertEquals(player2, gameManager.getCurrentPlayer());
    }

    @Test
    void testPassTurn() {
        gameManager.addPlayer(player1);
        gameManager.addPlayer(player2);
        gameManager.passTurn();
        assertEquals(player2, gameManager.getCurrentPlayer());
    }

    @Test
    void testDetermineWinner() {
        gameManager.addPlayer(player1);
        gameManager.addPlayer(player2);
        player1.incrementScore(10);
        player2.incrementScore(20);
        assertEquals(player2, gameManager.determineWinner());
    }

    @Test
    void testRefillBag() {
        gameManager.addPlayer(player1);
        player1.gethand().add(new Tile('H', 4));
        gameManager.refillBag();
        assertEquals(7, player1.gethand().size());
    }

    @Test
    void testGetTilesLeftInBag() {
        assertEquals(98, gameManager.getTilesLeftInBag()); // assuming 100 tiles in a new bag
    }

}

package realTest;

import test.GameManager;
import test.Player;

public class GameManagerTest {
    public static void main(String[] args) {
        GameManager g= new GameManager();
        g.addPlayer(new Player(1));
        g.runGame();
    }
}

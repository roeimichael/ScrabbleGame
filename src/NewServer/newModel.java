package NewServer;

import java.util.Observable;

public class newModel extends Observable {
    miniGameManager mgm;
    public newModel(){
        this.mgm=miniGameManager.get();
    }
    public void setGameStarted() {
        this.mgm=miniGameManager.get();
        mgm.startGame();
    }
}

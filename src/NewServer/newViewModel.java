package NewServer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.Observable;
import java.util.Observer;

public class newViewModel implements Observer {
    private newModel model;
    private BooleanProperty gameStartedProperty;
    public newViewModel(newModel model){
        this.model=model;
        gameStartedProperty = new SimpleBooleanProperty(false);
    }
    public BooleanProperty gameStartedProperty() {
        return gameStartedProperty;
    }

    public void setGameStarted(boolean gameStarted) {
        gameStartedProperty.set(gameStarted);
        model.setGameStarted();
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}

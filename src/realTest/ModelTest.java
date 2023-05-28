package realTest;
import test.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import model.*;

public class ModelTest {

    private Model model;

    @Before
    public void setUp() {
        model = new Model();
    }

    @Test
    public void testAssignLetterScores() {
        model.assignLetterScores();
        assertEquals(1, (int)model.letterScores.get('A'));
        assertEquals(3, (int)model.letterScores.get('B'));
        // Add more checks here for other letters
    }

    @Test
    public void testUpdateBoardState() {
        model.updateBoardState("test");
        assertEquals("test", model.boardState);
    }

    @Test
    public void testApplyString() {
        model.applyString("help");
        assertEquals("help", model.getHelp());
    }


    @Test
    public void testLetterSelected() {
        model.letterSelected('B', 0, 1);
        assertEquals('B', model.getLetter());
        assertEquals("0", model.getRow());
        assertEquals("1", model.getCol());
    }

    @Test
    public void testGetWordSelectedHorizontal() {
        model.addLetter(new CharacterData('H', 7, 7));
        model.addLetter(new CharacterData('E', 7, 8));
        model.addLetter(new CharacterData('L', 7, 9));
        model.addLetter(new CharacterData('L', 7, 10));
        model.addLetter(new CharacterData('O', 7, 11));

        String word = model.getWordSelected();
        assertEquals("HELLO", word);
    }

    @Test
    public void testGetWordSelectedVertical() {
        model.addLetter(new CharacterData('H', 7, 7));
        model.addLetter(new CharacterData('E', 8, 7));
        model.addLetter(new CharacterData('L', 9, 7));
        model.addLetter(new CharacterData('L', 10, 7));
        model.addLetter(new CharacterData('O', 11, 7));

        String word = model.getWordSelected();
        assertEquals("HELLO", word);
    }

    @Test
    public void testUndoSelected() {
        model.addLetter(new CharacterData('H', 0, 0));
        model.undoSelected();
        assertEquals(0, model.characterList.size());
    }
}


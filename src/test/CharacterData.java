package test;

public class CharacterData {
    private String character;
    private int row;
    private int column;

    public CharacterData(String character, int row, int column) {
        this.character = character;
        this.row = row;
        this.column = column;
    }

    public String getLetter() {
        return character;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public void setLetter(String letter) {
        this.character = letter;
    }
    public void setRow(int row) {
        this.row = row;
    }
    public void setColumn(int column) {
        this.column = column;
    }


    public boolean compareIndex(CharacterData other)
    { // function that compares the index of two characters
    	return  this.row == other.row && this.column == other.column;
    }
}

package nook;

import java.awt.Color;

public class Item {

    private char glyph;
    private Color color;
    private String name;
    private String description;
    private int scoreValue;
    private int thrownAttackValue;

    public Item(char glyph, Color color, String name, int scoreValue, String description) {
        this.glyph = glyph;
        this.color = color;
        this.name = name;
        this.scoreValue = scoreValue;
        this.description = description;
        thrownAttackValue = 1;
    }

    public String description() {
        return description;
    }
    public char glyph() {
        return glyph;
    }
    public Color color() {
        return color;
    }
    public String name() {
        return name;
    }
    public int scoreValue() { return scoreValue; }
    public int thrownAttackValue() { return thrownAttackValue; }
    public void modifyThrownAttackValue(int amount) { thrownAttackValue += amount; }

}

package nook;

import java.awt.Color;
import asciiPanel.AsciiPanel;

public enum Tile {

	FLOOR('.', new Color(0,0,0), "Walkable ground."),
    FLOOR_DUG('.', new Color(0, 64,0), "Freshly exposed earth."),
    GRASS('"', new Color(0, 64,0), "Fresh squishy grass."),
    NOOK_FLOOR('.', new Color(0,50,0), "Your clean floor."),
	WALL((char)219, AsciiPanel.green, "Mossy marshen wall."),
    WALL_GLOWING((char)219, new Color(56,220, 41), "Glowing marsh wall."),
	DEEPWALL((char)177, AsciiPanel.green,  "Mossy marsh wall."),
    DEEPWALL_GLOWING((char)177, AsciiPanel.brightGreen, "Glowing marsh wall."),
	DEEPERWALL((char)176, AsciiPanel.green,  "Mossy marsh wall."),
    DEEPERWALL_GLOWING((char)176, AsciiPanel.brightGreen, "Glowing marsh wall."),
	BOUNDS('#', AsciiPanel.green, "What exists beyond?"),
    NOOKWALL((char)219, new Color(99, 200, 91), "The walls of your humble Nook."),
	DOOR('/', AsciiPanel.red, "A door."),
	STAIRS_DOWN((char)240, new Color(0, 74, 134), "Some stairs leading down."),
	STAIRS_UP((char)240, AsciiPanel.brightGreen, "Some stairs leading up"),
    WATER('~', new Color(22, 122, 195), "Crisp, fresh water."),
    NOOKWATER('~', new Color(22, 122, 195), "The Stinging Pool."),
    TREE('T', new Color(5, 83, 7), "A ancient petal tree."),
    WINDOW_VERT('|', AsciiPanel.brightWhite, "Your Nook's squeaky clean windows."),
    WINDOW_HORIZ('-', AsciiPanel.brightWhite, "Your Nook's squeaky clean windows."),
	UNKNOWN(' ', AsciiPanel.black, "(unknown)");
	
	private char glyph;
	public char glyph() { return glyph; }
	private String description;
	public String description() {return description;}
	private Color color;
	private Color defaultColor;
	public Color defaultColor() {return  defaultColor;}
	public Color color() { return color; }
	public Color setColor(Color color) { return this.color = color;}
	
	Tile(char glyph, Color color, String description){
		this.glyph = glyph;
		this.color = color;
		this.defaultColor = color;
		this.description = description;
	}

	public boolean isDiggable() {
		return this == Tile.WALL || this == Tile.DEEPWALL || this == Tile.DEEPERWALL || this == Tile.WALL_GLOWING || this == Tile.DEEPWALL_GLOWING || this == Tile.DEEPERWALL_GLOWING;
	}

	public boolean isGround() {
	    return this != WALL && this != DEEPWALL && this != DEEPERWALL && this != BOUNDS && this != NOOKWALL && this != WALL_GLOWING
                && this != DEEPWALL_GLOWING && this != DEEPERWALL_GLOWING;
	}

	public boolean isWalkable() {
	    return this != BOUNDS && this != NOOKWALL && this != WINDOW_HORIZ && this != WINDOW_VERT && this != TREE;
    }

	public boolean isNookWater() {
	    return this == NOOKWATER;
    }

    public boolean isWater() {
        return this == WATER || this == NOOKWATER;
    }

    public boolean isStairs() { return this == STAIRS_UP || this == STAIRS_DOWN; }
}

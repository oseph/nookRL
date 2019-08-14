package nook;


import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class World {

	private Tile[][][] tiles;
	private Item[][][] items;
	private int width;
	private int height;
	private int depth;
	private List<Creature> creatures;

	public World(Tile[][][] tiles){
		this.tiles = tiles;
		this.width = tiles.length;
		this.height = tiles[0].length;
		this.depth = tiles[0][0].length;
		this.creatures = new ArrayList<Creature>();
		this.items = new Item[width][height][depth];
	}

	public int width() { return width; }
	public int height() { return height; }
	public int depth() { return depth; }

	public Creature creature(int x, int y, int z){
		for (Creature c : creatures){
			if (c.x == x && c.y == y && c.z == z)
				return c;
		}
		return null;
	}

	public Tile tile(int x, int y, int z){
		if (x < 0 || x >= width || y < 0 || y >= height || z < 0 || z >= depth)
			return Tile.BOUNDS;
		else
			return tiles[x][y][z];
	}

	public char glyph(int x, int y, int z) {
		Creature creature = creature(x, y, z);
		if (creature != null) { return creature.glyph(); }
		if (item(x, y, z) != null) { return item(x, y, z).glyph(); }

		return tile(x,y,z).glyph();
	}

	public Color color(int x, int y, int z){
		Creature creature = creature(x, y, z);
		if (creature != null) { return creature.color(); }
		if (item(x, y, z) != null) { return item(x, y, z).color(); }

		return tile(x, y, z).color();
	}

	public void dig(int x, int y, int z) {
		if (tile(x, y, z).isDiggable())
			tiles[x][y][z] = Tile.FLOOR_DUG;
	}

	public void glow(int x, int y, int z) {
		if (tiles[x][y][z] == Tile.WALL) { tiles[x][y][z] = Tile.WALL_GLOWING;}
		if (tiles[x][y][z] == Tile.DEEPWALL) { tiles[x][y][z] = Tile.DEEPWALL_GLOWING;}
		if (tiles[x][y][z] == Tile.DEEPERWALL) { tiles[x][y][z] = Tile.DEEPERWALL_GLOWING;}
	}

	public void unglow(int x, int y, int z) {
		if (tiles[x][y][z] == Tile.WALL_GLOWING) { tiles[x][y][z] = Tile.WALL;}
		if (tiles[x][y][z] == Tile.DEEPWALL_GLOWING) { tiles[x][y][z] = Tile.DEEPWALL;}
		if (tiles[x][y][z] == Tile.DEEPERWALL_GLOWING) { tiles[x][y][z] = Tile.DEEPERWALL;}
	}

	public void addPlantAtEmptyLocation(Creature creature, int z){
		int x;
		int y;

		do {
			x = (int)(Math.random() * width);
			y = (int)(Math.random() * height);
		}
		while (!tile(x,y,z).isGround() || !tile(x,y,z).isWalkable() || tile(x,y,z).isWater() || tile(x,y,z) == Tile.NOOKWATER || creature(x,y,z) != null
			|| tile(x,y,z).isStairs() || inNook(x,y,z) );

		creature.x = x;
		creature.y = y;
		creature.z = z;
		creatures.add(creature);
	}

	public void addAtEmptyLocation(Creature creature, int z){
		int x;
		int y;

		do {
			x = (int)(Math.random() * width);
			y = (int)(Math.random() * height);
		}
		while (!tile(x,y,z).isGround() || tile(x,y,z).isWater() || creature(x,y,z) != null || inNook(x,y,z) || tile(x,y,z).isStairs());

		creature.x = x;
		creature.y = y;
		creature.z = z;
		creatures.add(creature);
	}

	// add items to world
	public void addAtEmptyLocation(Item item, int depth) {
		int x;
		int y;

		// roll location.
		// if tile isn't ground, re-roll.
		do {
			x = (int)(Math.random() * width);
			y = (int)(Math.random() * height);
		}
		while (!tile(x,y,depth).isGround() || creature(x,y,depth) != null || tile(x,y,depth).isWater() || item(x,y,depth) != null || inNook(x,y,depth) || tile(x,y,depth).glyph() == 'T' || tile(x,y,depth).isStairs() || tile(x,y,depth) == Tile.NOOKWATER || tile(x,y,depth) == Tile.WINDOW_HORIZ || tile(x,y,depth) == Tile.WINDOW_VERT);

		items[x][y][depth] = item;

	}

	public void addAtSpecificLocation(Item item, int x, int y, int depth) {
		items[x][y][depth] = item;
	}

	public void addAroundBudlet(int depth, Creature creature) {
		Item item;
		int xLoc = 0;
		int yLoc = 0;

		//find budlet location
		for (int i = 0; i < 40; i++) {
			for (int j = 0; j < 34 ; j++) {
				if (items[i][j][depth] != null && item(i,j,depth).glyph() == (char)232) {
					xLoc = i;
					yLoc = j;
					break;
				}
			}
		}

		int xR;
		int yR;

		do {
			xR = ThreadLocalRandom.current().nextInt(-4, 4 + 1);
			yR = ThreadLocalRandom.current().nextInt(-4, 4 + 1);
		}
		while (xLoc + xR > 0 &&
			xLoc + xR < 40 &&
			yLoc + yR > 0 &&
			yLoc + yR < 34 &&
			items[xLoc + xR][yLoc + yR][depth] != null &&
			creature(xLoc + xR, yLoc + yR, depth) != null);

		creature.x = xLoc + xR;
		creature.y = yLoc + yR;
		creature.z = depth;
		creatures.add(creature);
	}

	// dropping itemAt to ground
	public boolean addAtEmptySpace(Item item, int x, int y, int z) {
		if (item == null) { return true; }

		List<Point> points = new ArrayList<Point>();
		List<Point> checked = new ArrayList<Point>();

		points.add(new Point(x, y, z));

		while (!points.isEmpty()) {
			Point p = points.remove(0);
			checked.add(p);

			if (!tile(p.x, p.y, p.z).isGround()) {
				continue;
			}

			if (items[p.x][p.y][p.z] == null) {
				items[p.x][p.y][p.z] = item;
				Creature c = this.creature(p.x, p.y, p.z);
				if (c != null) {
					c.notify("A %s lands between your feet", item.name());
				}
				return true;
			} else {
				List<Point> neighbors = p.neighbors8();
				neighbors.removeAll(checked);
				points.addAll(neighbors);
			}
		}
		return false;

	}

	public void update(){
		List<Creature> toUpdate = new ArrayList<Creature>(creatures);
		for (Creature creature : toUpdate){
			creature.update();
		}
	}

	public void remove(Creature other) {
		creatures.remove(other);
	}

	public Item item(int x, int y, int z) {
		return items[x][y][z];
	}

	public void remove(int x, int y, int z) {
		items[x][y][z] = null;
	}

	public boolean inNook(int x,int y,int z) {
		return x > 16 && x < 25 && y > 10 && y < 18 && z == 0;
	}

}

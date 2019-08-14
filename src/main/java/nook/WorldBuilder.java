package nook;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorldBuilder {

	private int width;
	private int height;
	private int depth;
	private Tile[][][] tiles;
	private int[][][] regions;
	private int nextRegion;

	public WorldBuilder(int width, int height, int depth) {
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.tiles = new Tile[width][height][depth];
		this.regions = new int[width][height][depth];
		this.nextRegion = 1;
	}

	public World build() {
		return new World(tiles);
	}

	private WorldBuilder randomizeTiles() {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				for (int z = 0; z < depth; z++) {
					tiles[x][y][z] = Math.random() < 0.5 ? Tile.FLOOR : Tile.WALL;
				}
			}
		}
		return this;
	}

	private WorldBuilder smooth(int times) {
		Tile[][][] tiles2 = new Tile[width][height][depth];
		for (int time = 0; time < times; time++) {

			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					for (int z = 0; z < depth; z++) {
						int floors = 0;
						int rocks = 0;

						for (int ox = -1; ox < 2; ox++) {
							for (int oy = -1; oy < 2; oy++) {
								if (x + ox < 0 || x + ox >= width || y + oy < 0
									|| y + oy >= height)
									continue;

								if (tiles[x + ox][y + oy][z] == Tile.FLOOR)
									floors++;
								else
									rocks++;
							}
						}
						if (rocks > 8) { tiles2[x][y][z] = Tile.DEEPWALL; }
						else { tiles2[x][y][z] = (floors >= rocks) ? Tile.FLOOR : Tile.WALL; }
					}
				}
			}
			tiles = tiles2;
		}
		return this;
	}

	private WorldBuilder makeGrass() {

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				for (int z = 0; z < depth; z++) {

					for (int ox = -1; ox < 2; ox++) {
						for (int oy = -1; oy < 2; oy++) {

							if (x + ox < 0 || x + ox >= width || y + oy < 0 || y + oy >= height)
								continue;

							if (tiles[x + ox][y + oy][z].isGround() && Math.random() > 0.98) {
								tiles[x][y][z] = Tile.GRASS;
							}
						}
					}
				}
			}
		}
		return this;
	}

	private WorldBuilder createRegions(){
		regions = new int[width][height][depth];

		for (int z = 0; z < depth; z++){
			for (int x = 0; x < width; x++){
				for (int y = 0; y < height; y++){
					if (tiles[x][y][z] != Tile.WALL && regions[x][y][z] == 0){
						int size = fillRegion(nextRegion++, x, y, z);

						if (size < 25)
							removeRegion(nextRegion - 1, z);
					}
				}
			}
		}
		return this;
	}

	private void removeRegion(int region, int z){
		for (int x = 0; x < width; x++){
			for (int y = 0; y < height; y++){
				if (regions[x][y][z] == region){
					regions[x][y][z] = 0;
					tiles[x][y][z] = Tile.WALL;
				}
			}
		}
	}

	private int fillRegion(int region, int x, int y, int z) {
		int size = 1;
		ArrayList<Point> open = new ArrayList<Point>();
		open.add(new Point(x,y,z));
		regions[x][y][z] = region;

		while (!open.isEmpty()){
			Point p = open.remove(0);

			for (Point neighbor : p.neighbors8()){
				if (neighbor.x < 0 || neighbor.y < 0 || neighbor.x >= width || neighbor.y >= height)
					continue;

				if (regions[neighbor.x][neighbor.y][neighbor.z] > 0
					|| tiles[neighbor.x][neighbor.y][neighbor.z] == Tile.WALL)
					continue;

				size++;
				regions[neighbor.x][neighbor.y][neighbor.z] = region;
				open.add(neighbor);
			}
		}
		return size;
	}

	public WorldBuilder connectRegions(){
		for (int z = 0; z < depth-1; z++){
			connectRegionsDown(z);
		}
		return this;
	}

	private void connectRegionsDown(int z){
		List<String> connected = new ArrayList<String>();

		for (int x = 0; x < width; x++){
			for (int y = 0; y < height; y++){
				String region = regions[x][y][z] + "," + regions[x][y][z+1];
				if (tiles[x][y][z] == Tile.FLOOR
					&& tiles[x][y][z+1] == Tile.FLOOR
					&& !connected.contains(region) && tiles[x][y][z] != Tile.NOOK_FLOOR){
					connected.add(region);
					connectRegionsDown(z, regions[x][y][z], regions[x][y][z+1]);
				}
			}

		}
	}

	private void connectRegionsDown(int z, int r1, int r2){
		List<Point> candidates = findRegionOverlaps(z, r1, r2);

		int stairs = 0;
		do{
			Point p = candidates.remove(0);
			tiles[p.x][p.y][z] = Tile.STAIRS_DOWN;
			tiles[p.x][p.y][z+1] = Tile.STAIRS_UP;
			stairs++;
		}
		while (candidates.size() > 1400);
	}

	public List<Point> findRegionOverlaps(int z, int r1, int r2) {
		ArrayList<Point> candidates = new ArrayList<Point>();

		for (int x = 0; x < width; x++){
			for (int y = 0; y < height; y++){
				if (tiles[x][y][z] == Tile.FLOOR
					&& tiles[x][y][z+1] == Tile.FLOOR
					&& regions[x][y][z] == r1
					&& regions[x][y][z+1] == r2){
					candidates.add(new Point(x,y,z));
				}
			}
		}

		Collections.shuffle(candidates);
		return candidates;
	}

	public WorldBuilder makeCaves() {
		return randomizeTiles()
			.smooth(8)
			.makeGrass()
			.createFirstString()
			.deepRocks()
			.createRegions()
			.connectRegions();

		//.addExitStairs();
	}

	private WorldBuilder deepRocks() {

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				for (int z = 0; z < depth ; z++) {

					int deepRocks = 0;
					for (int ox = -1; ox < 2; ox++) {
						for (int oy = -1; oy < 2; oy++) {
							if (x + ox < 0 || x + ox >= width || y + oy < 0 || y + oy >= height) {
								continue;
							}
							if (tiles[x + ox][y + oy][z] == Tile.DEEPWALL || tiles[x + ox][y + oy][z] == Tile.DEEPERWALL ) {
								deepRocks++;
							}
						}
					}
					if (deepRocks > 7) {
						tiles[x][y][z] = Tile.DEEPERWALL;
					}
				}
			}
		}
		return this;
	}

	/** Draw predefined nook in center of randomly generated and smoothed level. (depth = 0) */
	private WorldBuilder createFirstString() {

		String home =   "0,,000,,,00000000000\n" +
			",,0000000000000,,,00\n" +
			"00005555555555000000\n" +
			"000555555555555,0000\n" +
			"00,,5555555555,00000\n" +
			"000,0000000000000000\n" +
			"000000011---11000,00\n" +
			"00000001.....1000,00\n" +
			"0000000|..<..|0,0000\n" +
			"0000000|.....|0,0000\n" +
			"000.000|.....|000000\n" +
			"000..001.....1000000\n" +
			"00..00011...11000000\n" +
			"0##0000000..00000000\n" +
			",##00000000..00,,,00\n" +
			",,00,,,,,,00..00,000\n" +
			"000,,NNNN,,0..000000\n" +
			"00,,NNNNNN,,.0000,00\n" +
			"000,,NNNN,,.00000000\n" +
			"0000,,,,,,0000,,0000\n" +
			"00000000000000,,0000";

		String[] lines = home.split("\n");

		int i = 0; int j = 0;
		for (int wx = 10; wx < 30; wx++) {

			if (i > 19) {i=0;}
			for (int wy = 5; wy < 26; wy++) {
				if (j > 20) {j=0;}
				if (lines[j].charAt(i) == '1') {
					tiles[wx][wy][0] = Tile.NOOKWALL;
				} else if (lines[j].charAt(i) == '5') {
					tiles[wx][wy][0] = Tile.WATER;
				} else if (lines[j].charAt(i) == '|') {
					tiles[wx][wy][0] = Tile.WINDOW_VERT;
				} else if (lines[j].charAt(i) == '-') {
					tiles[wx][wy][0] = Tile.WINDOW_HORIZ;
				} else if (lines[j].charAt(i) == '#') {
					tiles[wx][wy][0] = Tile.WALL;
				} else if (lines[j].charAt(i) == 'N') {
					tiles[wx][wy][0] = Tile.NOOKWATER;
				} else if (lines[j].charAt(i) == '<') {
					tiles[wx][wy][0] = Tile.STAIRS_UP;
				} else if (lines[j].charAt(i) == '.') {
					tiles[wx][wy][0] = Tile.NOOK_FLOOR;
				} else if (lines[j].charAt(i) == ',') {
					tiles[wx][wy][0] = Tile.GRASS;
				} else {
					tiles[wx][wy][0] = Tile.FLOOR;
				}
				j++;
			}
			i++;
		}
		return this;
	}

	private WorldBuilder addExitStairs() {
		int x = -1;
		int y = -1;

		do {
			x = (int)(Math.random() * width);
			y = (int)(Math.random() * height);
		} while (tiles[x][y][0] != Tile.FLOOR);

		tiles[x][y][0] = Tile.STAIRS_UP;
		return this;
	}

}

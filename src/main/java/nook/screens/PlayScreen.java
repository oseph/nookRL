package nook.screens;

import java.awt.*;
import java.awt.event.KeyEvent;
import static java.awt.Color.*;
import java.util.ArrayList;
import java.util.List;
import asciiPanel.AsciiPanel;
import nook.*;

public class PlayScreen implements Screen {

	private World world;
	private Creature player;
	private int screenWidth;
	private int screenHeight;
	private List<String> messages;
	private FieldOfView fov;
	private Screen subscreen;
	private int playerStartX = 20;
	private int playerStartY = 14;
	private boolean haveSecateurs;
	private boolean haveBudlet;
	private boolean canSecateursBonus;
	private boolean secateurCheck;
	private boolean canWholeSetBonus;
	private boolean canSaucerBonus;
	private int counter;
	Color bg = new Color(12, 12, 12);

	public PlayScreen(){
		screenWidth = 40;
		screenHeight = 34;
		messages = new ArrayList<String>();
		createWorld();
		fov = new FieldOfView(world);
		StuffFactory stuffFactory = new StuffFactory(world);
		createCreatures(stuffFactory);
		createItems(stuffFactory);
		haveSecateurs = false;
		haveBudlet = false;
		canSecateursBonus = true;
		secateurCheck = true;
		canWholeSetBonus = true;
		counter = 0;
	}

	@Override
	public void displayOutput(AsciiPanel terminal) {
		terminal.setDefaultBackgroundColor(bg);
		int left = getScrollX();
		int top = getScrollY();
		displayTiles(terminal, left, top);
		terminal.write(player.glyph(), player.x - left, player.y - top, player.color());
		displayStats(terminal, 36);
		displayMessages(terminal, messages);
		if (subscreen != null) { subscreen.displayOutput(terminal);}
		//System.out.println("x: "+player.x +" y: " + player.y);
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		if (subscreen != null) {
			subscreen = subscreen.respondToUserInput(key);
		} else {
			switch (key.getKeyCode()) {
				case KeyEvent.VK_ESCAPE:
					return new LoseScreen(player);
				case KeyEvent.VK_LEFT:
				case KeyEvent.VK_H:
					player.moveBy(-1, 0, 0);
					break;
				case KeyEvent.VK_RIGHT:
				case KeyEvent.VK_L:
					player.moveBy(1, 0, 0);
					break;
				case KeyEvent.VK_UP:
				case KeyEvent.VK_K:
					player.moveBy(0, -1, 0);
					break;
				case KeyEvent.VK_DOWN:
				case KeyEvent.VK_J:
					player.moveBy(0, 1, 0);
					break;
				case KeyEvent.VK_Y:
					player.moveBy(-1, -1, 0);
					break;
				case KeyEvent.VK_U:
					player.moveBy(1, -1, 0);
					break;
				case KeyEvent.VK_B:
					player.moveBy(-1, 1, 0);
					break;
				case KeyEvent.VK_N:
					player.moveBy(1, 1, 0);
					break;
				case KeyEvent.VK_D:
					if (player.inventory().isEmpty()) {
						player.notify("You have nothing to drop.");
					} else {
						subscreen = new DropScreen(player);
					}
					break;
				case KeyEvent.VK_P:
					petalHealer();
					break;
				case KeyEvent.VK_I:
					subscreen = new ExamineScreen(player);
					break;
				case KeyEvent.VK_SEMICOLON:
					subscreen = new LookScreen(world, player, "Looking around.", player.x - getScrollX(), player.y - getScrollY());
					break;
				case KeyEvent.VK_T:
					subscreen = new ThrowScreen(world, player, player.x - getScrollX(), player.y - getScrollY());
					break;
			}

			switch (key.getKeyChar()) {
				case 'g':
				case ',':
					player.pickup();
					break;
				case '<':
					if (userIsTryingToExit()){
						return userExits();
					} else { player.moveBy(0, 0, -1);
						break; }
				case '>':
					player.moveBy(0, 0, 1);
					break;
				case '?':
					subscreen = new HelpScreen(); break;
			}
		}

		if (subscreen == null) { world.update(); }

		if (player.hp() < 1) { return new LoseScreen(player); }

		// special score fun!
		// if secateurs in hand before picking up budlet: budlet score x2
		counter++;
		scoreChecker();

		return this;
	}

	private void createCreatures(StuffFactory stuffFactory){
		player = stuffFactory.newPlayer(messages, fov);
		player.x = playerStartX; player.y = playerStartY;

		for (int z = 0; z < world.depth(); z++){
			for (int i = 0; i < 12; i++){
				stuffFactory.newFungus(z, true);
			}
		}

		for (int z = 1; z < world.depth(); z++){
			for (int i = 0; i < 6; i++){
				stuffFactory.newSproutling(z, player);
			}
		}

		for (int z = 3; z < world.depth(); z++){
			for (int i = 0; i < 5; i++){
				stuffFactory.newBramble(z, player);
			}
		}

		for (int z = 0; z < world.depth(); z++){
			for (int i = 0; i < 4; i++){
				stuffFactory.newPetalTree(z);
			}
		}

		stuffFactory.newBudlet(4);

		for (int i = 0; i < 2; i++){
			stuffFactory.newObelisk(4, player);
		}

	}

	private void createItems(StuffFactory stuffFactory) {
		for (int z = 0; z < world.depth(); z++) {
			for (int i = 0; i < world.width()*world.height() / 100; i++) {
				stuffFactory.newPetal(z);
			}
		}

		stuffFactory.newSecateurs(world.depth()-2);
		stuffFactory.newDemitasse(world.depth()-4);
		stuffFactory.newSaucer(world.depth()-3);
	}

	private void createWorld(){
		world = new WorldBuilder(40, 34, 5)
			.makeCaves()
			.build();
	}

	public int getScrollX() { return Math.max(0, Math.min(player.x - screenWidth / 2, world.width() - screenWidth)); }

	public int getScrollY() { return Math.max(0, Math.min(player.y - screenHeight / 2, world.height() - screenHeight)); }

	private void displayStats(AsciiPanel terminal, int y){
		String depthNumber;
		terminal.clear(' ', 0, 34, 40, 6,AsciiPanel.brightWhite, bg);
		if (player.z == 0) {
			depthNumber = String.format("[Nook]");
		} else { depthNumber = String.format("[Cave depth: %1d]", player.z+1);}

		String scoreStats = String.format("%1d pts", player.score());
		String hpStats = String.format("%1d/%1dHP", player.hp(), player.maxHp());
		String petalCount = String.format("%1d/10 [p]etals", player.petalCounter());
		terminal.writeCenter(depthNumber, y-1, null, bg);
		terminal.writeCenter(hpStats, y,null, bg);

		if (player.petalCounter() >= 10) {
			terminal.writeCenter(petalCount, y+1, green, bg);
		} else if (player.petalCounter() >= 5) {
			terminal.writeCenter(petalCount, y+1, blue, bg);
		} else {
			terminal.writeCenter(petalCount, y+1, null, bg);
		}
		terminal.writeCenter(scoreStats, y+2,null, bg);
	}

	private void displayMessages(AsciiPanel terminal, List<String> messages) {
		int top;
		int a = 255;
		Color col;
		if (player.y < 11){
			top = (screenHeight-2) - messages.size();
		} else {
			top = 5 - messages.size();
		}
		for (int i = 0; i < messages.size(); i++){
			col = new Color(a,a,a);
			terminal.writeCenter(messages.get(i), top + i, col);
			//col.darker();
			a = a - 75;
			if (a < 0) {a = 0;}
		}
		messages.clear();
	}

	private void displayTiles(AsciiPanel terminal, int left, int top) {
		fov.update(player.x, player.y, player.z, player.visionRadius());
		for (int x = 0; x < screenWidth; x++){
			for (int y = 0; y < screenHeight; y++){
				int wx = x + left;
				int wy = y + top;
				if (player.canSee(wx, wy,player.z)) {
					terminal.write(world.glyph(wx, wy, player.z), x, y, world.color(wx, wy, player.z), new Color(0, 30,0));
					if (player.haveItem("petal") || player.haveItem("fungal petal") ) {
						player.glow();
					} else {
						player.unglow();
					}
				} else {
					terminal.write(fov.tile(wx, wy, player.z).glyph(), x, y, new Color(11, 36, 10));
				}
			}
		}
		terminal.setDefaultBackgroundColor( Color.black);
		terminal.setDefaultForegroundColor( Color.white);
	}

	private boolean userIsTryingToExit() {
		return player.z == 0 && world.tile(player.x, player.y, player.z) == Tile.STAIRS_UP;
	}

	private Screen userExits() {
		for (Item item: player.inventory().getItems()) {
			if (item != null && item.name().equals("budlet")) {
				return new WinScreen(player);
			}
		}
		return new LoseScreen(player);
	}

	private void petalHealer() {
		Item[] items = player.inventory().getItems();

		if (player.petalCounter() >= 5) {
			// 3hp + 10% (rounded down) of missing hp.
			player.modifyHp(5+((player.maxHp()-player.hp())/10));
			int rem = 0;
			for (int i = 0; i < items.length; i++) {
				if (rem < 5 && items[i] != null &&
						(items[i].name().matches("petal") || items[i].name().matches("fungal petal"))) {
					player.inventory().remove(items[i]);
					rem++;
				}
			}
			player.petalHealCountUp();
		}
	}

	private void scoreChecker() {
		if (player.haveItem("budlet") && !player.haveItem("secateurs")) {
			canSecateursBonus = false;
			//System.out.println("no secateur bonus for you");
		}

		if (player.haveItem("demitasse") && !player.haveItem("saucer")) {
			canSaucerBonus = false;
			//System.out.println("no saucer bonus for you");
		}

		if (player.haveItem("secateurs") && player.haveItem("budlet")) {
			if (canSecateursBonus) {
				player.notify("You snip off the budlet.");
				//System.out.println("Secateur prescore = " + player.score());
				player.addPoints(4000);
				canSecateursBonus = false;
				//System.out.println("secateurs bonus! budlet X2!");
			}
		}

		if (canWholeSetBonus) {
			if (player.haveItem("secateurs") && player.haveItem("budlet") && player.haveItem("demitasse") && player.haveItem("saucer")) {
				//System.out.println("Whole set prescore = " + player.score());
				player.addPoints(player.score());
				canWholeSetBonus = false;
				//System.out.println("whole set bonus!");
			}
		}
		if (counter % 3 == 0) {
			player.addPoints(-1);
		}
	}
}

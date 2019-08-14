package nook;

import asciiPanel.AsciiPanel;

import java.awt.Color;

public class Creature {

	private World world;
	public int x;
	public int y;
	public int z;
	private char glyph;
	public Color color;
	private Color defaultColor;
	private CreatureAi ai;
	private int maxHp;
	private int score;
	public int scorePts;
	private int hp;
	private int attackValue;
	private int defenseValue;
	private int visionRadius;
	private String name;
	private String description;
	private Inventory inventory;
	private int petalHealCount;

	public Creature(World world, String name, char glyph, Color color, int maxHp, int attack, int defense, int visionRadius, int scorePts, String description){
		this.world = world;
		this.glyph = glyph;
		this.color = color;
		this.defaultColor = color;
		this.maxHp = maxHp;
		this.hp = maxHp;
		this.attackValue = attack;
		this.defenseValue = defense;
		this.visionRadius = visionRadius;
		this.name = name;
		this.score = 0;
		this.scorePts = scorePts;
		this.inventory = new Inventory(10);
		this.description = description;
		petalHealCount = 0;
	}

	public char glyph() { return glyph; }
	public Color color() { return color; }
	public void setCreatureAi(CreatureAi ai) { this.ai = ai; }
	public int maxHp() { return maxHp; }
	public int score() {return score;}
	public int hp() { return hp; }
	public int attackValue() { return attackValue; }
	public int defenseValue() { return defenseValue; }
	public int visionRadius() { return visionRadius; }
	public String name() { return name;}
	public String description() { return description; }
	public Inventory inventory() {return inventory;}
	public void petalHealCountUp() { petalHealCount++;}
	public int petalHealCount() {return petalHealCount;}

	public void moveBy(int mx, int my, int mz) {
		if (x+mx < 0) {
			mx = 0;
		}
		if (x+mx >= world.width()) {
			mx = world.width();
		}
		if (y+my < 0) {
			my = 0;
		}
		if (y+my >= world.height()) {
			my = world.height();
		}

		Tile tile = world.tile(x+mx, y+my, z+mz);

		if (mx == 0 && my == 0 && mz ==0) {
			return;
		}

		if (!tile.isWalkable()) {
			if (tile == Tile.TREE) {
				doAction("graze the petal tree");
				return;
			} else {
				return;
			}
		}

		if (tile.isWater()) {
			notify((Math.random() > 0.5) ? "Splish splosh!" : "Splash sploosh!");
		}

		if (tile.isNookWater()) {
			maxHp = maxHp() - 2;
			modifyHp(maxHp-hp);
			notify((Math.random() > 0.5) ? "Oo! It stings." : "Ack! That sort of burns.");
		}

		itemNotify(x+mx, y+my, z+mz);

		if (mz == -1){
			if (tile == Tile.STAIRS_DOWN) {
				notify("You walk up to level %d", z+mz+1);
			} else {
				doAction("bonk your head");
				return;
			}
		} else if (mz == 1){
			if (tile == Tile.STAIRS_UP) {
				doAction("walk down to level %d", z+mz+1);
			} else {
				doAction("face plant");
				return;
			}
		}
		// if there's another creature
		Creature other = world.creature(x+mx, y+my, z+mz);

		if (other == null) {
			ai.onEnter(x+mx, y+my, z+mz, tile);
		} else {
			attack(other);
		}

		if (world.inNook(x,y,z)) { notify("Sure is cozy here."); }

	}

	public void attack(Creature other){
		if (other.name().equals("petal tree")) {
			if (Math.random() > 0.5) {
				doAction("admire the petal tree");
			} else {
				doAction("gaze at the petal tree");
			}
			return;
		}

		int amount = Math.max(0, attackValue() - other.defenseValue());
		amount = (int)(Math.random() * amount) + 1;
		other.modifyHp(-amount);

		if (other.hp < 1 ) {
			addPoints(other);
			notify("The %s dies.", other.name());
		}

		doAction("hit the %s", other.name);
		other.notify("The %s hits you.", name());
	}

	public void modifyHp(int amount) {
		hp += amount;
		if (hp > maxHp) { hp = maxHp; }

		if (hp < 1) {
			if (Math.random() < 0.3) {leavePetal();}
			world.remove(this);
		}
		if (hp < maxHp/4) {
			this.color = AsciiPanel.brightRed;
		} else if (hp < maxHp/2) {
			this.color = AsciiPanel.brightYellow;
		} else if (hp > maxHp/2) {
			this.color = defaultColor;
		}
	}

	private void leavePetal(){
		Item petal = new Item('`', AsciiPanel.green, "petal", 5, "A fungal petal.");
		world.addAtEmptySpace(petal, x, y, z);
	}

	public void addPoints(Creature other){
		score += other.scorePts;
	}

	public void addPoints(int num){
		if (score + num < 1) { score = 0; }
		else {
			score += num;
		}
	}

	public void dig() { // int wx, int wy, int wz) {
		Creature player = creatureAt(x,y,z);
		Item[] items = player.inventory().getItems();

		for (int i = 0; i < items.length; i++) {
			if (items[i] != null && (items[i].name().equals("petal") || items[i].name().equals("fungal petal"))) {
				int r = 2;
				for (int ox = -r; ox < r+1 ; ox++) {
					for (int oy = -r; oy < r+1; oy++) {
						if (world.tile(ox+x, oy+y, player.z) != null) {
							world.dig(ox+x, oy+y, player.z);
						}
					}
				}
				notify("The walls retract.");
				notify("Your petal dissolves.");
				player.inventory().remove(items[i]);
				break;
			}
		}
	}

	public void glow() {
		Creature player = creatureAt(x, y, z);
		int r = visionRadius();
		for (int ox = -r; ox < r + 1; ox++) {
			for (int oy = -r; oy < r + 1; oy++) {
				if (ox + x >= 0 && ox + x < world.width() && oy + y >= 0 && oy + y < world.height()) {
					if (world.tile(ox + x, oy + y, player.z) != null) {
						if (ox > -3 && ox < 3 && oy > -3 && oy < 3) {
							world.glow(ox + x, oy + y, player.z);
						} else {
							world.unglow(ox + x, oy + y, player.z);
						}
					}
				}
			}
		}
	}

	public void unglow() {
		Creature player = creatureAt(x, y, z);
		int r = visionRadius();
		for (int ox = -r; ox < r + 1; ox++) {
			for (int oy = -r; oy < r + 1; oy++) {
				if (ox + x >= 0 && ox + x < world.width() && oy + y >= 0 && oy + y < world.height()) {
					if (world.tile(ox + x, oy + y, player.z) != null) {
						if (ox > -3 && ox < 3 && oy > -3 && oy < 3) {
							world.unglow(ox + x, oy + y, player.z);
						}
					}
				}
			}
		}
	}

	public void update(){
		ai.onUpdate();
	}

	public boolean canEnter(int wx, int wy, int wz) {
		return world.tile(wx, wy, wz).isGround() && world.creature(wx, wy, wz) == null;
	}

	public void notify(String message, Object ... params){
		ai.onNotify(String.format(message, params));
	}

	public void doAction(String message, Object ... params){
		//radius in which you can hear events
		int r = 9;
		for (int ox = -r; ox < r+1; ox++){
			for (int oy = -r; oy < r+1; oy++){
				if (ox*ox + oy*oy > r*r)
					continue;

				Creature other = world.creature(x+ox, y+oy, z);

				if (other == null)
					continue;

				if (other == this)
					other.notify("You " + message + ".", params);
			}
		}
	}

	private String makeSecondPerson(String text){
		String[] words = text.split(" ");
		words[0] = words[0] + "s";

		StringBuilder builder = new StringBuilder();
		for (String word : words){
			builder.append(" ");
			builder.append(word);
		}
		return builder.toString().trim();
	}

	public boolean canSee(int wx, int wy, int wz){
		return ai.canSee(wx,wy,wz);
	}


	public boolean inNook() {
		return this.x > 27 && this.x < 34 && this.y > 11 && this.y < 17;
	}

	public void pickup() {
		Item item = world.item(x,y,z);

		if (item != null) {
			if (inventory.isFull()) {
				notify("Your pack is full.");
			} else {
				doAction("pickup a %s", item.name());
				world.remove(x, y, z);
				inventory.add(item);
				score += item.scoreValue();
			}
		} else {
			notify("There's here nothing to pick up.");
		}
	}

	public void drop(Item item){
		if (item == null) { return; }
		if (world.addAtEmptySpace(item, x, y, z)) {
			doAction("drop a " + item.name());
			inventory.remove(item);
			//world.addAtEmptySpace(itemAt, x, y, z);
			score -= item.scoreValue();
		} else {
			notify("There's nowhere to drop the %s", item.name());
		}
	}

	private void itemNotify(int x,int y,int z) {
		Item item = world.item(x,y,z);
		Tile tile = tile(x,y,z);
		if (tile == null) {}
		if (tile == Tile.STAIRS_UP) { notify("You see stairs leading up.");}
		else if (tile == Tile.STAIRS_DOWN) { notify("You see stairs leading down.");}

		if (item == null) {return;}
		else { notify("You see a %s here.", item.name());}
	}

	public int petalCounter() {
		Item[] items = this.inventory().getItems();
		int count = 0;
		for (int i = 0; i < items.length; i++) {
			if (items[i] != null && (items[i].name().matches("petal") || items[i].name().matches("fungal petal"))) {
				count++;
			}
		}
		return count;
	}

	public boolean haveItem(String item) {
		Item[] items = this.inventory().getItems();
		for (int i = 0; i < items.length; i++) {
			if (items[i] != null && items[i].name().equals(item)) {
				return true;
			}
		}
		return false;
	}

	public Tile realTile(int wx, int wy, int wz) {
		return world.tile(wx, wy, wz);
	}

	public Tile tile(int wx, int wy, int wz) {
		if (canSee(wx, wy, wz)) {
			return world.tile(wx,wy,wz);
		} else {
			return ai.rememberedTile(wx, wy, wz);
		}
	}

	public Creature creatureAt(int wx, int wy, int wz) {
		if (canSee(wx, wy, wz)){
			return world.creature(wx, wy, wz);
		} else {
			return null;
		}
	}

	public Item itemAt(int wx, int wy, int wz) {
		if (canSee(wx, wy, wz)){
			return world.item(wx, wy, wz);
		} else {
			return null;
		}
	}

	public void throwItem(Item item, int wx, int wy, int wz) {
		Point end = new Point(x, y, 0);

		for (Point p : new Line(x, y, wx, wy)){
			if (!realTile(p.x, p.y, z).isGround())
				break;
			end = p;
		}

		wx = end.x;
		wy = end.y;

		Creature c = creatureAt(wx, wy, wz);

		if (c != null) {
			throwAttack(item, c);
		}

		inventory.remove(item);
		world.addAtEmptySpace(item, wx, wy, wz);

	}

	private void throwAttack(Item item, Creature other) {
		int amount = Math.max(0, attackValue / 2 + item.thrownAttackValue() - other.defenseValue());

		amount = (int)(Math.random() * amount) + 1;

		//doAction("throw a %s at the %s", itecm.name(), other.name);

		other.modifyHp(-amount);
	}

}

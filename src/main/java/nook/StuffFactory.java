package nook;

import java.util.List;
import java.util.Random;
import asciiPanel.AsciiPanel;
import java.awt.Color;

public class StuffFactory {

    private World world;
    private Random rng;

    public StuffFactory(World world){
        this.world = world;
        this.rng = new Random();
    }

    // CREATURES
    // world/ name/ glyph/ color/ HP/ Dmg/ Def/ sight radius/ pts //

    public Creature newPlayer(List<String> messages, FieldOfView fov){
        Creature player = new Creature(world, "you", (char)2, AsciiPanel.brightWhite, 25, 5, 1, 12, 10000, "This is you.");
        world.addAtEmptyLocation(player, 0);
        new PlayerAi(player, messages, fov);
        return player;
    }

    public Creature newFungus(int depth, boolean canSpread){
        Creature fungus = new Creature(world, "fungus", (char)5, colorMe(), 2, 0, 0, 2, 1, "Regular cave fungus.");
        world.addPlantAtEmptyLocation(fungus, depth);
        new FungusAi(fungus, this, canSpread);
        return fungus;
    }

    public Creature newSproutling(int depth, Creature player){
        Creature sproutling = new Creature(world, "sproutling", (char)234, new Color(0,255,0), 10, 2, 0, 3, 10, "An erratic sproutling.");
        world.addAtEmptyLocation(sproutling, depth);
        new SproutlingAi(sproutling, player);
        return sproutling;
    }

    public Creature newBramble(int depth, Creature player){
        Creature bramble = new Creature(world, "bramble", (char)143, new Color(103, 175, 29), 15, 3, 1, 7, 50, "A lumbering bramble. It looks grumpy.");
        world.addAtEmptyLocation(bramble, depth);
        Item petal = new Item('*', new Color(0x8E9B7D), "thorn", 20, "launch");
        petal.modifyThrownAttackValue(2);
        bramble.inventory().add(petal);
        new BrambleAI(bramble, player);
        return bramble;
    }

    public Creature newObelisk(int depth, Creature player){
        Creature obelisk = new Creature(world, "obelisk", '8', new Color(143,200, 63), 15, 3, 1, 9, 50, "An ominous obelisk.");
        world.addAroundBudlet(depth, obelisk);
        for (int i = 0; i < 10; i++) {
            Item shard = new Item('o', new Color(133, 151, 58), "shard", 50, "An sharp fragment.");
            shard.modifyThrownAttackValue(3);
            obelisk.inventory().add(shard);
        }
        new ObeliskAI(obelisk, player);
        return obelisk;
    }

    public Creature newPetalTree(int depth){
        Creature petalTree = new Creature(world, "petal tree", 'T', new Color(78, 149, 42), 10000, 0, 10000, 7, 50, "A pleasant petal tree.");
        world.addAtEmptyLocation(petalTree, depth);
        new PetalTreeAi(petalTree, this, world);
        return petalTree;
    }

    // ITEMS

    public Item newPetal(int depth) {
        Item petal = new Item('`', colorMe1(), "petal", 5, "Yet another lovely petal.");
        world.addAtEmptyLocation(petal, depth);
        return petal;
    }

    public Item newPetalDrop(int x, int y, int depth) {
        Item petalDrop = new Item('`', colorMe1(), "fungal petal", 5, "A slightly smelly fungal petal.");
        world.addAtSpecificLocation(petalDrop, x, y, depth);
        return petalDrop;
    }

    public Item newBudlet(int depth) {
        Item budlet = new Item((char)232, new Color(224,0, 219), "budlet", 4000, "A delicious bud waiting to be steeped.");
        world.addAtEmptyLocation(budlet, depth);
        return budlet;
    }

    public Item newSecateurs(int depth) {
        Item secateurs = new Item((char)251, new Color(255, 255, 255), "secateurs", 2000, "Look at this pair of pruning clippers!");
        world.addAtEmptyLocation(secateurs, depth);
        return secateurs;
    }

    public Item newDemitasse(int depth) {
        Item demitasse = new Item((char)235, new Color(255, 255, 255), "demitasse", 2000, "A minimal demitasse.");
        world.addAtEmptyLocation(demitasse, depth);
        return demitasse;
    }

    public Item newSaucer(int depth) {
        Item saucer = new Item((char)9, new Color(255, 255, 255), "saucer", 2000, "A minimal saucer.");
        world.addAtEmptyLocation(saucer, depth);
        return saucer;
    }

    // ---- Random Colour -- //

    public Color colorMe() {
        Color[] newcolor = {new Color(0, 100, 0), new Color(0, 255, 0), new Color(0, 160,0)};
        return newcolor[rng.nextInt(3)];
    }

    public Color colorMe1() {
        Color[] newcolor = {new Color(0, 157, 150), new Color(141, 255, 122), new Color(186, 117, 148)};
        return newcolor[rng.nextInt(3)];
    }

}

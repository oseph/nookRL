package nook;

import java.awt.*;
import java.util.Random;

public class FungusAi extends CreatureAi {

    private StuffFactory factory;
    private int spreadcount;
    private Random rng;
    private boolean canSpread;

    public FungusAi(Creature creature, StuffFactory factory, boolean canSpread) {
        super(creature);
        this.factory = factory;
        this.rng = new Random();
        this.canSpread = canSpread;
    }

    public void onUpdate(){
        if (canSpread) {
            if (spreadcount < 3 && Math.random() < 0.0025) {
                spread();
            }
        }
    }

    private void spread(){
        int x = creature.x + rng.nextInt(3)-1;
        int y = creature.y + rng.nextInt(3)-1;

        Tile tile = creature.tile(x,y, creature.z);
        if (creature.canEnter(x, y, creature.z) && tile == Tile.FLOOR && !tile.isStairs()) {

            creature.doAction("spawn a child");

            Creature child = factory.newFungus(creature.z, false);
            child.x = x;
            child.y = y;
            child.z = creature.z;
            spreadcount++;
        }

    }
}

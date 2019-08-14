package nook;

import java.util.Random;

/**
 * Created by Josh on 2016-12-24.
 */
public class PetalTreeAi extends CreatureAi {

    private StuffFactory factory;
    private Random rng;
    private World world;

    public PetalTreeAi(Creature creature, StuffFactory factory, World world) {
        super(creature);
        this.factory = factory;
        this.rng = new Random();
        this.world = world;
    }

    public void onUpdate() {
        if (countPetalRadius() < 5 && Math.random() < 0.025) {
            spread();
        }
    }

    private void spread() {
        int x = creature.x + rng.nextInt(3)-1;
        int y = creature.y + rng.nextInt(3)-1;

        Tile tile = creature.tile(x,y, creature.z);
        if (tile.isGround() && !tile.isStairs() && world.item(x,y,creature.z) == null) {
            factory.newPetalDrop(x,y, creature.z);
        }
    }

    private int countPetalRadius() {
        int r = 2;
        int count= 0;
        int x = creature.x;
        int y = creature.y;
        int z = creature.z;
        for (int ox = -r; ox < r + 1; ox++) {
            for (int oy = -r; oy < r + 1; oy++) {

                if (ox + x >= 0 && ox + x < world.width() && oy + y >= 0 && oy + y < world.height() && world.item(ox+x,oy+y,z) != null) {

                    if (world.item(ox+x,oy+y,z).name().equals("petal")) {
                        count++;
                    }

                }
            }
        }
        return count;
    }

}

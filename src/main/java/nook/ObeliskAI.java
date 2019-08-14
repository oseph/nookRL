package nook;

import java.awt.*;
import java.util.List;

/**
 * Created by Josh on 2016-12-07.
 */
public class ObeliskAI extends CreatureAi {

    private Creature player;
    private int invLoc = 0;
    int stagger = 0;

    public ObeliskAI(Creature creature, Creature player) {
        super(creature);
        this.player = player;
    }

    public void onUpdate() {
        if (creature.canSee(player.x, player.y, player.z)){
            hunt(player);
            stagger++;
        }

    }

    public void hunt(Creature target) {
        if (Math.random() < 0.2 && invLoc < 10 && stagger > 3) {
            if (!creature.inventory().isEmpty()) {
                Item toThrow = creature.inventory().get(invLoc);
                creature.throwItem(toThrow, player.x, player.y, player.z);
                player.notify("spits!");
                invLoc++;
                stagger = 0;
            }
        }
    }
}

package nook;

import java.util.List;


public class BrambleAI extends CreatureAi {


    private Creature player;

    public BrambleAI(Creature creature, Creature player) {
        super(creature);
        this.player = player;
    }

    public void onUpdate() {
        if (Math.random() < 0.3) {
            creature.doAction("stumble");
            return; }
        if (creature.canSee(player.x, player.y, player.z)) {
            hunt(player);
        } else {
            wander();
        }
    }

    public void hunt(Creature target) {
        if (!creature.inventory().isEmpty()) {
            Item toThrow = creature.inventory().get(0);
            creature.throwItem(toThrow, player.x, player.y, player.z);
            player.notify("The bramble spits thorns at you.");
        }

        List<Point> points = new Path(creature, target.x, target.y).points();
        int mx = points.get(0).x - creature.x;
        int my = points.get(0).y - creature.y;

        creature.moveBy(mx,my,0);

    }

    public void wander() {

        int mx = (int)(Math.random() * 3) - 1;
        int my = (int)(Math.random() * 3) - 1;

        Creature other = creature.creatureAt(creature.x + mx, creature.y + my, creature.z);

        if (other != null && other.glyph() == creature.glyph())
            return;
        else
            creature.moveBy(mx, my, 0);

    }
}

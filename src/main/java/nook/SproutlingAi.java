package nook;

import java.util.List;

public class SproutlingAi extends CreatureAi {

    private Creature player;

    public SproutlingAi(Creature creature, Creature player) {
        super(creature);
        this.player = player;
    }

    public void onUpdate(){
        if (creature.canSee(player.x, player.y, player.z)) {
            hunt(player);
        } else {
            wander();
        }
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

    public void hunt(Creature target) {
        List<Point> points = new Path(creature, target.x, target.y).points();
        int mx = points.get(0).x - creature.x;
        int my = points.get(0).y - creature.y;
        creature.moveBy(mx,my,0);
    }

}

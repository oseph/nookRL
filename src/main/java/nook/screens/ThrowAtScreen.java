package nook.screens;

import nook.Creature;
import nook.Item;
import nook.Line;
import nook.Point;
import nook.World;

public class ThrowAtScreen extends TargetBasedScreen {
    private Item item;
    private World world;

    public ThrowAtScreen(World world, Creature player, int sx, int sy, Item item) {
        super(world, player, "Throw " + item.name() + " at?", sx, sy);
        this.item = item;
    }

    public boolean isAcceptable(int x, int y) {
        if (!player.canSee(x,y,player.z)){
            return false;
        }

        for (Point p: new Line(player.x, player.y, x, y)) {
            if (!player.realTile(p.x, p.y, player.z).isGround()) {
                return false;
            }
        }
        return true;
    }

    public void selectWorldCoordinate(int x, int y, int screenX, int screenY){
        player.throwItem(item, x, y, player.z);
    }
}

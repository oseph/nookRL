package nook.screens;

import nook.Creature;
import nook.Item;
import nook.World;

/**
 * Created by Josh on 2017-01-25.
 */
public class ThrowScreen extends InventoryBasedScreen {
    private int sx;
    private int sy;
    private World world;

    public ThrowScreen(World world, Creature player, int sx, int sy) {
        super(player);
        this.sx = sx;
        this.sy = sy;
        this.world = world;
    }

    protected String getVerb() {
        return "throw";
    }

    @Override
    protected boolean isAcceptable(Item item) {
        return true;
    }

    protected Screen use(Item item){
        return new ThrowAtScreen(world, player,sx,sy,item);

    }
}

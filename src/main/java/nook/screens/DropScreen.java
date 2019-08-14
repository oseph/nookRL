package nook.screens;

import nook.Creature;
import nook.Item;

/**
 * Created by Josh on 2016-11-27.
 */
public class DropScreen extends InventoryBasedScreen {

    public DropScreen(Creature player) {
        super(player);
    }

    protected String getVerb() {
        return "drop";
    }

    protected boolean isAcceptable(Item item){
        return true;
    }

    protected Screen use(Item item) {
        player.drop(item);
        return null;
    }

}

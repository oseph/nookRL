package nook.screens;

import asciiPanel.AsciiPanel;
import nook.Creature;
import nook.Item;
import nook.Tile;
import nook.World;

/**
 * Created by Josh on 2017-01-20.
 */
public class LookScreen extends TargetBasedScreen {

    public LookScreen(World world, Creature player, String caption, int sx, int sy) {
        super(world, player, "", sx, sy);
    }

    public void displayOutput(AsciiPanel terminal){
        super.displayOutput(terminal);
        terminal.writeCenter("Looking around.", 35);
        terminal.writeCenter("Press [esc] to return", 36);
    }

    public void enterWorldCoordinate(int x, int y, int screenX, int screenY) {
        Creature creature = player.creatureAt(x, y, player.z);
        if (creature != null) {
            caption = creature.description();
            return;
        }

        Item item = player.itemAt(x, y, player.z);
        if (item != null) {
            caption = item.description();
            return;
        }

        Tile tile = player.tile(x, y, player.z);
        caption = tile.description();
    }


}

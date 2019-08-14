package nook.screens;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import nook.Creature;
import nook.Item;
import asciiPanel.AsciiPanel;

/**
 * Created by Josh on 2016-11-27.
 */
public abstract class InventoryBasedScreen implements Screen {

    protected Creature player;
    private String letters;

    protected abstract String getVerb();
    protected abstract boolean isAcceptable(Item item);
    protected abstract Screen use(Item item);

    public InventoryBasedScreen(Creature player) {
        this.player = player;
        this.letters = "abcdefghijklmnopqrstuvwxyz";
    }

    public void displayOutput(AsciiPanel terminal) {
        ArrayList<String> lines = getList();

        int y = 5;
        int x = 2;

        //Set the menu Colours
        terminal.setDefaultBackgroundColor( Color.black);
        terminal.setDefaultForegroundColor( AsciiPanel.brightWhite);

        // if you have items to drop
        if (lines.size() > 0) {
            terminal.clear(' ', 1, 2, 23+getVerb().length()+2, lines.size()+4);
            for (String line: lines) {
                terminal.write(line,x,y++);
            }
            terminal.clear(' ',1, 1, 30, 3);
            terminal.write("What would you like to " + getVerb() + "?", 2, 2);
        } else {
            terminal.clear(' ',1, 1, 30, 3);
            terminal.write("You have nothing to " + getVerb() + ".", 2, 2);
        }

        terminal.repaint();
        terminal.setDefaultBackgroundColor( Color.black);
        terminal.setDefaultForegroundColor( AsciiPanel.brightWhite);
    }

    public Screen respondToUserInput(KeyEvent key) {
        char c = key.getKeyChar();

        Item[] items = player.inventory().getItems();

        if (letters.indexOf(c) > -1
            && items.length > letters.indexOf(c)
            && items[letters.indexOf(c)] != null
            && isAcceptable(items[letters.indexOf(c)])) {
            return use(items[letters.indexOf(c)]);
        } else if (key.getKeyCode() == KeyEvent.VK_ESCAPE) {
            return null;
        } else { return this; }
    }

    private ArrayList<String> getList() {
        ArrayList<String> lines = new ArrayList<String>();
        Item[] inventory = player.inventory().getItems();

        for (int i = 0; i <inventory.length; i++){
            Item item = inventory[i];
            if (item == null || !isAcceptable(item)) { continue; }

            String line = letters.charAt(i) + " - " + item.glyph() +  " " + item.name();

            lines.add(line);
        }
        return lines;
    }
}

package nook.screens;

/**
 * Created by Josh on 2017-01-20.
 */

import java.awt.*;
import java.awt.event.KeyEvent;
import nook.Creature;
import nook.Line;
import nook.Point;
import nook.World;
import asciiPanel.AsciiPanel;

public abstract class TargetBasedScreen implements Screen {
    protected Creature player;
    protected String caption;
    private World world;
    private int sx;
    private int sy;
    private int x;
    private int y;

    public TargetBasedScreen(World world, Creature player, String caption, int sx, int sy){
        this.player = player;
        this.caption = caption;
        this.sx = sx;
        this.sy = sy;
        this.world = world;
    }

    public void displayOutput(AsciiPanel terminal) {
        terminal.clear(' ', 0, 34, 40, 6,AsciiPanel.brightWhite, new Color(12,12,12));
        for (Point p: new Line(sx, sy, sx+x, sy+y)) {
            if (sx+x < 0 || sx+x > world.width()-1 || sy+y < 0 || sy+y > world.height()-1) {
                continue;
            }
                char curr;
                if(player.canSee(sx+x, sy+y, player.z)) { curr = world.glyph(sx+x, sy+y, player.z);} else {
                curr = ' ';}
                terminal.write('*', p.x, p.y, AsciiPanel.brightGreen);
                terminal.write(player.glyph(), sx, sy);
                terminal.write(curr, sx+x, sy+y, AsciiPanel.black, AsciiPanel.brightGreen);

        }
        terminal.clear(' ', 0, 37, 40, 1);
        if (sy + y < 11) {
            terminal.writeCenter(caption, 31);
        } else {
            terminal.writeCenter(caption, 5);
        }
    }

    public Screen respondToUserInput(KeyEvent key) {
        int prevX = x;
        int prevY = y;

        switch (key.getKeyCode()){
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_H:  x--; break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_L: x++; break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_J: y--; break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_K: y++; break;
            case KeyEvent.VK_Y: x--; y--; break;
            case KeyEvent.VK_U: x++; y--; break;
            case KeyEvent.VK_B: x--; y++; break;
            case KeyEvent.VK_N: x++; y++; break;
            case KeyEvent.VK_ENTER: selectWorldCoordinate(player.x + x, player.y + y, sx + x, sy + y); return null;
            case KeyEvent.VK_ESCAPE: return null;
        }


        enterWorldCoordinate(player.x + x, player.y + y, sx + x, sy + y);
        if (sx+x > world.width()-1 || sx+x < 0) { x = prevX;}
        if (sy+y > world.height()-1 || sx+y < 0) { y = prevY;}
        return this;
    }

    public boolean isAcceptable(int x, int y) {
        return true;
    }

    public void enterWorldCoordinate(int x, int y, int screenX, int screenY) {
    }

    public void selectWorldCoordinate(int x, int y, int screenX, int screenY){
    }


}
package nook.screens;

import java.awt.*;
import java.awt.event.KeyEvent;
import asciiPanel.AsciiPanel;

import static java.awt.Color.gray;

public class HelpScreen implements Screen {
    @Override
    public void displayOutput(AsciiPanel terminal) {
        terminal.clear();
        terminal.setDefaultForegroundColor(AsciiPanel.brightWhite);
        terminal.writeCenter("NOOK", 1, AsciiPanel.brightGreen);
        terminal.writeCenter("As you settle into your cozy nook", 3);
        terminal.writeCenter("you suddenly realize you've steeped", 4);
        terminal.writeCenter("your last budlet of tea.", 5);
        terminal.writeCenter("Head outside and find a", 7);
        terminal.writeCenter("fresh budlet in the caves below.", 8);
        horizontalFooter(terminal, 10);
        terminal.writeCenter(" HOW TO PLAY ", 10, AsciiPanel.brightGreen);
        int y = 40;
        int off = 28;
        terminal.write("arrow keys OR hjklbnyu - move around", 2, y - off--);
        terminal.write("; - to examine your surroundings", 2, y - off--);
        terminal.write("i - to view inventory", 2, y - off--);
        terminal.write("t - to throw items", 2, y - off--);
        terminal.write("g - or [,] to pick up items", 2, y - off--);
        terminal.write("d - to drop", 2, y- off--);
        terminal.write("p - to consume 5 petals for health", 2, y- off--);
        terminal.write("> - to go downstairs ("+(char)240+")", 2, y- off--);
        terminal.write("< - to up upstairs ("+(char)240+")", 2, y- off--);
        off--;
        horizontalFooter(terminal, y- off--);
        terminal.writeCenter("Walls react to carried petals.", y- (off--), AsciiPanel.green);
        terminal.writeCenter("Try to collect all the items.", y- (off--), AsciiPanel.green);

        terminal.writeCenter("[press any key to continue]", 38);
    }
    @Override
    public Screen respondToUserInput(KeyEvent key) {
        return null;
    }

    private void horizontalFooter(AsciiPanel terminal, int height){
        for (int i = 1; i < 39 ; i++) {
            terminal.write('-', i, height, gray);
        }
    }
}
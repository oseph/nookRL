package nook.screens;

import java.awt.event.KeyEvent;

import asciiPanel.AsciiPanel;
import nook.Creature;
import static java.awt.Color.gray;

public class LoseScreen implements Screen {

    private int screenWidth;
    private int screenHeight;
    private Creature player;

    public LoseScreen(Creature player) {
        screenWidth = 40;
        screenHeight = 34;
        this.player = player;

    }

	@Override
	public void displayOutput(AsciiPanel terminal) {
        terminal.writeCenter("You lose.", 3);
        horizontalFooter(terminal, 2);
        horizontalFooter(terminal, 4);
        terminal.writeCenter("Number of times healed: " + player.petalHealCount(), 10);
		terminal.writeCenter("-- press [enter] to restart --", 35);
        terminal.writeCenter("-- press [esc] to return to title --", 36);

	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
	    switch (key.getKeyCode()){
            case KeyEvent.VK_ENTER:
                return new PlayScreen();
            case KeyEvent.VK_ESCAPE:
                return new StartScreen();
        }
        return this;
	}

    private void horizontalFooter(AsciiPanel terminal, int height){
        for (int i = 1; i < screenWidth-1 ; i++) {
            terminal.write('-', i, height, gray);
        }
    }
}

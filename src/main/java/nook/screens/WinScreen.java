package nook.screens;

import java.awt.event.KeyEvent;
import asciiPanel.AsciiPanel;
import static java.awt.Color.*;
import nook.Creature;

public class WinScreen implements Screen {

    private int screenWidth;
    private int screenHeight;
    private Creature player;
    int petalHealCount;

    public WinScreen(Creature player) {
        this.player = player;
        screenWidth = 40;
        screenHeight = 34;
    }

	@Override
	public void displayOutput(AsciiPanel terminal) {
		terminal.writeCenter("You won!", 3);
		horizontalFooter(terminal, 2);
        horizontalFooter(terminal, 4);
        terminal.writeCenter("You safely return to your nook", 12);
        terminal.writeCenter("with a freshly picked budlet.", 13);
        terminal.writeCenter("Number of times healed: " + player.petalHealCount(), 15);
        terminal.writeCenter("Your final score: " + player.score(), 17
                , AsciiPanel.brightGreen);
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

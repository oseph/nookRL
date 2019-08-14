package nook.screens;


import java.awt.event.KeyEvent;

import asciiPanel.AsciiPanel;

import static java.awt.Color.gray;

public class StartScreen implements Screen {

    @Override
    public void displayOutput(AsciiPanel terminal) {
        horizontalFooter(terminal, 12);
        horizontalFooter(terminal, 16);
        terminal.writeCenter("Nook", 14 , AsciiPanel.brightGreen);
        terminal.writeCenter("Start [enter]", 22, AsciiPanel.brightWhite);
        terminal.writeCenter("Quit [escape]", 23, AsciiPanel.brightWhite);
        terminal.writeCenter("v.0.1.2", 28);
    }

    @Override
    public Screen respondToUserInput(KeyEvent key) {

        switch (key.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
            case KeyEvent.VK_ENTER:
                return new PlayScreen();
        }
        return this;
    }

    private void horizontalFooter(AsciiPanel terminal, int height){
        for (int i = 12; i < 27 ; i++) {
            terminal.write('-', i, height, gray);
        }
    }
}


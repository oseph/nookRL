/**
* NookRL
* A roguelike created by Josh Holinaty.
*
*   "Find the budlet in the caves below so you can have a warm
*    cup of tea before you go to bed."
*
* This personal project is/was a learning excerise in understanding 
* Java, Gradle, Git. 
*
* Thanks to Trystan for the wonderful Java roguelike tutorial which
* this is mostly based on and modified.
* http://trystans.blogspot.ca/2016/01/roguelike-tutorial-00-table-of-contents.html
*
*/

package nook;

import javax.swing.JFrame;
import asciiPanel.AsciiPanel;
import asciiPanel.AsciiFont;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import nook.screens.Screen;
import nook.screens.StartScreen;

public class ApplicationMain extends JFrame implements KeyListener {

	private static final long serialVersionUID = 1060623638149583738L;
	private AsciiPanel terminal;
	private Screen screen;

	public ApplicationMain(){
		super();
		terminal = new AsciiPanel(40,40, AsciiFont.CP437_16x16);
		add(terminal);
		pack();
		screen = new StartScreen();
		addKeyListener(this);
		repaint();
	}


	@Override
	public void repaint(){
		terminal.clear();
		screen.displayOutput(terminal);
		super.repaint();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		screen = screen.respondToUserInput(e);
		repaint();
	}

	@Override
	public void keyReleased(KeyEvent e) { }

	@Override
	public void keyTyped(KeyEvent e) { }

	public static void main(String[] args) {
		ApplicationMain app = new ApplicationMain();
		app.setTitle("Nook");
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		app.setLocationRelativeTo(null);
		app.setResizable(false);
		app.setVisible(true);

	}
}

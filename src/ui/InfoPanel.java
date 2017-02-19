package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import game.GameState;

/*
 * A UI panel for displaying additional information about the game.
 */
public class InfoPanel extends JPanel {
	
	private int[] scores;
	
	public static final int PANEL_WIDTH = OthelloFrame.FRAME_WIDTH;
	public static final int PANEL_HEIGHT = 40;

	private static final long serialVersionUID = 1895792502596588154L;

	public InfoPanel() {
		this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
		this.setBackground(Color.GRAY);
		this.scores = new int[2];
	}
	
	public void updateUI(GameState game) {
		
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawString("INFO", PANEL_WIDTH, PANEL_HEIGHT);
	}

}

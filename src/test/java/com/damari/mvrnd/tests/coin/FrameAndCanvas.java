package com.damari.mvrnd.tests.coin;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class FrameAndCanvas extends JPanel {

	private static final long serialVersionUID = 1L;

	private BufferedImage canvas;

	public FrameAndCanvas(String topic, int width, int height, Color bgColor) {
		canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		fillCanvas(bgColor);

		JFrame frame = new JFrame(topic);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(dim.width / 2 - width / 2, dim.height / 2 - height / 2);
		frame.add(this);
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public Dimension getPreferredSize() {
		return new Dimension(canvas.getWidth(), canvas.getHeight());
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(canvas, null, null);
	}

	public void fillCanvas(Color c) {
		int color = c.getRGB();
		for (int x = 0; x < canvas.getWidth(); x++) {
			for (int y = 0; y < canvas.getHeight(); y++) {
				canvas.setRGB(x, y, color);
			}
		}
		repaint();
	}

	public void text(int x, int y, String text, Color c, Font f) {
		Graphics2D g2d = (Graphics2D) canvas.getGraphics();
		g2d.setPaint(c);
		g2d.setFont(f);
		g2d.drawString(text, x, y);
		g2d.dispose();
        repaint();
	}

	public void plot(int x, int y, int rgb) {
		canvas.setRGB(x, y, rgb);
		repaint();
	}

	public int getPixelColor(int x, int y) {
		return canvas.getRGB(x, y);
	}

	public void drawRect(Color c, int x1, int y1, int width, int height) {
		int color = c.getRGB();
		for (int x = x1; x < x1 + width; x++) {
			for (int y = y1; y < y1 + height; y++) {
				canvas.setRGB(x, y, color);
			}
		}
		repaint();
	}

}

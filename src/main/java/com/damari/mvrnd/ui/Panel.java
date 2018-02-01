package com.damari.mvrnd.ui;

import static com.damari.mvrnd.algorithm.Strategy.round;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.MouseInputAdapter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.damari.mvrnd.coin.Coin;
import com.damari.mvrnd.coin.CoinSecureRandom;
import com.damari.mvrnd.coin.CoinXoRoShiRo128PlusRandom;
import com.damari.mvrnd.data.DataGenerator;
import com.damari.mvrnd.order.Broker;

public class Panel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(Panel.class.getName());

	private static final Font axisFont = new Font("Arial", Font.BOLD, 12);
	private static final Color axisColor = Color.BLACK;

	private static final Font labelFont = new Font("Arial", Font.BOLD, 16);
	private static final Color labelColor = Color.GRAY;

	private static final Font priceDescFont = new Font("Arial", Font.BOLD, 10);
	private static final Color priceDescColor = Color.BLUE;

	private int winW, winH;

	private int gridX1, gridY1, gridX2, gridY2;

	private DataGenerator asset;

	private ArrayList<ColoredRectangle> coloredRectangles = new ArrayList<ColoredRectangle>();
	private Rectangle shape;

	public Panel(int winWidth, int winHeight) {
		this.winW = winWidth;
		this.winH = winHeight;

		this.gridX1 = 105;
		this.gridX2 = winW - 60;
		this.gridY1 = 50;
		this.gridY2 = winH - 120;

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
		}

		JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame frame = new JFrame("Random Walk");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(winW, winH);
		frame.setResizable(false);
		frame.setLocationRelativeTo( null );
		setBackground(Color.WHITE);

		frame.getContentPane().add(this);

		ButtonPanel buttonPanel = new ButtonPanel(this);
		frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		MyMouseListener ml = new MyMouseListener();
		addMouseListener(ml);
		addMouseMotionListener(ml);

		frame.setVisible(true);
	}

	public void drawStockPrice(Graphics g) {
		Coin coin = new CoinXoRoShiRo128PlusRandom(50.050f); // visible at 50.05 and higher
		//Coin coin = new CoinSecureRandom(50.02f); // visible at 50.04 and higher
		//Coin coin = new Coin(50.10f); // visible at 50.04 and higher

		int colorRGB = 250;
		float yArea = gridY2 - gridY1;
		if (asset == null) {
			asset = new DataGenerator();
		}
		Broker broker = new Broker();
		for (int stockIdx = 0; stockIdx < 100; stockIdx++) {
			broker
				.reset((int)(50_000.00f * 100f))
				.setCommissionPercent(0.0025f);

			int iterations = 3_000_000;
			long time = DateTime.parse("2016-10-03").getMillis();

//			int startPrice = (int)(75.60f * 100f);
//			int spread = (int)(0.05f * 100f);

			int startPrice = (int)(257.60f * 100f);
			int spread = (int)(0.10f * 100f);

			long timeStep = 300;
			int size = asset.generateRandomWalk(coin, iterations, time, startPrice, spread, timeStep);

			float minPrice = (int)(asset.getMinPrice() / 100f);
			float maxPrice = (int)(asset.getMaxPrice() / 100f);
			float minMaxDiff = maxPrice - minPrice;
			g.setColor(new Color(colorRGB, colorRGB, colorRGB));
			for (int i = 0; i < size; i++) {
				if (i % 40 == 0) {
					float xArea = gridX2 - gridX1;
					int x = gridX1 + (int)(xArea / (float)size * (float)i);

					float price = asset.getPrice(i) / 100f;
					int y = gridY1 + (int)(yArea / minMaxDiff * (maxPrice - price));

					g.drawRect(x, y, 0, 0);
				}
			}

			final int colMin = 20;
			final int colStep = 3;
			colorRGB = colorRGB >= colMin + colStep ? colorRGB - colStep : colMin;
		}

	}

	// Only process last stock
	//int maxPositions = 6;
	//int tradeSize = 50;
	//BigDecimal distance = BigDecimal.valueOf(3);
	//Algorithm algo = new GridOne(broker, maxPositions, tradeSize, distance);
	//algo.process(stock);

	// Price labeling for each 100k
//		if (i % 100_000 == 0) {
//			// X-axis label
//			g.setFont(AXIS_FONT);
//			g.setColor(Color.BLACK);
//			g.drawString(String.valueOf(i), x + 2, gridY2 + 13);
//
//			// Price desc
//			g.setFont(PRICE_DESC_FONT);
//			g.setColor(PRICE_DESC_COLOR);
//			g.drawLine(x + 2, y - 2, x + 15, y - 20);
//			g.drawLine(x + 15, y - 20, x + 92, y - 20);
//			String deltaLabel = "";
//			if (prevPrice != null) {
//				BigDecimal priceChange = price.subtract(prevPrice).divide(price, 5, BigDecimal.ROUND_UP).multiply(BigDecimal.valueOf(100));
//				String deltaPercent = priceChange.setScale(2, BigDecimal.ROUND_UP).toString();
//				deltaLabel = " (Δ" + deltaPercent + "%)";
//			}
//			g.drawString(String.valueOf(price) + deltaLabel, x + 17, y - 22);
//
//			prevPrice = price;
//		}

	private void drawLabels(Graphics g) {
		g.setFont(labelFont);
		g.setColor(labelColor);
		g.drawString("y is price", gridX2 - 60, gridY1 + 20);
		g.drawString("x is time", gridX2 - 60, gridY1 + 40);
		g.drawRect(gridX2 - 70, gridY1, 90, 50);
	}

	private void drawAxis(Graphics g) {
		g.setFont(axisFont);
		g.setColor(axisColor);

		g.drawLine(gridX1 - 20, gridY2, gridX2, gridY2); // X-axis
		g.drawLine(gridX1, gridY1, gridX1, gridY2 + 20); // Y-axis

		float maxPrice = asset.getMaxPrice() / 100f;
		float yArea = gridY2 - gridY1;
		int y = gridY1 + (int)(yArea / maxPrice * maxPrice);

		g.drawString("y(min): " + round(asset.getMinPrice()), gridX1 - 80, y);
		g.drawString("y(max): " + round(asset.getMaxPrice()), gridX1 - 80, gridY1 + 10);
		g.drawString("y(mid): " + round(asset.getMidPrice()), gridX1 - 80, y - (y - gridY1 + 10) / 2);
	}

	private void drawUser(Graphics g) {
		Color foreground = g.getColor();
		for (Panel.ColoredRectangle cr : coloredRectangles) {
			g.setColor(cr.getForeground());
			Rectangle r = cr.getRectangle();
			g.drawRect(r.x, r.y, r.width, r.height);
		}
		if (shape != null) {
			Graphics2D g2d = (Graphics2D)g;
			g2d.setColor(foreground);
			g2d.draw(shape);
		}
	}

	public void addRectangle(Rectangle rectangle, Color color) {
		ColoredRectangle cr = new ColoredRectangle(color, rectangle);
		coloredRectangles.add(cr);
		repaint();
	}

	public void run() {
		coloredRectangles.clear();
		asset.generateRandomWalk(new CoinSecureRandom(), 1000, new DateTime().getMillis(),
				(int)(80.00f * 100f), (int)(0.10f * 100f), 345);
		repaint();
	}

	public void clear() {
		coloredRectangles.clear();
		repaint();
	}

	public void setAsset(DataGenerator asset) {
		this.asset = asset;
		this.repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		drawStockPrice(g);
		drawLabels(g);
		drawAxis(g);
		drawUser(g);
	}

	class MyMouseListener extends MouseInputAdapter {
		private Point startPoint;

		public void mousePressed(MouseEvent e) {
			startPoint = e.getPoint();
			shape = new Rectangle();
		}

		public void mouseDragged(MouseEvent e) {
			int x = Math.min(startPoint.x, e.getX());
			int y = Math.min(startPoint.y, e.getY());
			int width = Math.abs(startPoint.x - e.getX());
			int height = Math.abs(startPoint.y - e.getY());

			shape.setBounds(x, y, width, height);
			repaint();
		}

		public void mouseReleased(MouseEvent e) {
			if (shape.width != 0 || shape.height != 0) 	{
				addRectangle(shape, e.getComponent().getForeground());
			}
			shape = null;
		}
	}

	static class ColoredRectangle {
		private Color foreground;
		private Rectangle rectangle;

		public ColoredRectangle(Color foreground, Rectangle rectangle) {
			this.foreground = foreground;
			this.rectangle = rectangle;
		}

		public Color getForeground() {
			return foreground;
		}

		public void setForeground(Color foreground) {
			this.foreground = foreground;
		}

		public Rectangle getRectangle() {
			return rectangle;
		}
	}

}

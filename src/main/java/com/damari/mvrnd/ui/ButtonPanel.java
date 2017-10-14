package com.damari.mvrnd.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class ButtonPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;

	private Panel drawingArea;

	public ButtonPanel(Panel drawingArea) {
		this.drawingArea = drawingArea;

		add(createButton("Run", Color.BLACK));
		add(createButton("Red", Color.RED));
		add(createButton("Blue", Color.BLUE));
		add(createButton("Clear", null));
	}

	private JButton createButton(String text, Color background) {
		JButton button = new JButton(text);
		button.setBackground(background);
		button.addActionListener(this);
		return button;
	}

	public void actionPerformed(ActionEvent e) {
		JButton button = (JButton)e.getSource();
		if("Run".equals(e.getActionCommand())) {
			drawingArea.run();
		} else if("Clear".equals(e.getActionCommand())) {
			drawingArea.clear();
		} else {
			drawingArea.setForeground(button.getBackground());
		}
	}

}
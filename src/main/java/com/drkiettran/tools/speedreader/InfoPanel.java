package com.drkiettran.tools.speedreader;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.Border;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InfoPanel extends JPanel {

	private static final long serialVersionUID = -6861378863577764057L;
	private JLabel fileNameLabel;
	private JTextPane resultText;
	protected Integer selectedPageNo = null;
	private MainFrame mainFrame;
	private String messageText = "";

	public Integer getSelectedPageNo() {
		return selectedPageNo;
	}

	public InfoPanel() {
		arrangeFixedComponents();
		makeTextArea();
		setBorder();
		arrangeLayout();
	}

	private void arrangeLayout() {
		setLayout(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane(resultText);
		scrollPane.getViewport().setPreferredSize(new Dimension(100, 100));
		add(scrollPane, BorderLayout.CENTER);
		add(fileNameLabel, BorderLayout.SOUTH);
	}

	private void setBorder() {
		Border innerBorder = BorderFactory.createTitledBorder("Results");
		Border outterBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		setBorder(BorderFactory.createCompoundBorder(outterBorder, innerBorder));

	}

	private void makeTextArea() {
		resultText = new JTextPane();
		resultText.setCaretPosition(0);
		resultText.setCaretColor(Color.white);
		resultText.setContentType("text/html");
		resultText.setText("");

		resultText.addMouseListener(getMouseListner());
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(InfoPanel.class);

	private boolean isNumber(String selectedPageNoStr) {
		for (int idx = 0; idx < selectedPageNoStr.length(); idx++) {
			if (!Character.isDigit(selectedPageNoStr.charAt(idx))) {
				return false;
			}
		}
		return true;
	}

	private MouseListener getMouseListner() {

		return new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				String selectedPageNoStr = resultText.getSelectedText();
				if (selectedPageNoStr != null && isNumber(selectedPageNoStr)) {
					selectedPageNo = Integer.valueOf(selectedPageNoStr);
					mainFrame.goToPage(selectedPageNo);
				}
				LOGGER.info("1. text: '{}'", resultText.getSelectedText());

			}

			@Override
			public void mousePressed(MouseEvent e) {
				LOGGER.info("2. text: '{}'", resultText.getSelectedText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				LOGGER.info("3. text: '{}'", resultText.getSelectedText());
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				LOGGER.info("4. text: '{}'", resultText.getSelectedText());
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

		};
	}

	private void arrangeFixedComponents() {
		fileNameLabel = new JLabel("file:");
		fileNameLabel.setFont(new Font("Candara", Font.PLAIN, 12));
	}

	public void setFileName(String fileName) {
		fileNameLabel.setText("file: " + fileName);
	}

	public void addText(String text) {
		messageText = new StringBuilder(messageText).append("<br>").append(text).append("<br>").toString();
		resultText.setText(messageText);
	}

	public void clearText() {
		messageText = "";
		resultText.setText(messageText);
	}

	public void setFrame(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}
}

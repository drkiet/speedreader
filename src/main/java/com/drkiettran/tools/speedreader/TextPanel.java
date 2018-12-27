package com.drkiettran.tools.speedreader;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultCaret;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drkiettran.tika.text.ReadingTextManager2;
import com.drkiettran.tika.text.TextApp;
import com.drkiettran.tools.speedreader.ReaderListener.Command;

public class TextPanel extends JPanel {
	private static final Logger logger = LoggerFactory.getLogger(TextPanel.class);

	private static final int DEFAULT_DISPLAYING_FONT_SIZE = 60;
	private static final int DEFAULT_TEXT_AREA_FONT_SIZE = 18;
	private static final int SMALLEST_DISPLAYING_FONT_SIZE = 20;
	private static final int SMALLEST_TEXT_AREA_FONT_SIZE = 10;
	private static final int LARGEST_DISPLAYING_FONT_SIZE = 100;
	private static final int LARGEST_TEXT_AREA_FONT_SIZE = 32;
	private static final long serialVersionUID = -825536523977292110L;
	private String helpText = loadHelpText();
	private JTextArea textArea;
	private JLabel displayingWordLabel;
	private String readingText = null;
	private JLabel infoLabel;
	private JLabel titleLabel;
	private ReaderListener readerListener;
	private ReadingTextManager2 readingTextManager;

	private String displayingFontName = "Candara";
	private int displayingWordFontSize = DEFAULT_DISPLAYING_FONT_SIZE;
	private String textAreaFontName = "Times New Roman";
	private int textAreaFontSize = DEFAULT_TEXT_AREA_FONT_SIZE;
	private int defaultBlinkRate = 0;
	private boolean doneReading = false;

	public boolean isDoneReading() {
		return doneReading;
	}

	public TextPanel() {
		arrangeFixedComponents();
		makeTextArea();
		setBorder();
		arrangeLayout();
	}

	private void arrangeFixedComponents() {
		displayingWordLabel = new JLabel("");
		displayingWordLabel.setFont(new Font(displayingFontName, Font.PLAIN, displayingWordFontSize));
		infoLabel = new JLabel("");
		titleLabel = new JLabel("Title:");
	}

	private void arrangeLayout() {
		setLayout(new BorderLayout());
		add(displayingWordLabel, BorderLayout.NORTH);
		add(new JScrollPane(textArea), BorderLayout.CENTER);
		add(titleLabel, BorderLayout.SOUTH);
		add(infoLabel, BorderLayout.SOUTH);
	}

	private void setBorder() {
		Border innerBorder = BorderFactory.createTitledBorder("Reading");
		Border outterBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		setBorder(BorderFactory.createCompoundBorder(outterBorder, innerBorder));
	}

	private void makeTextArea() {
		displayHelpText();
		defaultBlinkRate = textArea.getCaret().getBlinkRate();
		textArea.setCaretPosition(0);
		textArea.setCaret(new FancyCaret());
		textArea.setCaretColor(Color.red);
		textArea.setFont(new Font(textAreaFontName, Font.PLAIN, textAreaFontSize));
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);

		textArea.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				readerListener.invoke(Command.RESTART);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				readerListener.invoke(Command.RESTART);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				readerListener.invoke(Command.RESTART);
			}

		});
	}

	private void displayHelpText() {
		textArea = new JTextArea(helpText);
	}

	public void setHelpText(String helpText) {
		this.helpText = helpText;
	}

	private void restart() {
		readingTextManager = new ReadingTextManager2(helpText);
	}

	public void resetReading() {
		restart();
		readingText = null;
		textArea.setText(helpText);
		textArea.setCaret(new DefaultCaret());
		textArea.setCaretPosition(0);
		textArea.requestFocus();
		displayingWordLabel.setText("");
		infoLabel.setText("");
		repaint();
	}

	public void next() {

		if (readingText == null) {
			doneReading = false;
			readingText = textArea.getText();
			textArea.setText(readingText);
			readingTextManager = new ReadingTextManager2(readingText);
			textArea.setText(readingTextManager.getReadingText());
		}

		String wordToRead = getNextWord();
		StringBuilder sb = new StringBuilder();

		for (int idx = 0; idx < wordToRead.length(); idx++) {
			char curChar = wordToRead.charAt(idx);
			if (curChar == '-' || Character.isAlphabetic(curChar) || Character.isDigit(curChar)) {
				sb.append(curChar);
			}
		}

		wordToRead = sb.toString();
		logger.info("word2Read: {}", wordToRead);

		if (wordToRead != null) {
			textArea.setCaretPosition(readingTextManager.getCurrentCaret());
			textArea.requestFocus();
			displayingWordLabel.setText(wordToRead);
			displayReadingInformation(); 
		} else {
			displayReadingInformation();
			doneReading = true;
		}
		repaint();
	}

	private void displayReadingInformation() {
		int wordsFromBeginning = readingTextManager.getWordsFromBeginning();
		int totalWords = readingTextManager.getTotalWords();
		int readingPercentage = (100 * wordsFromBeginning) / totalWords;

		infoLabel.setText(String.format("%d of %d words (%d%%)", wordsFromBeginning, totalWords, readingPercentage));
		infoLabel.setForeground(Color.BLUE);
	}

	private String getNextWord() {
		return readingTextManager.getNextWord();
	}

	public void setReaderListener(ReaderListener readerListener) {
		this.readerListener = readerListener;
	}

	public void loadTextFromFile(String text) {
		restart();
		readingText = null;
		textArea.setText(text);
		displayingWordLabel.setText("");
		infoLabel.setText("");
		repaint();
	}

	private String loadHelpText() {
		try (InputStream is = TextApp.class.getResourceAsStream("/Helpfile.txt")) {
			StringBuilder sb = new StringBuilder();

			for (;;) {
				int c = is.read();
				if (c < 0) {
					break;
				}
				sb.append((char) c);
			}
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void stopReading() {
		textArea.setCaret(new DefaultCaret());
		textArea.getCaret().setBlinkRate(defaultBlinkRate);
		textArea.setCaretPosition(readingTextManager.getCurrentCaret());
		textArea.requestFocus();
	}

	public void startReading() {
		textArea.setCaret(new FancyCaret());
		if (readingTextManager != null) {
			textArea.setCaretPosition(readingTextManager.getCurrentCaret());
		} else {
			textArea.setCaretPosition(0);
		}
		textArea.requestFocus();
	}

	public void setCurrentCaretAt() {
		if (readingTextManager != null) {
			readingTextManager.setCurrentCaret(textArea.getCaretPosition());
		}
	}

	public void setLargerTextFont() {
		if (textAreaFontSize < LARGEST_TEXT_AREA_FONT_SIZE) {
			this.textAreaFontSize++;
		}
		textArea.setFont(new Font(textAreaFontName, Font.PLAIN, textAreaFontSize));
		repaint();
	}

	public void setSmallerTextFont() {
		if (textAreaFontSize > SMALLEST_TEXT_AREA_FONT_SIZE) {
			this.textAreaFontSize--;
		}
		textArea.setFont(new Font(textAreaFontName, Font.PLAIN, textAreaFontSize));
		repaint();
	}

	public void setLargerWordFont() {
		if (displayingWordFontSize < LARGEST_DISPLAYING_FONT_SIZE) {
			this.displayingWordFontSize++;
		}
		displayingWordLabel.setFont(new Font(displayingFontName, Font.PLAIN, displayingWordFontSize));
		repaint();
	}

	public void setSmallerWordFont() {
		if (displayingWordFontSize > SMALLEST_DISPLAYING_FONT_SIZE) {
			this.displayingWordFontSize--;
		}
		displayingWordLabel.setFont(new Font(displayingFontName, Font.PLAIN, displayingWordFontSize));
		repaint();
	}

	public int search(String searchText) {
		if (readingTextManager != null) {
			return readingTextManager.search(searchText);
		}
		return -1;
	}

	public void setInfo(String info) {
		if (readingTextManager != null) {
			infoLabel.setText(info);
			infoLabel.setForeground(Color.BLUE);
			textArea.setCaretPosition(readingTextManager.getCurrentCaret());
			textArea.requestFocus();
		} else {
			infoLabel.setText("Start reading first!");
			infoLabel.setForeground(Color.RED);
		}
		repaint();
	}
}

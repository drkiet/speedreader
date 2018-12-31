package com.drkiettran.tools.speedreader;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drkiettran.tika.text.ReadingTextManager;
import com.drkiettran.tika.text.SearchResult;
import com.drkiettran.tika.text.TextApp;
import com.drkiettran.tika.text.Word;
import com.drkiettran.tools.speedreader.ReaderListener.Command;

public class TextPanel extends JPanel {
	private static final String LINE_INFO = " *** line: ";

	private static final Logger LOGGER = LoggerFactory.getLogger(TextPanel.class);

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
	private ReadingTextManager readingTextManager;

	private String displayingFontName = "Candara";
	private String infoFontName = "Candara";
	private int displayingWordFontSize = DEFAULT_DISPLAYING_FONT_SIZE;
	private int infoFontSize = 12;
	private String textAreaFontName = "Candara";
	private int textAreaFontSize = DEFAULT_TEXT_AREA_FONT_SIZE;
	private int defaultBlinkRate = 0;
	private boolean doneReading = false;

	private Object highlightedWord = null;

	private SearchResult searchResult = null;

	private List<Object> highlightedWords = new ArrayList<Object>();
	private Object highlightSelectedWord = null;
	private Word selectedWord;

	private String searchText;

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
		displayingWordLabel = new JLabel();
		displayingWordLabel.setFont(new Font(displayingFontName, Font.PLAIN, displayingWordFontSize));
		displayingWordLabel.setHorizontalAlignment(JLabel.CENTER);
		infoLabel = new JLabel("");
		infoLabel.setFont(new Font(infoFontName, Font.PLAIN, infoFontSize));
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
		textArea.setCaretColor(Color.white);
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

		textArea.addMouseMotionListener(getMouseMotionListener());
		textArea.addMouseListener(getMouseListner());

	}

	private MouseListener getMouseListner() {

		return new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (readingTextManager != null) {
					selectedWord = readingTextManager.getWordAt(textArea.getCaretPosition());
					try {
						highlightSelectedWord = highlight(selectedWord.getTransformedWord(),
								selectedWord.getIndexOfText(), Color.GRAY, highlightSelectedWord);
					} catch (BadLocationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		};
	}

	private MouseMotionListener getMouseMotionListener() {
		return new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e) {

				int viewToModel = textArea.viewToModel(e.getPoint());
				if (viewToModel != -1) {
					try {
						String labelText = infoLabel.getText();
						int idx = labelText.indexOf(LINE_INFO);
						if (idx >= 0) {
							labelText = labelText.substring(0, idx);
						}
						infoLabel.setText(labelText + LINE_INFO + (1 + textArea.getLineOfOffset(viewToModel)));
						repaint();
					} catch (BadLocationException e1) {
						e1.printStackTrace();
					}
				}
			}

			@Override
			public void mouseDragged(MouseEvent e) {
			}
		};

	}

	private void displayHelpText() {
		textArea = new JTextArea(helpText);
	}

	public void setHelpText(String helpText) {
		this.helpText = helpText;
	}

	private void restart() {
		readingTextManager = new ReadingTextManager(helpText);
	}

	public void resetReading() {
		LOGGER.info("Reset reading ...");
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

	public void next() throws BadLocationException {

		if (readingText == null) {
			doneReading = false;
			readingText = textArea.getText();
			readingTextManager = new ReadingTextManager(readingText);
			textArea.setText(readingTextManager.getReadingText());
		}

		String wordToRead = getNextWord();

		if (wordToRead != null) {
			if (wordToRead.isEmpty()) {
				return;
			}

			highlightedWord = highlight(wordToRead, readingTextManager.getCurrentCaret(), Color.PINK, highlightedWord);
			textArea.requestFocus();
			displayingWordLabel.setText(wordToRead);
			displayReadingInformation();
		} else {
			displayReadingInformation();
			doneReading = true;
		}
		repaint();
	}

	private Object highlight(String wordToRead, int caret, Color color, Object highlightedWord)
			throws BadLocationException {
		textArea.setCaretPosition(caret);
		Highlighter hl = textArea.getHighlighter();
		HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(color);
		int p0 = textArea.getCaretPosition();
		int p1 = p0 + wordToRead.length();
		if (highlightedWord != null) {
			hl.removeHighlight(highlightedWord);
		}
		return hl.addHighlight(p0, p1, painter);
	}

	private void unHighlight(Object highlightedWord) {
		textArea.getHighlighter().removeHighlight(highlightedWord);
	}

	private void displayReadingInformation() {
		int wordsFromBeginning = readingTextManager.getWordsFromBeginning();
		int totalWords = readingTextManager.getTotalWords();
		int readingPercentage = (100 * wordsFromBeginning) / totalWords;

		infoLabel.setText(String.format("%d of %d words (%d%%) - line %d of %d", wordsFromBeginning, totalWords,
				readingPercentage, getCurrentLineNumber(), getLineCount()));
		infoLabel.setForeground(Color.BLUE);
	}

	private void displaySearchResult() {

		infoLabel.setText(String.format("found %d '%s's", searchResult.getNumberMatchedWords(), searchText));
		infoLabel.setForeground(Color.BLUE);
	}

	private int getLineCount() {
		return textArea.getLineCount();
	}

	private int getCurrentLineNumber() {
		try {
			return textArea.getLineOfOffset(textArea.getCaretPosition()) + 1;
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
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

	public void search(String searchText) {
		if (readingTextManager == null) {
			return;
		}

		for (Object highlightedWord : highlightedWords) {
			unHighlight(highlightedWord);
		}

		this.searchText = searchText;
		this.searchResult = readingTextManager.search(searchText);
		Hashtable<Integer, String> matchedWords = searchResult.getMatchedWords();
		highlightedWords = new ArrayList<Object>();

		for (Integer idx : matchedWords.keySet()) {
			try {
				LOGGER.info("highlighted at /{}/", matchedWords.get(idx));
				highlightedWords.add(highlight(matchedWords.get(idx), idx, Color.GREEN, null));
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		displaySearchResult();
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

	public void startReadingAt() {
		if (readingTextManager != null) {
			readingTextManager.setCurrentCaret(selectedWord.getIndexOfText());
			startReading();
		}
	}
}

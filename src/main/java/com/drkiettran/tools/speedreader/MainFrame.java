package com.drkiettran.tools.speedreader;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import java.util.Timer;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import com.drkiettran.text.model.Document;
import com.drkiettran.text.model.SearchResult;
import com.drkiettran.tools.speedreader.ReaderListener.Command;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = -5184507871687024902L;
	private TextPanel textPanel;
	private Toolbar toolbar;
	private FormPanel formPanel;
	private HelpPictureDialog helpPictureDialog;
	private JFileChooser fileChooser;
	private TextTimerTask textTimerTask = null;
	private Timer timer = null;
	private InfoPanel infoPanel;
	private String fileName;
	private Document document;
	private List<SearchResult> searchResults = null;
	private String searchText;

	public MainFrame() throws IOException {
		super("Simple Speed Reader Program");

		setLayout(new BorderLayout());
		toolbar = new Toolbar();
		textPanel = new TextPanel();
		infoPanel = new InfoPanel();
		formPanel = new FormPanel();
		infoPanel.setFrame(this);
		fileChooser = new JFileChooser();

		helpPictureDialog = new HelpPictureDialog(this);
		textPanel.setInfoPanel(infoPanel);

		formPanel.setReaderListener((Command cmd) -> {
			switch (cmd) {
			case LOAD:
				loadTextFromFile();
				break;
			case BROWSE:
				browseDirectoryForFile();
				break;
			case SEARCH:
				searchText = formPanel.getSearchText();
				searchText(searchText);
				searchTextInDocument(searchText);
				break;
			case NEXTFIND:
				nextFind();
				break;
			case GOTO:
				goToPage(formPanel.getGotoPageNo());
			default:
				break;
			}
		});

		textPanel.setReaderListener((Command cmd) -> {
			switch (cmd) {
			case RESET:
				resetReading();
				break;
			default:
				break;
			}
		});

		textPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (isReading()) {
					pauseReading();
				} else {
					startReading();
				}
			}
		});

		toolbar.setReaderListener((Command cmd) -> {
			switch (cmd) {
			case START_AT:
				startReadingAtCaret();
				// let it fall through ...
			case START:
				startReading();
				formPanel.enableSearch();
				break;

			case RESET:
				resetReading();
				// let it fall ...
			case STOP:
				pauseReading();
				break;

			case LARGER_TEXT_FONT:
				makeLargerFont();
				break;

			case SMALLER_TEXT_FONT:
				makeSmallerFont();
				break;

			case PREVIOUS_PAGE:
				previousPage();
				break;

			case NEXT_PAGE:
				nextPage();
				break;

			case LARGER_WORD_FONT:
				textPanel.setLargerWordFont();
				break;

			case SMALLER_WORD_FONT:
				textPanel.setSmallerWordFont();
				break;

			case HELP_PICTURE:
				helpPictureDialog.setVisible(!helpPictureDialog.isVisible());
				break;

			default:
				break;
			}
		});

		add(formPanel, BorderLayout.WEST);
		add(toolbar, BorderLayout.NORTH);
		add(textPanel, BorderLayout.CENTER);
		add(infoPanel, BorderLayout.SOUTH);

		setSize(800, 700);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	private void nextFind() {
		if (searchResults == null || searchResults.size() == 0) {
			return;
		}

		for (int idx = document.getCurrentPageNumber(); idx < document.getPageCount(); idx++) {
			if (searchResults.get(idx).getNumberMatchedWords() > 0) {
				textPanel.goTo(idx);
				textPanel.search(searchText);
			}
		}
	}

	public void goToPage(int gotoPageNo) {
		if (gotoPageNo > 0) {
			textPanel.goTo(gotoPageNo);
		}
	}

	private void searchTextInDocument(String searchText) {
		if (document == null) {
			return;
		}

		searchResults = document.search(searchText);
		StringBuilder sb = new StringBuilder("\nSearch results: <br>\n");
		for (int idx = 0; idx < searchResults.size(); idx++) {
			SearchResult sr = searchResults.get(idx);
			if (sr.getNumberMatchedWords() > 0) {
				sb.append("page ").append(idx + 1);
				sb.append(" has ").append(sr.getNumberMatchedWords()).append(" ").append(searchText).append("<br>\n");
			}
		}

		infoPanel.addText(sb.toString());
	}

	private void previousPage() {
		textPanel.previousPage();
		pauseReading();
	}

	private void nextPage() {
		textPanel.nextPage();
		pauseReading();
	}

	public void browseDirectoryForFile() {
		if (fileChooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
			formPanel.setFileName(fileChooser.getSelectedFile().getAbsolutePath());
		}
	}

	public void loadTextFromFile() {
		document = formPanel.getDocument();
		fileName = formPanel.getFileName();
		infoPanel.setFileName(fileName);
		StringBuilder sb = new StringBuilder();

		if (document != null) {
			textPanel.loadTextFromFile(document);
			formPanel.enableSearch();
			formPanel.enableGoTo();

			sb.append('\n').append(fileName).append(" is loaded successfully!\n");
			sb.append("The document has ").append(document.getPageCount()).append(" pages.\n");
			infoPanel.addText(sb.toString());

		} else {
			textPanel.resetReading();
			sb.append('\n').append(fileName).append(" fails to load!\n");
			infoPanel.addText(sb.toString());
		}
	}

	public void startReadingAtCaret() {
		textPanel.startReadingAt();
	}

	public void makeSmallerFont() {
		textPanel.setSmallerTextFont();
	}

	public void makeLargerFont() {
		textPanel.setLargerTextFont();
	}

	public void resetReading() {
		textPanel.resetReading();
	}

	public void pauseReading() {
		if (isReading()) {
			timer.cancel();
			textTimerTask = null;
			timer = null;
			textPanel.pauseReading();
		}
	}

	public void startReading() {
		if (!isReading()) {
			textTimerTask = new TextTimerTask();
			textTimerTask.register(textPanel);
			timer = new Timer();
			int speedWpm = formPanel.getSpeedWpm();
			timer.schedule(textTimerTask, 0, (60 * 1000) / speedWpm);
			textPanel.startReading();
		}
	}

	private boolean isReading() {
		return textTimerTask != null;
	}

	private void searchText(String searchText) {
		textPanel.search(searchText);
	}

}

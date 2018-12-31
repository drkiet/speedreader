package com.drkiettran.tools.speedreader;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.Timer;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.text.BadLocationException;

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

	public MainFrame() throws IOException {
		super("Simple Speed Reader Program");

		setLayout(new BorderLayout());
		toolbar = new Toolbar();
		textPanel = new TextPanel();
		formPanel = new FormPanel();
		fileChooser = new JFileChooser();

		helpPictureDialog = new HelpPictureDialog(this);

		formPanel.setReaderListener((Command cmd) -> {
			switch (cmd) {
			case LOAD:
				loadTextFromFile();
				formPanel.disableSearch();
				break;
			case BROWSE:
				browseDirectoryForFile();
				break;
			case SEARCH:
				searchText(formPanel.getSearchText());
				break;
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
					stopReading();
				} else {
					startReading();
				}
			}

			public void mouseEntered(MouseEvent e) {
				System.out.println("mouse enters ...");
			}

			public void mouseExited(MouseEvent e) {
				System.out.println("mouse exits ...");
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
				stopReading();
				break;

			case LARGER_TEXT_FONT:
				makeLargerFont();
				break;

			case SMALLER_TEXT_FONT:
				makeSmallerFont();
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

		setSize(800, 700);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public void browseDirectoryForFile() {
		if (fileChooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
			formPanel.setFileName(fileChooser.getSelectedFile().getAbsolutePath());
		}
	}

	public void loadTextFromFile() {
		textPanel.loadTextFromFile(formPanel.getText());
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

	public void stopReading() {
		if (isReading()) {
			timer.cancel();
			textTimerTask = null;
			timer = null;
			textPanel.stopReading();
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

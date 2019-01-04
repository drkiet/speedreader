package com.drkiettran.tools.speedreader;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drkiettran.text.TextApp;
import com.drkiettran.text.model.Document;
import com.drkiettran.tools.speedreader.ReaderListener.Command;

/**
 * 
 * @author ktran
 *
 */
public class FormPanel extends JPanel {
	private static final Logger LOGGER = LoggerFactory.getLogger(FormPanel.class);

	private static final long serialVersionUID = 3506596135223108382L;
	private JLabel fileNameLabel;
	private JTextField fileNameField;
	private JButton loadButton;
	private JButton browserButton;
	private JLabel speedLabel;
	private JTextField speedField;
	private JLabel searchTextLabel;
	private JTextField searchTextField;
	private JButton searchButton;
	private JButton setButton;
	private Integer speedWpm = 200;
	private String fileName;
	private String text;
	private ReaderListener readerListener;

	private Document document = null;

	private String loadingError = "";

	private JButton goToPageNoButton;

	private JTextField pageNoTextField;

	private JLabel pageNoTextLabel;

	private JButton nextFindButton;

	public String getLoadingError() {
		return loadingError;
	}

	public Document getDocument() {
		return document;
	}

	public String getText() {
		return text;
	}

	public Integer getSpeedWpm() {
		return speedWpm;
	}

	public FormPanel() {
		Dimension dim = getPreferredSize();
		dim.width = 250;
		setPreferredSize(dim);

		fileNameLabel = new JLabel("File Name: ");
		fileNameField = new JTextField(10);
		speedLabel = new JLabel("Speed (wpm): ");
		speedField = new JTextField(10);
		searchTextLabel = new JLabel("Text");
		searchTextField = new JTextField(10);
		pageNoTextLabel = new JLabel("Page No.");
		pageNoTextField = new JTextField(10);
		speedField.setText("" + speedWpm);

		setButton = new JButton("Set");
		loadButton = new JButton("Load");
		browserButton = new JButton("Browse");
		searchButton = new JButton("Search");
		nextFindButton = new JButton("Next");
		goToPageNoButton = new JButton("Go to");

		setButton.addActionListener((ActionEvent actionEvent) -> {
			speedWpm = Integer.valueOf(speedField.getText());
		});

		loadButton.addActionListener((ActionEvent actionEvent) -> {
			fileName = fileNameField.getText();
			TextApp textApp = new TextApp();

			document = textApp.getPages(fileName);
			if (document == null) {
				loadingError = "Unable to load " + fileName;
			} else {
				LOGGER.info("{} has {} pages", fileName, document.getPageCount());
				loadingError = "";
			}
			readerListener.invoke(Command.LOAD);
		});

		browserButton.addActionListener((ActionEvent actionEvent) -> {
			readerListener.invoke(Command.BROWSE);
		});

		searchButton.addActionListener((ActionEvent actionEvent) -> {
			readerListener.invoke(Command.SEARCH);
		});

		nextFindButton.addActionListener((ActionEvent actionEvent) -> {
			readerListener.invoke(Command.NEXTFIND);
		});

		goToPageNoButton.addActionListener((ActionEvent actionEvent) -> {
			readerListener.invoke(Command.GOTO);
		});

		Border innerBorder = BorderFactory.createTitledBorder("Configuration");
		Border outterBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		setBorder(BorderFactory.createCompoundBorder(outterBorder, innerBorder));

		layoutComponents();
	}

	public String getFileName() {
		return fileName;
	}

	private void layoutComponents() {
		setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();

		//// FIRST ROW /////////////
		gc.gridy = 0;

		// Always do the following to avoid future confusion :)
		// Speed
		gc.weightx = 1;
		gc.weighty = 0.1;

		gc.gridx = 0;
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.LINE_END;
		gc.insets = new Insets(0, 0, 0, 5);
		add(speedLabel, gc);

		gc.gridx = 1;
		gc.insets = new Insets(0, 0, 0, 0);
		gc.anchor = GridBagConstraints.LINE_START;
		add(speedField, gc);

		//// next row /////////////
		gc.gridy++;
		gc.weightx = 1;
		gc.weighty = .2;

		gc.gridx = 1;
		gc.insets = new Insets(0, 0, 0, 0);
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		add(setButton, gc);

		// Always do the following to avoid future confusion :)
		// File Name
		gc.gridy++;
		gc.weightx = 1;
		gc.weighty = 0.1;

		gc.gridx = 0;
		gc.anchor = GridBagConstraints.LINE_END;
		gc.insets = new Insets(0, 0, 0, 5);
		add(fileNameLabel, gc);

		gc.gridx = 1;
		gc.insets = new Insets(0, 0, 0, 0);
		gc.anchor = GridBagConstraints.LINE_START;
		add(fileNameField, gc);

		//// next row /////////////
		gc.gridy++;
		gc.weightx = 1;
		gc.weighty = .2;

		gc.gridx = 1;
		gc.insets = new Insets(0, 0, 0, 0);
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		add(loadButton, gc);

		//// next row /////////////
		gc.gridy++;
		gc.weightx = 1;
		gc.weighty = .2;

		gc.gridx = 1;
		gc.insets = new Insets(0, 0, 0, 0);
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		add(browserButton, gc);

		// Always do the following to avoid future confusion :)
		gc.gridy++;
		gc.weightx = 1;
		gc.weighty = 0.1;

		gc.gridx = 0;
		gc.anchor = GridBagConstraints.LINE_END;
		gc.insets = new Insets(0, 0, 0, 5);
		add(searchTextLabel, gc);

		gc.gridx = 1;
		gc.insets = new Insets(0, 0, 0, 0);
		gc.anchor = GridBagConstraints.LINE_START;
		add(searchTextField, gc);

		//// next row /////////////
		gc.gridy++;
		gc.weightx = 1;
		gc.weighty = .2; // 5;

		gc.gridx = 1;
		gc.insets = new Insets(0, 0, 0, 0);
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		add(searchButton, gc);

		//// next row /////////////
		gc.gridy++;
		gc.weightx = 1;
		gc.weighty = .2;

		gc.gridx = 1;
		gc.insets = new Insets(0, 0, 0, 0);
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		add(nextFindButton, gc);

		// Always do the following to avoid future confusion :)
		gc.gridy++;
		gc.weightx = 1;
		gc.weighty = 0.1;

		gc.gridx = 0;
		gc.anchor = GridBagConstraints.LINE_END;
		gc.insets = new Insets(0, 0, 0, 5);
		add(pageNoTextLabel, gc);

		gc.gridx = 1;
		gc.insets = new Insets(0, 0, 0, 0);
		gc.anchor = GridBagConstraints.LINE_START;
		add(pageNoTextField, gc);

		//// next row /////////////
		gc.gridy++;
		gc.weightx = 1;
		gc.weighty = 5;

		gc.gridx = 1;
		gc.insets = new Insets(0, 0, 0, 0);
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		add(goToPageNoButton, gc);

		disableSearch();
		disableGoto();
	}

	public void disableGoto() {
		pageNoTextField.setEnabled(false);
		goToPageNoButton.setEnabled(false);
	}

	public void enableGoTo() {
		pageNoTextField.setEnabled(true);
		goToPageNoButton.setEnabled(true);
	}

	public void setReaderListener(ReaderListener readerListener) {
		this.readerListener = readerListener;
	}

	public void setFileName(String selectedFile) {
		fileNameField.setText(selectedFile);
	}

	public String getSearchText() {
		return searchTextField.getText();
	}

	public void enableSearch() {
		searchButton.setEnabled(true);
		nextFindButton.setEnabled(true);
		searchTextField.setEnabled(true);
	}

	public void disableSearch() {
		searchButton.setEnabled(false);
		nextFindButton.setEnabled(false);
		searchTextField.setEnabled(false);
	}

	public int getGotoPageNo() {
		if (!pageNoTextField.getText().trim().isEmpty()) {
			return Integer.valueOf(pageNoTextField.getText());
		}
		return -1;
	}
}

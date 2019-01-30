package com.drkiettran.tools.speedreader;

import java.util.TimerTask;

import javax.swing.text.BadLocationException;

public class TextTimerTask extends TimerTask {
	private TextPanel textPanel;

	public void register(TextPanel textPanel) {
		this.textPanel = textPanel;
	}

	@Override
	public void run() {
		try {
			textPanel.nextWord();
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (textPanel.isDoneReading()) {
				cancel();
			}
		}
	}

}

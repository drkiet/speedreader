package com.drkiettran.tools.speedreader;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextTimerTask extends TimerTask {
	private static final Logger logger = LoggerFactory.getLogger(TextTimerTask.class);

	private TextPanel textPanel;
	private long prevTime = System.currentTimeMillis();

	public void register(TextPanel textPanel) {
		this.textPanel = textPanel;
	}

	@Override
	public void run() {
		long enterTime = System.currentTimeMillis();
		long delayTime = enterTime - prevTime;

		logger.info("delayTime: {}", delayTime);
		textPanel.next();
		if (textPanel.isDoneReading()) {
			cancel();
		}
		delayTime = System.currentTimeMillis() - enterTime;
		logger.info("processingTime: {}", delayTime);
		prevTime = System.currentTimeMillis();
	}

}

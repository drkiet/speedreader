package com.drkiettran.tools.speedreader;

import java.io.IOException;

import javax.swing.SwingUtilities;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame mainFrame = new MainFrame();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
}

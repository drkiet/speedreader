package com.drkiettran.tools.speedreader;

import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class HelpPictureDialog extends JDialog {
	private static final long serialVersionUID = -923602908685872490L;

	public HelpPictureDialog(JFrame parent) {
		super(parent, "Navigation Help for Simple Speed Reader", false);
		ImageIcon helpPicture = createIcon("/images/HelpPicture.png");
		getContentPane().add(new JLabel(helpPicture));
		pack();
	}

	private ImageIcon createIcon(String path) {
		URL url = getClass().getResource(path);

		if (url == null) {
			System.err.println("Unable to load image: " + path);
		}
		return new ImageIcon(url);
	}

}

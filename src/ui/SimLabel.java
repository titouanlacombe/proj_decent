package ui;

import javax.swing.SwingConstants;
import java.awt.Color;

public class SimLabel extends javax.swing.JLabel {

	public SimLabel(String text) {
		super(text);

		this.setHorizontalAlignment(SwingConstants.CENTER);
		this.setVerticalAlignment(SwingConstants.CENTER);
		this.setForeground(Color.WHITE);
	}
}

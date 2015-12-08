package app;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class ProgressBar extends JPanel {
	private JLabel lblName;
	private JProgressBar progressBar;

	/**
	 * Create the panel.
	 */
	public ProgressBar(String label) {
		initGUI();
		progressBar.setValue(0);
		lblName.setText(label);
	}
	
	private void initGUI() {
		setBorder(null);
		setSize(270, 50);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		lblName = new JLabel("Client:C1 is uploading...");
		lblName.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblName.setPreferredSize(new Dimension(270, 20));
		lblName.setMaximumSize(lblName.getPreferredSize());
		add(lblName);
		
		progressBar = new JProgressBar();
		progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
		progressBar.setPreferredSize(new Dimension(550, 20));
		progressBar.setMaximumSize(progressBar.getPreferredSize());
		progressBar.setForeground(new Color(51, 153, 255));
		add(progressBar);
	}
	
	public void updateProgressBar(int val) {
		progressBar.setValue(val);
	}

}

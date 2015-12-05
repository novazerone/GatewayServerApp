package gatewayServer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.LineBorder;

public class ProgressBar extends JPanel {
	private JLabel lblName;
	private JProgressBar progressBar;

	/**
	 * Create the panel.
	 */
	public ProgressBar() {
		initGUI();
	}
	
	//TODO: DELETE. FOR TESTING PURPOSES.
	public ProgressBar(int val) {
		initGUI();
		progressBar.setValue(val);
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
		progressBar.setPreferredSize(new Dimension(450, 30));
		progressBar.setMaximumSize(progressBar.getPreferredSize());
		progressBar.setForeground(Color.GREEN);
		add(progressBar);
	}

}

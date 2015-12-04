package gatewayServer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.BevelBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

public class GatewayUI {

	// GUI
	private JFrame frame;

	private JPanel pnlMain;
	private JTextPane messagePane;

	private JPanel pnlStatus;
	private JLabel lblStatus;	
	private JProgressBar progressBar;

	public GatewayUI(String windowName){
		initializeGUI(windowName);
	}

	/**
	 * Initialize GUI.
	 */
	private void initializeGUI(String windowName){
		frame = new JFrame(windowName);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setSize(410, 400);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));

		pnlMain = new JPanel();
		pnlMain.setLayout(null);
		frame.getContentPane().add(pnlMain, BorderLayout.CENTER);

		messagePane = new JTextPane();
		messagePane.setEditable(false);

		JScrollPane scrollMsg = new JScrollPane();
		scrollMsg.setBounds(10, 10, 385, 335);
		scrollMsg.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollMsg.setViewportView(messagePane);
		pnlMain.add(scrollMsg);

		pnlStatus = new JPanel();
		pnlStatus.setPreferredSize(new Dimension(10, 20));
		pnlStatus.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		frame.getContentPane().add(pnlStatus, BorderLayout.SOUTH);
		pnlStatus.setLayout(new BoxLayout(pnlStatus, BoxLayout.X_AXIS));

		lblStatus = new JLabel("Status");
		pnlStatus.add(lblStatus);

		progressBar = new JProgressBar();
		progressBar.setValue(50);
		pnlStatus.add(progressBar);

		frame.setVisible(true);
	}

	/**
	 * Wrapper method for appending text to the pane.
	 */
	public void log(String msg, Color c){
		appendToPane(messagePane, msg, c);
	}

	/**
	 * Append to the pane, with custom color.
	 */
	private void appendToPane(JTextPane tp, String msg, Color c) {
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

		aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
		aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

		int oldLen = tp.getDocument().getLength();
		try{
			tp.getStyledDocument().insertString(oldLen, msg, aset);
		} catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
}

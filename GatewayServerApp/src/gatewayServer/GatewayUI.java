package gatewayServer;

import java.awt.Color;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

public class GatewayUI {

	// GUI
	private JFrame frame;
	
	/**
	 * This is the container for all components on the LEFT side of the separator.
	 */
	private JPanel pnlLeft;
	private JTextPane messagePane;
	
	/**
	 * This is the container for all components on the RIGHT side of the separator.
	 */
	private JPanel pnlRight;
	/**
	 * This is the panel where you dynamically add progress bars.
	 */
	private JPanel pnlProgressStack;
	private JScrollPane scrollProgress;

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
		frame.setSize(700, 400);
		frame.getContentPane().setLayout(null);

		/* LEFT SIDE */
		pnlLeft = new JPanel();
		pnlLeft.setLocation(0, 0);
		pnlLeft.setSize(405, 371);
		pnlLeft.setLayout(null);
		frame.getContentPane().add(pnlLeft);

		messagePane = new JTextPane();
		messagePane.setEditable(false);

		JScrollPane scrollMsg = new JScrollPane();
		scrollMsg.setBounds(10, 10, 385, 350);
		scrollMsg.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollMsg.setViewportView(messagePane);
		pnlLeft.add(scrollMsg);
		
		/* MIDDLE */
		JSeparator separator = new JSeparator();
		separator.setBounds(405, 10, 1, 350);
		separator.setOrientation(SwingConstants.VERTICAL);
		frame.getContentPane().add(separator);
		
		/* RIGHT SIDE */
		pnlRight = new JPanel();
		pnlRight.setBounds(405, 0, 289, 371);
		pnlRight.setLayout(null);
		frame.getContentPane().add(pnlRight);
		
		pnlProgressStack = new JPanel();
		pnlProgressStack.setBorder(new LineBorder(Color.GRAY));
		pnlProgressStack.setLayout(new BoxLayout(pnlProgressStack, BoxLayout.Y_AXIS));
		
		scrollProgress = new JScrollPane(pnlProgressStack, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollProgress.setBounds(10, 10, 270, 350);
		pnlRight.add(scrollProgress);
		
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
	
	
	public void validatePanelUpdate(){
		pnlProgressStack.validate();
		scrollProgress.validate();
	}
	
	public JPanel getPnlProgressStack() {
		return pnlProgressStack;
	}
}

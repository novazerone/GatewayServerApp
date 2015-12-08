package server;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
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

import app.ProgressBar;
import database.FileJDBCTemplate;
import database.ServerJDBCTemplate;

public class ServerUI {

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
	private JList<String> listFile;
	private JButton btnRefresh;

	private String serverName;

	public ServerUI(String _windowName){
		initializeGUI(_windowName);
	}

	/**
	 * Initialize GUI.
	 */
	private void initializeGUI(String _windowName){
		frame = new JFrame(_windowName);
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
		
		scrollProgress = new JScrollPane(pnlProgressStack, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollProgress.setBounds(10, 10, 270, 155);
		pnlRight.add(scrollProgress);

		listFile = new JList<String>();
		listFile.setBounds(405, 11, 275, 284);
		listFile.setEnabled(false);

		JScrollPane scrollList = new JScrollPane();
		scrollList.setBounds(10, 170, 270, 160);
		scrollList.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollList.setViewportView(listFile);
		pnlRight.add(scrollList);

		btnRefresh = new JButton("Refresh");
		btnRefresh.setBounds(10, 335, 270, 25);
		btnRefresh.addActionListener(new BtnRefreshActionListener());
		pnlRight.add(btnRefresh);

		frame.setVisible(true);
	}

	/**
	 * Wrapper method for appending text to the pane.
	 */
	public void log(String _msg, Color _c) {
		appendToPane(messagePane, _msg, _c);
	}

	/**
	 * Append to the pane, with custom color.
	 */
	private void appendToPane(JTextPane _tp, String _msg, Color _c) {
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, _c);

		aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
		aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

		int oldLen = _tp.getDocument().getLength();
		try{
			_tp.getStyledDocument().insertString(oldLen, _msg, aset);
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
	
	public void addGap() {
		pnlProgressStack.add(Box.createRigidArea(new Dimension(0,5)));
	}
	
	/**
	 * @return the frame.
	 */
	public JFrame getFrame() {
		return frame;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
		refreshFileList();
	}

	/**
	 * Refreshes the list of files the server contains.
	 */
	private void refreshFileList() {
		FileJDBCTemplate dbFile= new FileJDBCTemplate();
		ServerJDBCTemplate dbServer = new ServerJDBCTemplate();

		List<database.models.File> fileList = dbFile.listServerFiles(dbServer.getServer(serverName).getId());
		DefaultListModel<String> model = new DefaultListModel<String>();
		System.out.println("Content:");
		for(database.models.File f : fileList) {
			System.out.println(f.getFile_name());
			model.addElement(f.getFile_name());
		}
		listFile.setModel(model);
	}

	/**
	 * Event handling for when btnRefresh is clicked.
	 */
	private class BtnRefreshActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			refreshFileList();
		}
	}
	
	public class ProgressBarAnimation implements Runnable {
		private int percent;
		private ProgressBar pb;
		
		public ProgressBarAnimation(ProgressBar pb, int percent) {
			this.pb = pb;
			this.percent = percent;
			run();
		}
		
		@Override
		public void run() {
			pb.updateProgressBar(percent);
		}
	}
}
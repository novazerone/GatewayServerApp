package server;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import database.FileJDBCTemplate;
import database.ServerJDBCTemplate;
import javafx.beans.binding.When;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.border.BevelBorder;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

public class ServerUI {

	// GUI
	private JFrame frame;

	private JPanel pnlMain;
	private JTextPane messagePane;
	private JList<String> listFile;
	private JButton btnRefresh;

	private JPanel pnlStatus;
	private JLabel lblStatus;	
	private JProgressBar progressBar;
	
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

		JSeparator separator = new JSeparator();
		separator.setBounds(405, 10, 1, 335);
		separator.setOrientation(SwingConstants.VERTICAL);
		pnlMain.add(separator);

		listFile = new JList<String>();
		listFile.setBounds(405, 11, 275, 284);
		listFile.setEnabled(false);

		JScrollPane scrollList = new JScrollPane();
		scrollList.setBounds(415, 10, 270, 305);
		scrollList.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollList.setViewportView(listFile);
		pnlMain.add(scrollList);

		btnRefresh = new JButton("Refresh");
		btnRefresh.setBounds(415, 320, 270, 25);
		btnRefresh.addActionListener(new BtnRefreshActionListener());
		pnlMain.add(btnRefresh);

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
		for(database.models.File f : fileList) {
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
}
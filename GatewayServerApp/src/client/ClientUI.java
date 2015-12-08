package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import database.FileJDBCTemplate;

public class ClientUI {

	// GUI
	private JFrame frame;

	private JPanel pnlMain;
	private JTextPane messagePane;
	private JButton btnBrowse;
	private JButton btnUpload;
	private JList<String> listFile;
	private JButton btnRefresh;
	private JButton btnDownload;

	private JPanel pnlStatus;
	private JLabel lblStatus;	
	private JProgressBar progressBar;

	private JFileChooser fileChooser;
	private File selectedFile;

	private Client client;
	private JPanel pnlLabel;
	private JPanel pnlProgress;

	public ClientUI(Client _client, String _windowName){
		client = _client;
		initializeGUI(_windowName);
		fileChooser = new JFileChooser(".");
		refreshFileList();
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
		scrollMsg.setBounds(10, 10, 385, 305);
		scrollMsg.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollMsg.setViewportView(messagePane);
		pnlMain.add(scrollMsg);

		btnBrowse = new JButton("Browse File");
		btnBrowse.setBounds(10, 320, 100, 25);
		btnBrowse.setEnabled(false);
		btnBrowse.addActionListener(new BtnBrowseActionListener());
		pnlMain.add(btnBrowse);

		btnUpload = new JButton("Upload");
		btnUpload.setBounds(295, 320, 100, 25);
		btnUpload.setEnabled(false);
		btnUpload.addActionListener(new BtnUploadActionListener());
		pnlMain.add(btnUpload);

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
		btnRefresh.setBounds(415, 320, 100, 25);
		btnRefresh.setEnabled(false);
		btnRefresh.addActionListener(new BtnRefreshActionListener());
		pnlMain.add(btnRefresh);

		btnDownload = new JButton("Download");
		btnDownload.setBounds(585, 320, 100, 25);
		btnDownload.setEnabled(false);
		btnDownload.addActionListener(new BtnDownloadActionListener());
		pnlMain.add(btnDownload);

		pnlStatus = new JPanel();
		pnlStatus.setPreferredSize(new Dimension(10, 20));
		pnlStatus.setLayout(null);
		frame.getContentPane().add(pnlStatus, BorderLayout.SOUTH);


		pnlLabel = new JPanel();
		pnlLabel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		pnlLabel.setBounds(0, 0, 100, 20);
		pnlLabel.setLayout(null);
		pnlStatus.add(pnlLabel);

		lblStatus = new JLabel("Status");
		lblStatus.setBounds(10, 0, 100, 20);
		pnlLabel.add(lblStatus);

		pnlProgress = new JPanel();
		pnlProgress.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		pnlProgress.setBounds(100, 0, 594, 20);
		pnlStatus.add(pnlProgress);
		pnlProgress.setLayout(null);

		progressBar = new JProgressBar();
		progressBar.setBounds(5, 4, 585, 15);
		pnlProgress.add(progressBar);
		progressBar.setForeground(new Color(51, 153, 255));
		progressBar.setValue(0);

		frame.setVisible(true);
	}

	/**
	 * Wrapper method for appending text to the pane.
	 */
	public void log(String _msg, Color _c){
		appendToPane(messagePane, _msg, _c);
	}

	public void updateProgressBar(int val){
		progressBar.setValue(val);
		if (progressBar.getValue() == 0 || progressBar.getValue() == 100) {
			lblStatus.setText("Done");
			btnUpload.setEnabled(true);
		}
		else {
			lblStatus.setText("Processing");
			btnUpload.setEnabled(false);
		}
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
	 * Enable the buttons in this window.
	 */
	public void setEnable(boolean _v){
		btnBrowse.setEnabled(_v);
		btnDownload.setEnabled(_v);
		btnRefresh.setEnabled(_v);
		listFile.setEnabled(_v);

		if(selectedFile != null){
			btnUpload.setEnabled(_v);
		} else{
			btnUpload.setEnabled(!_v);
		}
	}

	/**
	 * Refreshes the list of files available for the client to download.
	 */
	private void refreshFileList() {
		FileJDBCTemplate db = new FileJDBCTemplate();
		List<database.models.File> fileList = db.listFiles();
		DefaultListModel<String> model = new DefaultListModel<String>();
		for(database.models.File f : fileList) {
			model.addElement(f.getFile_name());
		}
		listFile.setModel(model);
	}

	/** 
	 * Event handling for when btnBrowse is clicked.
	 */
	private class BtnBrowseActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			progressBar.setValue(0);

			int x = fileChooser.showOpenDialog(null);

			if (x == JFileChooser.APPROVE_OPTION) {
				selectedFile = fileChooser.getSelectedFile();
				log("SELECTED: " + selectedFile.getAbsolutePath() + " (size "+selectedFile.length()+")\n", Color.BLACK);
				btnUpload.setEnabled(true);
			} else {
				selectedFile = null;
				log("No file selected." + "\n", Color.RED);
				btnUpload.setEnabled(false);
			}
		}
	}

	/** 
	 * Event handling for when btnUpload is clicked.
	 */
	private class BtnUploadActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {			
			try {
				client.trySendFile(selectedFile); //TODO: Thread?
			} catch (IOException e1) {
				log("Cannot upload '"+selectedFile.getName()+"'.", Color.RED);
			}
		}
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

	/** 
	 * Event handling for when btnDownload is clicked.
	 */
	private class BtnDownloadActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				client.tryDownloadFile(listFile.getSelectedValue().toString());
			} catch (IOException e1) {
				log("Cannot download '"+listFile.getSelectedValue().toString()+"'.", Color.RED);
			}
		}
	}
}

package client;

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

public class ClientWindow implements ActionListener{

	// GUI
	private JFrame frame;
	private JTextPane messagePane;
	private JFileChooser fileChooser;
	private JButton browseFile;
	private JButton uploadFile;
	private JButton downloadFile;

	private File selectedFile;

	private Client client;
	private JList<String> list;
	private JButton refresh;

	public ClientWindow(Client _client, String _windowName){
		client = _client;
		initializeGUI(_windowName);
		fileChooser = new JFileChooser(".");
		refreshFileList();
	}

	/*
	 * Initialize GUI.
	 */
	private void initializeGUI(String _windowName){
		fileChooser = new JFileChooser(".");

		frame = new JFrame(_windowName);
		frame.setSize(706, 375);
		frame.getContentPane().setLayout(null);

		browseFile = new JButton("Browse File");
		browseFile.setBounds(10, 306, 100, 25);
		browseFile.setEnabled(false);
		browseFile.addActionListener(this);

		uploadFile = new JButton("Upload");
		uploadFile.setBounds(284, 306, 100, 25);
		uploadFile.setEnabled(false);
		uploadFile.addActionListener(this);
		
		refresh = new JButton("Refresh");;
		refresh.setEnabled(false);
		refresh.setBounds(404, 306, 100, 25);
		refresh.addActionListener(this);

		downloadFile = new JButton("Download");
		downloadFile.setBounds(580, 306, 100, 25);
		downloadFile.setEnabled(false);
		downloadFile.addActionListener(this);

		messagePane = new JTextPane();
		messagePane.setBounds(0, 0, 500, 300);
		messagePane.setEditable(false);
		messagePane.setPreferredSize(new Dimension(500, 300));
		
		JScrollPane scrollPane1 = new JScrollPane();
		scrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane1.setBounds(10, 11, 374, 291);
		scrollPane1.setViewportView(messagePane);
		
		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		separator.setBounds(394, 11, 2, 320);
		
		list = new JList<String>();
		list.setBounds(405, 11, 275, 284);
		
		JScrollPane scrollPane2 = new JScrollPane();
		scrollPane2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane2.setBounds(405, 11, 275, 291);
		scrollPane2.setViewportView(list);

		frame.getContentPane().add(browseFile);
		frame.getContentPane().add(uploadFile);
		frame.getContentPane().add(downloadFile);
		frame.getContentPane().add(refresh);
		frame.getContentPane().add(scrollPane1);
		frame.getContentPane().add(scrollPane2);
		frame.getContentPane().add(separator);		
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	/*
	 * Enable the buttons in this window.
	 */
	public void setEnable(boolean _v){
		browseFile.setEnabled(_v);
		downloadFile.setEnabled(_v);
		refresh.setEnabled(_v);
		list.setEnabled(_v);

		if(selectedFile != null){
			uploadFile.setEnabled(_v);
		} else{
			uploadFile.setEnabled(!_v);
		}
	}


	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == browseFile) {
				int x = fileChooser.showOpenDialog(null);

				if (x == JFileChooser.APPROVE_OPTION) {
					selectedFile = fileChooser.getSelectedFile();
					log("SELECTED: " + selectedFile.getAbsolutePath() + " (size "+selectedFile.length()+")\n", Color.BLACK);
					uploadFile.setEnabled(true);
				} else {
					selectedFile = null;
					log("No file selected." + "\n", Color.RED);
					uploadFile.setEnabled(false);
				}
			} else if (e.getSource() == refresh) {
				refreshFileList();
			}

			// Send to server/gateway.
			if (e.getSource() == uploadFile) {
				client.sendFile(selectedFile);
			} else if(e.getSource() == downloadFile){
				client.downloadFile(list.getSelectedValue().toString());
			}
		} catch (Exception ex) {

		}
	}

	/*
	 * Wrapper method for appending text to the pane.
	 */
	public void log(String _msg, Color _c){
		appendToPane(messagePane, _msg, _c);
	}

	/*
	 * Append to the pane, with custom color.
	 */
	private void appendToPane(JTextPane _tp, String _msg, Color _c)
	{
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
	
	private void refreshFileList() {
		FileJDBCTemplate db = new FileJDBCTemplate();
		List<database.models.File> fileList = db.listFiles();
		DefaultListModel<String> model = new DefaultListModel<String>();
		for(database.models.File f : fileList) {
			model.addElement(f.getFile_name());
		}
		list.setModel(model);
	}

	public JFrame getFrame(){
		return frame;
	}
}

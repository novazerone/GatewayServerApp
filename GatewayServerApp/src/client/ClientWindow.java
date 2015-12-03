package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

public class ClientWindow implements ActionListener{

    // GUI
    private JFrame frame;
    private JTextPane messagePane;
    private JFileChooser fileChooser;
    private JButton browseFile;
    private JButton uploadFile;
    private JButton downloadFile;
    private JTextField textField = new JTextField(40);
    
    private File selectedFile;
    
    private Client client;
    
    public ClientWindow(Client _client, String _windowName){
    	client = _client;
    	initializeGUI(_windowName);
    }
    
    /*
     * Initialize GUI.
     */
    private void initializeGUI(String _windowName){
    	frame = new JFrame(_windowName);
    	messagePane = new JTextPane();
    	fileChooser = new JFileChooser(".");

    	browseFile = new JButton("Browse File");
    	browseFile.setEnabled(false);
        browseFile.addActionListener(this);
        
        uploadFile = new JButton("Upload");
        uploadFile.setEnabled(false);
        uploadFile.addActionListener(this);
        
        downloadFile = new JButton("Download");
        downloadFile.setEnabled(false);
        downloadFile.addActionListener(this);

        textField.setEditable(false);
        
        messagePane.setEditable(false);
        messagePane.setPreferredSize(new Dimension(500, 300));

        frame.getContentPane().add(browseFile, "North");
        frame.getContentPane().add(uploadFile, "South");
        frame.getContentPane().add(downloadFile, "West");
        frame.getContentPane().add(textField, "East");
        frame.getContentPane().add(new JScrollPane(messagePane), "Center");
        frame.pack();
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
    /*
     * Enable the buttons in this window.
     */
    public void setEnable(boolean _v){
    	browseFile.setEnabled(_v);
    	downloadFile.setEnabled(_v);
        textField.setEditable(_v);
    	
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
            }
            
            // Send to server/gateway.
            if (e.getSource() == uploadFile) {
                client.sendFile(selectedFile);
            } else if(e.getSource() == downloadFile){
            	client.downloadFile(textField.getText());
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
    
    public JFrame getFrame(){
    	return frame;
    }
}

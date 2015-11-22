package app;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

public class LogWindow {

    // GUI
    private JFrame frame;
    private JTextPane messagePane;
    
    public LogWindow(String windowName){
    	initializeGUI(windowName);
    }
    
    /*
     * Wrapper method for appending text to the pane.
     */
    public void log(String msg, Color c){
    	appendToPane(messagePane, msg, c);
    }
    
    /*
     * Initialize GUI.
     */
    private void initializeGUI(String windowName){
    	frame = new JFrame(windowName);
    	messagePane = new JTextPane();
    	
        messagePane.setEditable(false);
        messagePane.setPreferredSize(new Dimension(500, 300));
        
        frame.getContentPane().add(new JScrollPane(messagePane), "Center");
        frame.pack();
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
    /*
     * Append to the pane, with custom color.
     */
    private void appendToPane(JTextPane tp, String msg, Color c)
    {
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
    
    public JFrame getFrame(){
    	return frame;
    }
}

package client;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

import app.Driver;
import gatewayServer.Gateway;

public class Client {

    public static void main(String[] args) throws Exception {
        Client client = new Client(UUID.randomUUID().toString());
        
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
            	client.close();
            }
        });
    }

    private String name;
    
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private ClientWindow window;
    
    public Client(String _name) {
    	name = _name;
    	window = new ClientWindow(this, "Client");
    	window.log("Client initialized." + "\n", Color.BLACK);
    	
        // Enable window operation.
        window.setEnable(true);
    }
    
    /*
     * Opens a connection to the server.
     */
    public boolean open(){
    	try{
    		window.log("Connecting to gateway at port " + Driver.getClientPort()  + "\n", Color.BLACK);
    	
    		// Make a connection.
			socket = new Socket(Driver.getHost(), Driver.getClientPort());
    	} catch (IOException e){
    		window.log("Failed to connect to server!" + "\n", Color.RED);
    		window.log(e.getMessage() + "\n", Color.RED);
    		window.log(e.getStackTrace() + "\n", Color.RED);
    	
    		return false;
    	}
    	
    	window.log("Successfully connected to gateway!" + "\n",  new Color(0 , 100, 0));

		// Initialize streams.
    	window.log("Initializing streams..." + "\n", Color.BLACK);
        try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	        out = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			window.log("Failed to initialize streams." + "\n", Color.RED);
			window.log(e.getMessage() + "\n", Color.RED);
			window.log(e.getStackTrace() + "\n", Color.RED);
    		
    		return false;
		}
        
        window.log("Successfully initialized streams!" + "\n",  new Color(0 , 100, 0));
        
        return true;
    }
    
    /*
     * Close the current connection.
     */
    public void close(){
    	if(socket != null){
			try {
				socket.close();
			} catch (IOException e) {
				window.log("Failed to close connection." + "\n", Color.RED);
				window.log(e.getMessage() + "\n", Color.RED);
				window.log(e.getStackTrace() + "\n", Color.RED);
	    		
	    		return;
			}
    	}
    }
    
    /* 
     * Server logic.
     */
    private void run(){
        // Process all messages from the gateway, according to the protocol.
    	try{
	        while (true) {
	            String line = in.readLine();

	            // No message from the server.
	            if(line == null){
	        		window.log("Lost connection to server." + "\n", Color.RED);
	        		close();
	            	break;
	            }
	
	            if (line.startsWith("IDENTIFY")) {			// The server is asking for identification.
	            	// Respond with the UUID.
	                out.println(name);	
	                
	            } else if(line.startsWith("CONNECTION_SUCCESS")){ 
	            	window.log("Successfully connected to gateway!" + "\n", Color.BLACK);
	            } else if (line.startsWith("MESSAGE")) {	// The server sent a message.
	            	window.log(line.substring(8) + "\n", Color.BLACK);
	            		
	            } else if (line.startsWith("ERROR")) {		// The server sent an error message.
	            	window.log(line.substring(6) + "\n", Color.RED);
	            } else if (line.startsWith("CLOSE")){
	            	window.log("Gateway is closing. Disconnecting..." + "\n", Color.RED);
	            	close();
	            }
	        }
    	} catch(Exception e){
    		window.log("Lost connection to server." + "\n", Color.RED);
    		close();
    	}
    }
    
    /*
     * Sends a file to the gateway.
     * 
     */
    public void sendFile(File _file) throws IOException{
    	if(_file == null)
    		return;
    	
    	// Connect to the server.
    	open();
    	
    	out.println("MODE:UPLOAD");
    	out.println("FILENAME:" + _file.getName());
    	
    	while(true){
    		String input = in.readLine();
    		
    		if(input == null){
    			window.log("Failed to upload " + _file.getName() + "\n", Color.RED);
    			return;
    		}
    		
    		if(input.equals("PROCEEDTOUPLOAD"))
    			break;
    	}
    	
    	// TODO:
    	// Test file upload interruption and handle it.
    	// Test large file uploads.
    	// Source: http://stackoverflow.com/questions/10819516/sending-big-file-using-fileinputstream-objectoutputstream
    	// Split bytes into chunks (check source)
    	/*FileInputStream fis = new FileInputStream(_file);
        byte[] fileByte = new byte[fis.available()];
        
        window.log("Bytes Read: " + fis.read(fileByte) + "\n", Color.BLACK);
        window.log("Uploading..." + "\n", Color.BLACK);
        
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(fileByte);
        fis.close();
        */
    	
    	out.flush();
        BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
        FileInputStream fis = new FileInputStream(_file);
        BufferedInputStream bis = new BufferedInputStream(fis);
        int n = -1;
        byte[] buffer = new byte[4096];
        while((n = bis.read(buffer)) > -1){
        	bos.write(buffer, 0, n);
        }
        bis.close();
        bos.close();
        
        window.log("Successfully uploaded file!" + "\n", Color.BLUE);
        
        close();
    }
}

package server;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import app.Driver;
import app.LogWindow;

import java.util.UUID;
public class Server {

    public static void main(String[] args) throws Exception {
        Server server = new Server(UUID.randomUUID().toString());
        server.open();
        
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
            	server.close();
            }
        });
        
        server.run();
    }

    private String name;
    
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private LogWindow window;
    
    public Server(String _name) {
    	name = _name;
    	window = new LogWindow("Server - " + name);
    	window.log("Server initialized." + "\n", Color.BLACK);
    	window.log("UUID: " + name + "\n", Color.BLACK);
    }
    
    /*
     * Opens a connection to the server.
     */
    public boolean open(){
    	try{
    		window.log("Connecting to gateway at port " + Driver.getServerPort()  + "\n", Color.BLACK);
    	
    		// Make a connection.
			socket = new Socket(Driver.getHost(), Driver.getServerPort());
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
	
	            if (line.startsWith("IDENTIFY")) {			// The gateway is asking for identification.
	            	// Respond with the password
	                out.println(name + "|" + Driver.getServerPassword());	
	                
	            } else if(line.startsWith("CONNECTION_SUCCESS")){ 
	            	window.log("Successfully connected to gateway!" + "\n", Color.BLACK);
	            } else if (line.startsWith("MESSAGE")) {	// The gateway sent a message.
	            	window.log(line.substring(8) + "\n", Color.BLACK);
	            		
	            } else if (line.startsWith("ERROR")) {		// The gateway sent an error message.
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
}

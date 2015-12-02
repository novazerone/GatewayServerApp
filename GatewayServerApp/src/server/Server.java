package server;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import app.ByteCache;
import app.Driver;
import app.LogWindow;
import gatewayServer.ClientListener;

import java.util.UUID;
public class Server {

	public static String ServerName = "3"; // CHANGE SERVER NAME HERE
	public static int ListenerPort = 8084; // TEST PURPOSES.
    public static void main(String[] args) throws Exception {
    	// CHANGE SERVER NAME HERE
        Server server = new Server(ServerName);
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

    public static LogWindow window;

    private boolean isDownloading = false;
	private String fileName = "";
	private String fileSize = "";
	private ByteCache fileCache;
	
	private DuplicateListener duplicateListener;
	private ServerSocket serverSocket;
    
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
        
        window.log("Opening listener to other servers." + "\n",  new Color(0 , 100, 0));

		try {
			serverSocket = new ServerSocket(ListenerPort);
			duplicateListener = new DuplicateListener(serverSocket);
			duplicateListener.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // TODO: change ports
        
        return true;
    }
    
    /*
     * Close the current connection.
     */
    public void close(){
    	if(socket != null){
			try {
				duplicateListener.close();
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
	        	boolean readBytesSuccess = false;
	        	boolean readTextSuccess = false;
	            if(isDownloading)
	            	readInputBytes();
	            else
	            	readInputText();
	            
	            if(readBytesSuccess && readTextSuccess){
	            	window.log("Lost Connection \n", Color.RED);
	            	break;
	            }
	        }
    	} catch(Exception e){
    		window.log(e.getMessage() + "\n", Color.RED);
    		window.log("Lost connection to server." + "\n", Color.RED);
    		close();
    	}
    }
    
    private boolean readInputText() throws IOException{
    	String line = in.readLine();

        // No message from the server.
        if(line == null){
    		//window.log("Lost connection to server." + "\n", Color.RED);
    		//close();
        	//break;
        	return false;
        }

        if (line.startsWith("IDENTIFY")) {			// The gateway is asking for identification.
        	// Respond with the password
            out.println(name + "," + Driver.getServerPassword() + "," + ListenerPort);	
            
        } else if(line.startsWith("CONNECTION_SUCCESS")){ 
        	window.log("Successfully connected to gateway!" + "\n", Color.BLACK);
        } else if (line.startsWith("MESSAGE")) {	// The gateway sent a message.
        	window.log(line.substring(8) + "\n", Color.BLACK);
        		
        } else if (line.startsWith("ERROR")) {		// The gateway sent an error message.
        	window.log(line.substring(6) + "\n", Color.RED);
        	
        } else if (line.startsWith("CLOSE")){
        	window.log("Gateway is closing. Disconnecting..." + "\n", Color.RED);
        	close();
        	
        } else if(line.startsWith("REQUEST")){
        	String requestContent = line.substring(8);
        	window.log("Gateway requests " + requestContent + "\n", Color.BLACK);
        	String[] contentArray = requestContent.split(",");
        	String requestType = contentArray[0];
        	if(requestType.equals("UPLOAD")){
            	fileName = contentArray[1];
            	fileSize = contentArray[2];
            	
        		window.log("Downloading file from gateway..." + "\n", Color.BLUE);
        		isDownloading = true;
        		//DownloadFileHandler dfh = new DownloadFileHandler(socket, out, window, fileName, fileSize);
        		//dfh.start();
        	}
        } else if(line.startsWith("DUPLICATERESPONSE")){
        	String response = line.substring(18);
        	if(response.equals("SUCCESS")){
        		// No need to duplicate.
        		fileCache = null; // Release the cache.

    			window.log("Duplicate not necessary. Cache released.\n", Color.BLACK);
        	} else{
    			window.log("Duplicating to ports " + response + "\n", Color.BLACK);
        		String[] ports = response.split(",");
        		Duplicator duplicator = new Duplicator(fileCache, ports);
        		duplicator.start();
        	}
        }

        return true;
    }
    
    private boolean readInputBytes() throws IOException{
    	out.println("PROCEEDTOUPLOAD:0");
        out.flush();
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        
    	try{
        	int byteOffset = 0;
            window.log("Creating Stream... \n", Color.blue);
    		FileOutputStream fos = new FileOutputStream("C:\\" + ServerName + "\\" + fileName);
    		fos.flush();

            window.log("Creating buffer... \n", Color.blue);
			byte[] buffer = new byte[Driver.getServerTransferBlockSize()];
			int n;

			fileCache = new ByteCache(fileName, Integer.parseInt(fileSize));
            window.log("Downloading chunks... \n", Color.blue);
			// Download the chunks.
			while(byteOffset < Long.parseLong(fileSize)){
				n = dis.read(buffer);
				fos.write(buffer, 0, n);
				byteOffset += n;
				fileCache.write(buffer, 0, n);
				
				window.log("Downloading... " + byteOffset + " out of " + fileSize + "\n", Color.BLACK);
			}
			
			fos.close();
			
			// Finalize this cache. 
			fileCache.setIsFinal(true);
			
			window.log("File succesfully saved." + "\n", Color.BLACK);
			
			// Attempt to duplicate
			window.log("Attempting to duplicate file...\n", Color.BLACK);
			out.println("DUPLICATEFILE:" + fileName);
    	} catch(Exception e){
    		window.log("Error: " + e.getMessage() + "\n", Color.RED);
    		window.log("Failed to download file from client." + "\n", Color.RED);
    		e.printStackTrace();
    		return false;
    	}
    	
    	isDownloading = false;
    	return true;
    }
}

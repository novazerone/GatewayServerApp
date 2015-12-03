package client;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

import javax.swing.JOptionPane;

import app.ByteCache;
import app.Driver;
import controllers.UploadController;
import gatewayServer.Gateway;
import server.Server;

public class Client {

	public static String ClientName = "";
    
	public static void main(String[] args) throws Exception {
    	ClientName = JOptionPane.showInputDialog("Client name");
        Client client = new Client(ClientName);
        
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
    	
        try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	        out = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			window.log("Failed to initialize streams." + "\n", Color.RED);
			window.log(e.getMessage() + "\n", Color.RED);
			window.log(e.getStackTrace() + "\n", Color.RED);
    		
    		return false;
		}

    	window.log("Successfully connected to gateway!" + "\n",  new Color(0 , 100, 0));
        
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
			
	    	window.log("Closed connection" + "\n",  new Color(0 , 100, 0));
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
    	if(!open()){
    		window.log("Failed to connect to gateway.\n", Color.red);
    		return;
    	}
    	
    	// Headers.
    	out.println("MODE:UPLOAD");
    	out.println("FILENAME:" + _file.getName());
    	out.println("FILESIZE:" + _file.length());
    	
        int byteOffset = 0;
    	while(true){
    		String input = in.readLine();
    		
    		if(input == null){
    			window.log("Failed to upload " + _file.getName() + "\n", Color.RED);
    			return;
    		}
    		
    		// The signal to proceed uploading.
    		if(input.startsWith("PROCEEDTOUPLOAD:")){
    			byteOffset = Integer.parseInt(input.substring(16));
    			if(byteOffset != 0)
    				window.log("Resuming upload at " + byteOffset + "..." + "\n", Color.BLUE);
    			else
    				window.log("Beginning upload..." + "\n", Color.BLUE);
    			break;
    		}
    	}
    	
    	try{
	    	out.flush();
	        BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
	        FileInputStream fis = new FileInputStream(_file);
	        BufferedInputStream bis = new BufferedInputStream(fis);
	        
	        int n = -1;
	        byte[] buffer;
	        
	        if(bis.available() - byteOffset < Driver.getClientTransferBlockSize())
	        	buffer = new byte[bis.available() - byteOffset];
	        else
	        	buffer = new byte[Driver.getClientTransferBlockSize()];
	        
	        // Resume from last point.
	        bis.skip(byteOffset);
	        
        	// Upload...
        	while((n = bis.read(buffer)) > -1){
	        	bos.write(buffer, 0, n);
	        	byteOffset += n;
	        	window.log("Uploading... " + byteOffset + " out of " + _file.length() + "\n", Color.BLACK);
	        };
	        
	        bis.close();
	        bos.close();
	        
	        window.log("Successfully uploaded file!" + "\n", Color.BLUE);
    	} catch(Exception e){
            window.log("Error: " + e.getMessage() + "\n", Color.RED);
            e.printStackTrace();
    	}
        
        close();
    }
    
    public void downloadFile(String _fileName) throws IOException{
    	if(_fileName == null)
    		return;

    	if(!open()){
    		window.log("Failed to connect to gateway.\n", Color.red);
    		return;
    	}
    	
    	out.println("MODE:DOWNLOAD");
    	out.println("FILENAME:" + _fileName);
    	out.println("FILESIZE:111"); // Supply a tmp size.
    	
        int downloadPort;
        int fileSize;
        
        int byteOffset = 0;
    	while(true){
    		String input = in.readLine();
    		
    		if(input == null){
    			window.log("No response from server. Failed to download " + _fileName + "\n", Color.RED);
    			return;
    		}
    		
    		// The signal to proceed uploading.
    		if(input.startsWith("PROCEEDTOPORT")){
    			String response = input.substring(14);
    			downloadPort = Integer.parseInt(response);
    			
    			if(byteOffset != 0)
    				window.log("Resuming download at " + byteOffset + "..." + "\n", Color.BLUE);
    			else
    				window.log("Beginning download..." + "\n", Color.BLUE);
    			break;
    		}
    	}
    	
    	Socket downloadConnection = null;
    	try{
			window.log("Connecting to server at port " + downloadPort + "\n", Color.BLUE);
    		downloadConnection = new Socket(Driver.getHost(), downloadPort);

			BufferedReader downloadIn = new BufferedReader(new InputStreamReader(downloadConnection.getInputStream()));
			PrintWriter downloadOut = new PrintWriter(downloadConnection.getOutputStream(), true);
			
			window.log("Succesfully connected to port " + downloadPort + "\n", Color.BLUE);
			
			downloadOut.println("FILENAME:" + _fileName);
			downloadOut.flush();

			while(true){
	    		String input = downloadIn.readLine();
	    		
	    		if(input == null){
	    			window.log("Failed to download " + _fileName + "\n", Color.RED);
	    			return;
	    		}
	    		
	    		// The signal to proceed uploading.
	    		if(input.startsWith("PROCEEDTODOWNLOAD:")){
	    			String response = input.substring(18);
	    			String[] parts = response.split(",");
	    			byteOffset = Integer.parseInt(parts[0]);
	    			fileSize = Integer.parseInt(parts[1]);
	    			
	    			if(byteOffset != 0)
	    				window.log("Resuming download at " + byteOffset + "..." + "\n", Color.BLUE);
	    			else
	    				window.log("Beginning download..." + "\n", Color.BLUE);
	    			break;
	    		}
	    	}

    		while(true){
            	try{
        			DataInputStream dis = new DataInputStream(downloadConnection.getInputStream());
        			File directory = new File(".\\_Clients\\" + ClientName);
                    directory.mkdir();
            		FileOutputStream fos = new FileOutputStream(directory +"\\" + _fileName,false);
					byte[] buffer = new byte[Driver.getClientTransferBlockSize()];
					int n;
					
					// Download the chunks.
					while(byteOffset < fileSize){
						n = dis.read(buffer);
						fos.write(buffer, 0, n);
						byteOffset += n;
						
						window.log("Downloading... " + byteOffset + " out of " + fileSize + "\n", Color.BLACK);
					}
					
					fos.close();
					
					window.log("File succesfully saved." + "\n", Color.BLACK);
					break;
            	} catch(Exception e){
            		window.log("Error: " + e.getMessage() + "\n", Color.RED);
            		window.log("Failed to download file from server." + "\n", Color.RED);
            		e.printStackTrace();
            	}
				break;
            }
    	} catch(Exception e){
    		e.printStackTrace();
    	} finally{
    		if(downloadConnection != null)
    			downloadConnection.close();
    	}
    	
        close();
    }
}

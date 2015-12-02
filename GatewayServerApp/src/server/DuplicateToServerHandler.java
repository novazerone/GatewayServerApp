package server;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import app.ByteCache;
import app.Driver;

public class DuplicateToServerHandler {
	
	private Socket socket;
	private  BufferedReader in;
	private PrintWriter out;
	
	private ByteCache file;
	
	public String input = "";
	
	public DuplicateToServerHandler(Socket _socket, BufferedReader _in, PrintWriter _out, ByteCache _byteCache){
		socket = _socket;
		in = _in;
		out = _out;
		file = _byteCache;
		Server.window.log("Initialized Handler", Color.BLACK);
		if(_byteCache == null)
			Server.window.log("Cache is null", Color.BLACK);
	}
	
	public void run() throws IOException{
		if(file == null)
    		return;

    	// Headers.
		out.println("REQUEST:UPLOAD," + file.getFileName() + "," + file.getCurrentSize());
		out.flush();

        int byteOffset = 0;

    	while(true){
    		input = in.readLine();
    		
    		if(input == null){
    			Server.window.log("Failed to upload " + file.getFileName() + "\n", Color.RED);
    			return; 
    		}
    		
    		if(input.startsWith("REQUESTDENIED")){
    			Server.window.log("Failed to upload " + file.getFileName() + ". Request denied. \n", Color.BLACK);
    			return;
    		}
    		
    		// The signal to proceed uploading.
    		if(input.startsWith("PROCEEDTOUPLOAD:")){
    			byteOffset = Integer.parseInt(input.substring(16));
    			if(byteOffset != 0)
    				Server.window.log("Resuming upload at " + byteOffset + "..." + "\n", Color.BLUE);
    			else
    				Server.window.log("Beginning upload..." + "\n", Color.BLUE);
    			
    			break;
    		}
    	}
    	
    	try{
	    	out.flush();
	        DataOutputStream bos = new DataOutputStream(socket.getOutputStream());
	        ByteArrayInputStream bais = new ByteArrayInputStream(file.getBytes());
	        BufferedInputStream bis = new BufferedInputStream(bais);
	        
	        int n = -1;
	        byte[] buffer;
	        
	        if(bis.available() - byteOffset < Driver.getServerTransferBlockSize())
	        	buffer = new byte[bis.available() - byteOffset];
	        else
	        	buffer = new byte[Driver.getServerTransferBlockSize()];
	        
	        // Resume from last point.
	        //bis.skip(byteOffset);
	        
        	// Upload...
        	while(byteOffset < file.getTargetSize()){
        		n = bis.read(buffer);
	        	bos.write(buffer, 0, n);
	        	byteOffset += n;
	        	Server.window.log("Uploading... " + byteOffset + " out of " + file.getTargetSize() + "\n", Color.BLACK);
	        };
	        
	        Server.window.log("Successfully uploaded file!" + "\n", Color.BLUE);
    	} catch(Exception e){
    		Server.window.log("Error: " + e.getMessage() + "\n", Color.RED);
            e.printStackTrace();
    	}
        
    	// Release references.
    	socket = null;
    	file = null;
	}
}

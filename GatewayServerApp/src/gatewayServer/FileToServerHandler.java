package gatewayServer;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import app.ByteCache;
import app.Driver;

public class FileToServerHandler extends Thread {
	
	private Socket connection;
	private PrintWriter out;
	
	private ByteCache file;
	
	public String input = "";
	int fileId;
	
	public FileToServerHandler(Socket _socket, PrintWriter _out, ByteCache _byteCache, int _fileId){
		connection = _socket;
		out = _out;
		file = _byteCache;
		fileId = _fileId;
	}
	
	@Override
	public void run(){
		if(file == null)
    		return;
		
    	// Headers.
		out.println("REQUEST:UPLOAD," + file.getFileName() + "," + file.getCurrentSize() + "," + fileId);
    	
        int byteOffset = 0;
    	while(true){
    		
    		if(input == null){
    			Gateway.log("Failed to upload " + file.getFileName() + "\n", Color.RED);
    			return;
    		}
    		
    		if(input.startsWith("REQUESTDENIED")){
    			Gateway.log("Failed to upload " + file.getFileName() + ". Request denied. \n", Color.BLACK);
    			return;
    		}
    		
    		// The signal to proceed uploading.
    		if(input.startsWith("PROCEEDTOUPLOAD:")){
    			byteOffset = Integer.parseInt(input.substring(16));
    			if(byteOffset != 0)
    				Gateway.log("Resuming upload at " + byteOffset + "..." + "\n", Color.BLUE);
    			else
    				Gateway.log("Beginning upload..." + "\n", Color.BLUE);
    			break;
    		}
    	}
    	
    	try{
	    	out.flush();
	        DataOutputStream bos = new DataOutputStream(connection.getOutputStream());
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
	        	Gateway.log("Uploading... " + byteOffset + " out of " + file.getTargetSize() + "\n", Color.BLACK);
	        };
	        
	        Gateway.log("Successfully uploaded file!" + "\n", Color.BLUE);
    	} catch(Exception e){
            Gateway.log("Error: " + e.getMessage() + "\n", Color.RED);
            e.printStackTrace();
    	}
        
    	// Release references.
    	connection = null;
    	file = null;
	}
}

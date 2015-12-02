package server;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import app.ByteCache;
import app.Driver;

public class DuplicateHandler extends Thread {
	private String name;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    
    private boolean isDownloading = false;
    private boolean isFinished = false;
    
    private String fileName;
    private String fileSize;
    
    public DuplicateHandler(Socket _socket) {
    	socket = _socket;
    }

    @Override
    public void run(){
        // Process all messages from the gateway, according to the protocol.
    	try{
	        while (true) {
	        	if(isFinished)
	        		break;
	        	
	        	boolean readBytesSuccess = false;
	        	boolean readTextSuccess = false;
	            if(isDownloading)
	            	readInputBytes();
	            else
	            	readInputText();
	            
	            if(readBytesSuccess && readTextSuccess){
	            	//window.log("Lost Connection \n", Color.RED);
	            	break;
	            }
	        }
    	} catch(Exception e){
    		//window.log(e.getMessage() + "\n", Color.RED);
    		//window.log("Lost connection to server." + "\n", Color.RED);
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

       if(line.startsWith("REQUEST")){
        	String requestContent = line.substring(8);
        	System.out.println("Gateway requests " + requestContent);
        	String[] contentArray = requestContent.split(",");
        	String requestType = contentArray[0];
        	if(requestType.equals("UPLOAD")){
            	fileName = contentArray[1];
            	fileSize = contentArray[2];
            	
        		//window.log("Downloading file from server..." + "\n", Color.BLUE);
        		isDownloading = true;
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
    		FileOutputStream fos = new FileOutputStream("C:\\" + Server.ServerName + "\\" + fileName);
    		fos.flush();

			byte[] buffer = new byte[Driver.getServerTransferBlockSize()];
			int n;

			// Download the chunks.
			while((n = dis.read(buffer)) > 0){
				fos.write(buffer, 0, n);
				byteOffset += n;
				
				//window.log("Downloading... " + byteOffset + " out of " + fileSize + "\n", Color.BLACK);
			}
			
			//fos.close();
			
			// Finalize this cache. 
			
			//window.log("File succesfully saved." + "\n", Color.BLACK);
			
			// Attempt to duplicate
			out.println("DUPLICATEFILE:" + fileName);
    	} catch(Exception e){
    		//window.log("Error: " + e.getMessage() + "\n", Color.RED);
    		//window.log("Failed to download file from client." + "\n", Color.RED);
    		e.printStackTrace();
    		return false;
    	}
    	
    	isDownloading = false;
    	isFinished = true;
    	return true;
    }
    
    public void close(){
    	if(socket != null){
			try {
				socket.close();
			} catch (IOException e) {

			}
    	}
    }
}
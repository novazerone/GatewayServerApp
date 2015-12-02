package gatewayServer;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

import app.ByteCache;
import app.CacheManager;
import app.Driver;

public class ClientHandler extends Thread {
	private String name;
    private Socket connection;
    private BufferedReader in;
    private PrintWriter out;
    private ObjectInputStream ois;
    private FileOutputStream fos;

    public ClientHandler(Socket _socket) {
        connection = _socket;
    }

    @Override
    public void run() {
        try {
        	// Initialize streams.
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            out = new PrintWriter(connection.getOutputStream(), true);

            Gateway.log("A client connected." + "\n", Color.BLACK);
            name = UUID.randomUUID().toString();
            
            // Get mode and file name.
            String mode = null;
            String fileName = null;
            long fileSize = 0;
            while(true){
            	String input = in.readLine();
            	
            	if(input == null){
            		Gateway.log("Client lost connection. \n", Color.BLACK);
            		throw new Exception();
            	}
            	
            	if(input.startsWith("MODE:")){
            		mode = input.substring(5);
            	} else if(input.startsWith("FILENAME:")){
            		fileName = input.substring(9);
            	} else if(input.startsWith("FILESIZE:")){
            		fileSize = Long.parseLong(input.substring(9));
            	}
            	
            	if(mode != null && fileName != null && fileSize != 0)
            		break;
            }
            

        	CacheManager cm = Gateway.getInstance().getCacheManager();
        	int byteOffset = 0;
            if(mode.equals("UPLOAD")){
            	if(cm.contains(fileName)){
            		if(!cm.getByteCache(fileName).getIsFinal()){
            			byteOffset = cm.getByteCache(fileName).getCurrentSize();
            			out.println("PROCEEDTOUPLOAD:" + byteOffset);
            			
            			Gateway.log("Resuming download at " + byteOffset + "..." + "\n", Color.BLUE);
            			
            		} else{
            			out.println("PROCEEDTOUPLOAD:0"); // Overwrite file.
            			Gateway.log("Beginning download..." + "\n", Color.BLUE);
            		}
            		
            	} else{
            		out.println("PROCEEDTOUPLOAD:0");
        			Gateway.log("Beginning download..." + "\n", Color.BLUE);
            	}
            	
                out.flush();
				BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());;
				
	            while(true){
	            	try{
						fos = new FileOutputStream(".\\_Gateway\\"+fileName, byteOffset > 0);
						
						byte[] buffer = new byte[Driver.getClientTransferBlockSize()];
						int n;

						ByteCache cache = null;
						if(byteOffset > 0){	// If the download was resumed.
							cache = cm.getByteCache(fileName);
						} else{
							cache = new ByteCache(fileName, (int)fileSize);
							cm.add(cache);
						}
						
						// Download the chunks.
						while((n = bis.read(buffer)) > 0){
							fos.write(buffer, 0, n);
							byteOffset += n;
							cache.write(buffer, 0, n);
							
							Gateway.log("Downloading... " + byteOffset + " out of " + fileSize + "\n", Color.BLACK);
						}
						
						fos.close();
						
						// Finalize this cache. 
						cache.setIsFinal(true);
						
						Gateway.log("File succesfully saved." + "\n", Color.BLACK);
						
						// Distribute
						Gateway.getInstance().distribute(cache);
	            	} catch(Exception e){
	            		Gateway.log("Error: " + e.getMessage() + "\n", Color.RED);
	            		Gateway.log("Failed to download file from client." + "\n", Color.RED);
	            		e.printStackTrace();
	            	}
					break;
	            }
            } else if (mode == "DOWNLOAD"){
            	
            }

        } catch (IOException e) {
            System.out.println(e);
            
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
        	close();
        }
    }
    
    public void close(){
        try {
            connection.close();
            if(out != null)
            	out.println("CLOSE");
        } catch (IOException e) {
        	System.out.println(e.getMessage());
        }
        
        Gateway.getInstance().getClientListener().removeClient(this);
		Gateway.log("Client " + name + " closed connection." + "\n", Color.BLACK);
    }
    
    public void setClientName(String _name){
    	name = _name;
    }
}
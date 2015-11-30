package gatewayServer;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

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
            	}
            	
            	
            	if(mode != null && fileName != null)
            		break;
            }
            
            
            if(mode.equals("UPLOAD")){
            	out.println("PROCEEDTOUPLOAD");
            	Gateway.log("Uploading...", Color.BLACK);
                out.flush();
            	ois = new ObjectInputStream(connection.getInputStream());
	            while(true){
	            	// TODO:
	            	// Add protocols
	            	// Split to chunks.
	            	Object o;
					try {
						o = ois.readObject();
		                Gateway.log("Receiving file..." + "\n", Color.BLACK);
		                byte[] file = (byte[]) o;
		                fos = new FileOutputStream(fileName);
		                fos.write(file);
		                fos.close();
		                
		                Gateway.log("File succesfully saved." + "\n", Color.BLACK);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
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
		Gateway.log(name + " closed connection." + "\n", Color.BLACK);
    }
    
    public void setClientName(String _name){
    	name = _name;
    }
}
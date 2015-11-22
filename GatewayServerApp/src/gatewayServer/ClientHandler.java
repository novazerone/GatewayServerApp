package gatewayServer;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;

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

            Gateway.log("A client is trying to connect." + "\n", Color.BLACK);
            out.println("IDENTIFY");
            while (true) {
                String uuid = in.readLine();
                
                if(uuid == null){
                    Gateway.log("Client failed to connect." + "\n", Color.BLACK);
                    break;
                }
                
                name = uuid;
                Gateway.log("Client ", Color.BLACK);
                Gateway.log(name, Color.BLUE);
                Gateway.log(" connected." + "\n", Color.BLACK);
                
                // Signal the client that the connection was established.
                out.println("CONNECTION_SUCCESS");
                break;
            }

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
	                fos = new FileOutputStream("file.aos");
	                fos.write(file);
	                fos.close();
	                Gateway.log("File succesfully saved." + "\n", Color.BLACK);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	
				/*
            	String message = in.readLine();
            	if(message == null){
            		break;
            	}
            	
            	if(message.startsWith("MESSAGE")){
            		Gateway.log(message.substring(8) + "\n", Color.BLACK);
            	}*/
            }

        } catch (IOException e) {
            System.out.println(e);
            
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
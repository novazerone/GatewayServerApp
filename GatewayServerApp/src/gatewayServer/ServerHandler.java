package gatewayServer;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import app.Driver;

public class ServerHandler extends Thread {
	private String name;
    private Socket connection;
    private BufferedReader in;
    private PrintWriter out;

    public ServerHandler(Socket _socket) {
        connection = _socket;
    }

    @Override
    public void run() {
        try {
        	// Initialize streams.
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            out = new PrintWriter(connection.getOutputStream(), true);

            Gateway.log("A server is trying to connect. Identifying..." + "\n", Color.BLACK);
            out.println("IDENTIFY");
            while (true) {
                String identification = in.readLine();
                String[] parts = identification.split("[|]");
                String serverName = parts[0];
                String password = parts[1];
                
                if (!password.equals(Driver.getServerPassword()))
                	continue; // TODO: Close connection on failure. Or something.
                
                name = serverName;
                Gateway.log("Server ", Color.BLACK);
                Gateway.log(name, Color.BLUE);
                Gateway.log(" connected." + "\n", Color.BLACK);
                
                // Signal the server that the connection was established.
                out.println("CONNECTION_SUCCESS");
                break;
            }
            
            while(true){
            	String message = in.readLine();
            	
            	if(message == null){
            		break;
            	}
            	
            	if(message.startsWith("MESSAGE")){
            		Gateway.log(message.substring(8) + "\n", Color.BLACK);
            	}
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
        
        Gateway.getInstance().getServerListener().removeServer(this);
		Gateway.log(name + " closed connection." + "\n", Color.BLACK);
    }
    
    public void setServerName(String _name){
    	name = _name;
    }
}
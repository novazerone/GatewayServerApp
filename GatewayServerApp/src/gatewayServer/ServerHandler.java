package gatewayServer;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import app.ByteCache;
import app.Driver;
import database.ServerJDBCTemplate;

public class ServerHandler extends Thread {
	private String name;
    private Socket connection;
    private BufferedReader in;
    private PrintWriter out;
    
    private FileToServerHandler ftsh;
    
    private int listenerPort = 0;	// The port in which this server will listen for duplications.
	private ServerJDBCTemplate dbServer;
	private int dbServerId;

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
                String[] parts = identification.split(",");
                String serverName = parts[0];
                String password = parts[1];
                listenerPort = Integer.parseInt(parts[2]);
                
                if (!password.equals(Driver.getServerPassword()))
                	continue; // TODO: Close connection on failure. Or something.
                
                name = serverName;
                Gateway.log("Server ", Color.BLACK);
                Gateway.log(name, Color.BLUE);
                Gateway.log(" connected." + "\n", Color.BLACK);

            	dbServer = new ServerJDBCTemplate();
                // Signal the server that the connection was established.
                out.println("CONNECTION_SUCCESS");
                out.flush();
                break;
            }
            
            while(true){
            	String message = in.readLine();
            	
            	if(message == null){
            		if(ftsh != null)
            			ftsh.input = null;
            		break;
            	}
            	
            	if(message.startsWith("MESSAGE")){
            		Gateway.log(message.substring(8) + "\n", Color.BLACK);
            	} else if(message.startsWith("DUPLICATEFILE")){
            		String fileName = message.substring(14);
            		// TODO: Query stuff. If file is not 2/3, respond with another server's port 
            		// so that this guy can establish a connection with it independently.
            		if(Gateway.getInstance().getServerListener().getSize() < 2)
            			out.println("DUPLICATERESPONSE:SUCCESS");
            		else {
            			String ports = Gateway.getInstance().getRemainingServers();
            			// TODO: Respond with a CSV of server ports.
            			out.println("DUPLICATERESPONSE:" + ports);
            		}
            	}
            	
            	if(ftsh != null)
            		ftsh.input = message;
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
            dbServer.update(dbServer.getServer(name).getId(), false);
            if(out != null)
            	out.println("CLOSE");
        } catch (IOException e) {
        	System.out.println(e.getMessage());
        }
        
        Gateway.getInstance().getServerListener().removeServer(this);
		Gateway.log("Server " + name + " closed connection." + "\n", Color.BLACK);
    }
    
    public void setServerName(String _name){
    	name = _name;
    }
    
    public String getServerName(){
    	return name;
    }
    
    public Socket getSocket(){
    	return connection;
    }
    
    public int getDuplicationPort(){
    	return listenerPort;
    }
    
    public void uploadFile(ByteCache _byteCache){

		ftsh = new FileToServerHandler(connection, out, _byteCache);
		ftsh.start();
    }
}
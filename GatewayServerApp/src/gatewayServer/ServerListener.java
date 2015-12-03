package gatewayServer;

import database.ServerJDBCTemplate;
import database.models.Server_File;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.List;
import java.util.Vector;

/*
 * ServerListener
 * - A thread that listens to sub-server connections.
 */
public class ServerListener extends Thread{
	
	private ServerSocket socket;
	private Vector<ServerHandler> servers = new Vector<ServerHandler>();
	
	public ServerListener (ServerSocket _socket){
		socket = _socket;
	}
	
	public void run(){
        try {
        	// Listen for server connections
            while (true) {
        		ServerHandler server = new ServerHandler(socket.accept());
        		server.start();

        		addServer(server);
            }
        } catch (SocketException e) {
			System.out.println("Socket closed.");
			
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public boolean addServer(ServerHandler _server){
		return servers.add(_server);
	}
	
	public boolean removeServer(ServerHandler _server){

		ServerJDBCTemplate dbServer = new ServerJDBCTemplate();
		List<Server_File> files = dbServer.checkFile(_server.getDuplicationPort());
		
		return servers.remove(_server);
	}
	
	public ServerHandler getServerHandler(int id){
		return servers.get(id);
	}
	
	public ServerHandler getServerHandler(String _name){
		for(int x = 0; x < servers.size(); x++){
			ServerHandler sh = servers.get(x);
			if(sh.getServerName().equals(_name))
				return sh;
		}
		
		return null;
	}
	
	public int getSize(){
		return servers.size();
	}
	
	public void close(){
		for(int x = 0; x < servers.size(); x++){
			servers.get(x).close();
		}
		
		servers.clear();
		
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

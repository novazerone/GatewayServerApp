package gatewayServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Vector;

/*
 * ClientListener
 * - A thread that listens to client connections.
 */
public class ClientListener extends Thread{
	
	private ServerSocket socket;
	private Vector<ClientHandler> clients = new Vector<ClientHandler>();
	
	public ClientListener (ServerSocket _socket){
		socket = _socket;
	}
	
	public void run(){
        try {
        	// Listen for client connections
            while (true) {
            	ClientHandler client = new ClientHandler(socket.accept());
            	client.start();

        		addClient(client);
            }
        } catch (SocketException e) {
			System.out.println("Socket closed.");
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public boolean addClient(ClientHandler _client){
		return clients.add(_client);
	}
	
	public boolean removeClient(ClientHandler _client){
		return clients.remove(_client);
	}
	
	public void close(){
		for(int x = 0; x < clients.size(); x++){
			clients.get(x).close();
		}
		
		clients.clear();
		
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

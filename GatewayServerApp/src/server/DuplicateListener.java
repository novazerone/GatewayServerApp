package server;

import java.awt.Color;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Vector;

/**
 * DuplicateListener
 * - A thread that listens to sub-server connections.
 */
public class DuplicateListener extends Thread{

	private ServerSocket socket;
	private Vector<DuplicateHandler> servers = new Vector<DuplicateHandler>();

	public DuplicateListener (ServerSocket _socket){
		socket = _socket;
	}

	@Override
	public void run(){
		try {
			Server.window.log("Duplication Listener at port " + socket.getLocalPort() + "\n", Color.BLUE);
			// Listen for server connections
			while (true) {
				DuplicateHandler server = new DuplicateHandler(socket.accept());
				server.start();

				addServer(server);
			}
		} catch (SocketException e) {
			System.out.println("Socket closed.");

		} catch(IOException e){
			System.out.println("Socket closed.");
		}
	}

	public boolean addServer(DuplicateHandler _server){
		return servers.add(_server);
	}

	public boolean removeServer(DuplicateHandler _server){
		return servers.remove(_server);
	}

	public DuplicateHandler getServerHandler(int id){
		return servers.get(id);
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

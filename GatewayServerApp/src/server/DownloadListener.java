package server;

import java.awt.Color;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Vector;

/**
 * DownloadListener
 * - A thread that listens to client connections.
 */
public class DownloadListener extends Thread{

	private ServerSocket socket;
	private Vector<DownloadHandler> clients = new Vector<DownloadHandler>();

	public DownloadListener (ServerSocket _socket){
		socket = _socket;
	}

	@Override
	public void run(){
		try {
			Server.window.log("Download Listener at Port " + socket.getLocalPort() + "\n", Color.BLUE);
			// Listen for server connections
			while (true) {
				DownloadHandler server = new DownloadHandler(socket.accept());
				server.start();

				addClient(server);
			}
		} catch (SocketException e) {
			System.out.println("Socket closed.");

		} catch(IOException e){
			e.printStackTrace();
		}
	}

	public boolean addClient(DownloadHandler _client){
		return clients.add(_client);
	}

	public boolean removeClient(DownloadHandler _client){
		return clients.remove(_client);
	}

	public DownloadHandler getDownloadHandler(int id){
		return clients.get(id);
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

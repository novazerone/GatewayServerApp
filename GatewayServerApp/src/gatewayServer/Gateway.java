package gatewayServer;

import java.awt.Color;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;

import app.ByteCache;
import app.CacheManager;
import app.Driver;
import database.ServerJDBCTemplate;

public class Gateway extends Thread {

	public static void main(String[] args) {
		System.out.println("[SERVER] The gateway server is running.");
		Gateway gateway = Gateway.getInstance();

		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			@Override
			public void run()
			{
				gateway.close();
			}
		});

		gateway.run();
	}

	// Singleton
	private static Gateway instance = null;
	public static Gateway getInstance(){
		if(instance == null)
			instance = new Gateway();
		return instance;
	}

	private ServerSocket serverSocket;
	private ServerSocket clientSocket;

	private ServerListener serverListener;
	private ClientListener clientListener;

	private GatewayUI window = new GatewayUI("Gateway Server");

	private CacheManager cacheManager;

	public Gateway(){
		initialize();
	}

	public void initialize(){
		window.log("Initializing Server Listener..." + "\n", Color.BLACK);
		try {
			serverSocket = new ServerSocket(Driver.getServerPort());
			serverListener = new ServerListener(serverSocket);
		} catch (IOException e) {
			window.log("Failed to initialize Server Listener..." + "\n", Color.BLACK);
			window.log(e.getMessage() + "\n", Color.RED);
			window.log(e.getStackTrace() + "\n", Color.RED);

			return;
		}

		window.log("Successfully initialized Server Listener!" + "\n",  new Color(0 , 100, 0));

		window.log("Initializing Client Listener..." + "\n", Color.BLACK);
		try {
			clientSocket = new ServerSocket(Driver.getClientPort());
			clientListener = new ClientListener(clientSocket);
		} catch (IOException e) {
			window.log("Failed to initialize Client Listener..." + "\n", Color.BLACK);
			window.log(e.getMessage() + "\n", Color.RED);
			window.log(e.getStackTrace() + "\n", Color.RED);

			return;
		}

		window.log("Successfully initialized Client Listener!" + "\n",  new Color(0 , 100, 0));
		// Initially, all servers are down.
		ServerJDBCTemplate server = new ServerJDBCTemplate();
		server.downAllServers();

	}

	public void run(){
		serverListener.start();
		clientListener.start();
		
		cacheManager = new CacheManager(1000000000);	// 1 GB
		
		try {
			synchronized(serverListener){
				serverListener.wait();
			}
		} catch (InterruptedException e) {
			window.log(e.getMessage() + "\n", Color.RED);
			window.log(e.getStackTrace() + "\n", Color.RED);

			return;
		}
	}

	public void close(){
		window.log("Closing gateway...", Color.BLACK);

		// Disconnect from the clients first.
		clientListener.close();

		// Close the server listener.
		synchronized(serverListener){
			serverListener.close();
			serverListener.notify();
		}
	}

	public ServerListener getServerListener(){
		return serverListener;
	}

	public ClientListener getClientListener(){
		return clientListener;
	}

	public CacheManager getCacheManager(){
		return cacheManager;
	}

	public void distribute(ByteCache _byteCache, List<database.models.Server> _servers, int fileId){
		if(_servers.size() == 0)
			return;

		Gateway.log("Retrieving handler...\n", Color.RED);
		ServerHandler handler = serverListener.getServerHandler(_servers.get(0).getName());
		if(handler == null){
			Gateway.log("Handler not found for server '" + _servers.get(0).getName() + "'\n", Color.BLACK);
			return;
		}

		Gateway.log("Sending file to server " + _servers.get(0).getUploadPort() + "...\n", Color.BLACK);
		_servers.remove(0);
		handler.uploadFile(_byteCache, _servers, fileId);
	}
	
	public void distribute(List<database.models.Server> _srcServers, List<database.models.Server> _dstServers, String _fileName, int fileId){
		if(_srcServers.size() == 0){
			Gateway.log("No source servers.\n", Color.RED);
			return;
		}
		
		if(_dstServers.size() == 0){
			Gateway.log("No destination servers.\n", Color.RED);
			return;
		}
		
		ServerHandler handler = serverListener.getServerHandler(_srcServers.get(0).getName());
		if(handler == null){
			Gateway.log("Handler not found for server '" + _srcServers.get(0).getName() + "'\n", Color.BLACK);
			return;
		}

		handler.uploadFile(_dstServers, _fileName, fileId);
	}

	public static void log(String _msg, Color _c){
		Gateway.getInstance().window.log(_msg, _c);
	}
	
	public GatewayUI getWindow(){
		return window;
	}
}

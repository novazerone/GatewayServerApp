package gatewayServer;

import java.awt.Color;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;

import app.ByteCache;
import app.CacheManager;
import app.Driver;
import app.LogWindow;

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
    
    private LogWindow window = new LogWindow("Gateway Server");
    
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
	
	public void distribute(ByteCache _byteCache){
		Gateway.log("Sending file to server..." + "\n", Color.BLACK);
		
		// TODO: Randomize on who gets the file.
		serverListener.getServerHandler(0).uploadFile(_byteCache);
	}
	
	public String getRemainingServers(){
		// TODO: Query for remaining servers.
		// Return CSV of ports.
		return "8083,8084";
	}
	
	public static void log(String _msg, Color _c){
		Gateway.getInstance().window.log(_msg, _c);
	}
}

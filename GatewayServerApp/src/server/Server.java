package server;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JOptionPane;

import app.ByteCache;
import app.Driver;
import database.FileJDBCTemplate;
import database.ServerJDBCTemplate;

public class Server {

	public static String ServerName = "";
	public static int ListenerPort;	// Duplicate port.
	public static int DownloadPort;

	public static void main(String[] args) throws Exception {
		Server server = new Server();

		do {
			getNameAndPort();
		} while(!server.open());

		server.updateServerUI();

		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			@Override
			public void run() {
				server.close();
			}
		});

		server.run();
	}

	private static void getNameAndPort() {
		ServerName = JOptionPane.showInputDialog("Server name");
		ListenerPort = 8000 + Integer.parseInt(ServerName);
		DownloadPort = 9000 + Integer.parseInt(ServerName);
		//ListenerPort = Integer.parseInt(JOptionPane.showInputDialog("ListenerPort (from 8001)"));
		//DownloadPort = Integer.parseInt(JOptionPane.showInputDialog("DownloadPort (from 9001)"));
	}

	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;

	public static ServerUI window;

	private boolean isDownloading = false;
	private String fileName = "";
	private String fileSize = "";
	private int fileId;
	private ByteCache fileCache;

	private DuplicateListener duplicateListener;
	private ServerSocket serverSocket;

	private DownloadListener downloadListener;
	private ServerSocket downloadServerSocket;

	private ServerJDBCTemplate dbServer;
	private int dbServerId;

	public Server() {
		window = new ServerUI("Server - " + ServerName);
		window.log("Server initialized." + "\n", Color.BLACK);
	}

	public void updateServerUI() {	
		dbServer = new ServerJDBCTemplate();
		database.models.Server dbServerObj = dbServer.getServer(ServerName);
		if(dbServerObj == null){
			dbServerId = dbServer.create(ServerName, ListenerPort, DownloadPort);
			dbServer.update(dbServerId, true);
			window.log("Server is new. Automatically registered in database.\n", Color.BLACK);
		} else{
			dbServerId = dbServerObj.getId();
			dbServer.update(dbServerId, true);
		}
		window.getFrame().setTitle("Server - " + ServerName + " // Listening @Port:" + ListenerPort + " // Downloading @Port:" + DownloadPort);
		window.log("UUID: " + ServerName + "\n", Color.BLACK);
		window.setServerName(ServerName);
	}

	/**
	 * Opens a connection to the server.
	 */
	public boolean open(){
		try{
			window.log("Connecting to gateway at port " + Driver.getServerPort()  + "\n", Color.BLACK);

			// Make a connection.
			socket = new Socket(Driver.getHost(), Driver.getServerPort());
		} catch (IOException e){
			window.log("Failed to connect to server!" + "\n", Color.RED);
			window.log(e.getMessage() + "\n", Color.RED);
			window.log(e.getStackTrace() + "\n", Color.RED);

			return false;
		}

		window.log("Successfully connected to gateway!" + "\n",  new Color(0 , 100, 0));

		// Initialize streams.
		window.log("Initializing streams..." + "\n", Color.BLACK);
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			window.log("Failed to initialize streams." + "\n", Color.RED);
			window.log(e.getMessage() + "\n", Color.RED);
			window.log(e.getStackTrace() + "\n", Color.RED);

			return false;
		}

		window.log("Successfully initialized streams!" + "\n",  new Color(0 , 100, 0));

		window.log("Opening listener to other servers." + "\n",  new Color(0 , 100, 0));

		try {
			serverSocket = new ServerSocket(ListenerPort);
			duplicateListener = new DuplicateListener(serverSocket);
			duplicateListener.start();

			downloadServerSocket = new ServerSocket(DownloadPort);
			downloadListener = new DownloadListener(downloadServerSocket);
			downloadListener.start();
		} catch (IOException e) {
			//e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Port already in use.", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}

		return true;
	}

	/**
	 * Close the current connection.
	 */
	public void close(){
		if(socket != null){
			try {
				dbServer.update(dbServerId, false);
				duplicateListener.close();
				downloadListener.close();
				socket.close();
			} catch (IOException e) {
				window.log("Failed to close connection." + "\n", Color.RED);
				window.log(e.getMessage() + "\n", Color.RED);
				window.log(e.getStackTrace() + "\n", Color.RED);

				return;
			}
		}
	}

	/**
	 * Server logic.
	 */
	private void run(){
		// Process all messages from the gateway, according to the protocol.
		try{
			while (true) {
				boolean readBytesSuccess = false;
				boolean readTextSuccess = false;
				if(isDownloading)
					readInputBytes();
				else
					readInputText();

				if(readBytesSuccess && readTextSuccess){
					window.log("Lost Connection \n", Color.RED);
					break;
				}
			}
		} catch(Exception e){
			window.log(e.getMessage() + "\n", Color.RED);
			window.log("Lost connection to server." + "\n", Color.RED);
			close();
		}
	}

	private boolean readInputText() throws IOException{
		String line = in.readLine();

		// No message from the server.
		if(line == null){
			//window.log("Lost connection to server." + "\n", Color.RED);
			//close();
			//break;
			return false;
		}

		if (line.startsWith("IDENTIFY")) {			// The gateway is asking for identification.
			// Respond with the password
			out.println(ServerName + "," + Driver.getServerPassword() + "," + ListenerPort);	

		} else if(line.startsWith("CONNECTION_SUCCESS")){ 
			window.log("Successfully connected to gateway!" + "\n", Color.BLACK);
		} else if (line.startsWith("MESSAGE")) {	// The gateway sent a message.
			window.log(line.substring(8) + "\n", Color.BLACK);

		} else if (line.startsWith("ERROR")) {		// The gateway sent an error message.
			window.log(line.substring(6) + "\n", Color.RED);

		} else if (line.startsWith("CLOSE")){
			window.log("Gateway is closing. Disconnecting..." + "\n", Color.RED);
			close();

		} else if(line.startsWith("REQUEST")){
			String requestContent = line.substring(8);
			window.log("Gateway requests " + requestContent + "\n", Color.BLACK);
			String[] contentArray = requestContent.split(",");
			String requestType = contentArray[0];
			if(requestType.equals("UPLOAD")){
				fileName = contentArray[1];
				fileSize = contentArray[2];
				fileId = Integer.parseInt(contentArray[3]);

				window.log("Downloading file from gateway..." + "\n", Color.BLUE);
				isDownloading = true;
				//DownloadFileHandler dfh = new DownloadFileHandler(socket, out, window, fileName, fileSize);
				//dfh.start();
			}
		}  else if(line.startsWith("DUPLICATERESPONSEWITHNAME")){
			String response = line.substring(26);

			window.log("Duplicating to ports " + response + "\n", Color.BLACK);
			String[] ports = response.split(",");
			String fName = ports[0];

			String[] fPorts = new String[ports.length - 1];
			for(int x = 1; x < ports.length; x++){
				fPorts[x - 1] = ports[x];
			}

			File file = new File(".\\_Servers\\" + ServerName + "\\" + fName);
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
			
			ByteCache cache = new ByteCache(fName, (int)file.length());
			byte[] b = new byte[(int)file.length()];
			bis.read(b);
			cache.write(b);
			
			FileJDBCTemplate dbFile = new FileJDBCTemplate();
			database.models.File gFile = dbFile.getFile(fName);
			window.log("Duplicating FID: " + gFile.getId() + "\n", Color.RED);

			Duplicator duplicator = new Duplicator(cache, fPorts, gFile.getId());
			duplicator.start();
		} else if(line.startsWith("DUPLICATERESPONSE")){
			String response = line.substring(18);
			if(response.equals("SUCCESS")){
				// No need to duplicate.
				fileCache = null; // Release the cache.

				window.log("Duplicate not necessary. Cache released.\n", Color.BLACK);
			} else{
				window.log("Duplicating to ports " + response + "\n", Color.BLACK);
				String[] ports = response.split(",");
				Duplicator duplicator = new Duplicator(fileCache, ports, fileId);
				duplicator.start();
			}
		}

		return true;
	}

	private boolean readInputBytes() throws IOException{
		out.println("PROCEEDTOUPLOAD:0");
		out.flush();
		DataInputStream dis = new DataInputStream(socket.getInputStream());

		try{
			int byteOffset = 0;
			window.log("Creating Stream... \n", Color.blue);
			File directory = new File(".\\_Servers\\" + ServerName);
			directory.mkdir();
			FileOutputStream fos = new FileOutputStream(directory +"\\" + fileName);
			fos.flush();

			window.log("Creating buffer... \n", Color.blue);
			byte[] buffer = new byte[Driver.getServerTransferBlockSize()];
			int n;

			fileCache = new ByteCache(fileName, Integer.parseInt(fileSize));
			window.log("Downloading chunks... \n", Color.blue);
			// Download the chunks.
			while(byteOffset < Long.parseLong(fileSize)){
				n = dis.read(buffer);
				fos.write(buffer, 0, n);
				byteOffset += n;
				fileCache.write(buffer, 0, n);

				window.log("Downloading... " + byteOffset + " out of " + fileSize + "\n", Color.BLACK);
			}

			fos.close();

			// Finalize this cache. 
			fileCache.setIsFinal(true);

			window.log("File succesfully saved." + "\n", Color.BLACK);

			window.log("Saving to database... FID: " + fileId + "\n", Color.BLACK);
			ServerJDBCTemplate db = new ServerJDBCTemplate();
			db.updateUploadFinish(fileId, db.getServer(ServerName).getId(), 1);

			// Attempt to duplicate
			window.log("Attempting to duplicate file...\n", Color.BLACK);
			out.println("DUPLICATEFILE:" + fileName);
		} catch(Exception e){
			window.log("Error: " + e.getMessage() + "\n", Color.RED);
			window.log("Failed to download file from client." + "\n", Color.RED);
			e.printStackTrace();
			return false;
		}

		isDownloading = false;
		return true;
	}
}

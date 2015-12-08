package client;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import app.Driver;
import database.FileJDBCTemplate;

public class Client {

	public static String ClientName = "";

	public static void main(String[] args) throws Exception {
		ClientName = JOptionPane.showInputDialog("Client name");
		Client client = new Client(ClientName);

		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run() {
				client.close();
			}
		});
	}

	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;

	private ClientUI window;


	public Client(String _name) {
		window = new ClientUI(this, "Client - " + ClientName);
		window.log("Client initialized." + "\n", Color.BLACK);

		// Enable window operation.
		window.setEnable(true);
	}

	/**
	 * Opens a connection to the server.
	 */
	public boolean open(){
		try{
			window.log("Connecting to gateway at port " + Driver.getClientPort()  + "\n", Color.BLACK);

			// Make a connection.
			socket = new Socket(Driver.getHost(), Driver.getClientPort());
		} catch (IOException e){
			window.log("Failed to connect to server!" + "\n", Color.RED);
			window.log(e.getMessage() + "\n", Color.RED);
			window.log(e.getStackTrace() + "\n", Color.RED);

			return false;
		}

		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			window.log("Failed to initialize streams." + "\n", Color.RED);
			window.log(e.getMessage() + "\n", Color.RED);
			window.log(e.getStackTrace() + "\n", Color.RED);

			return false;
		}

		window.log("Successfully connected to gateway!" + "\n",  new Color(0 , 100, 0));

		return true;
	}

	/**
	 * Close the current connection.
	 */
	public void close(){
		if(socket != null){
			try {
				socket.close();
			} catch (IOException e) {
				window.log("Failed to close connection." + "\n", Color.RED);
				window.log(e.getMessage() + "\n", Color.RED);
				window.log(e.getStackTrace() + "\n", Color.RED);

				return;
			}

			window.log("Closed connection" + "\n",  new Color(0 , 100, 0));
		}
	}

	/**
	 * Client logic.
	 */
	private void run(){
		// Process all messages from the gateway, according to the protocol.
		try{
			while (true) {
				String line = in.readLine();				

				if (line.startsWith("IDENTIFY")) { // The server is asking for identification.
					// Respond with the UUID.
					out.println(ClientName);	
				} else if(line.startsWith("CONNECTION_SUCCESS")){ 
					window.log("Successfully connected to gateway!" + "\n", Color.BLACK);
				} else if (line.startsWith("MESSAGE")) {	// The server sent a message.
					window.log(line.substring(8) + "\n", Color.BLACK);
				} else if (line.startsWith("ERROR")) {		// The server sent an error message.
					window.log(line.substring(6) + "\n", Color.RED);
				} else if (line.startsWith("CLOSE")){
					window.log("Gateway is closing. Disconnecting..." + "\n", Color.RED);
					close();
				} else if(line.isEmpty() && socket == null){ // No message from the server.
					window.log("Lost connection to server." + "\n", Color.RED);
					close();
					break;
				}
			}
		} catch(Exception e){
			window.log("Lost connection to server." + "\n", Color.RED);
			close();
		}
	}

	/**
	 * Sends a file to the gateway.
	 */
	public void trySendFile(File _file) throws IOException{
		new java.util.Timer().schedule( 
		        new java.util.TimerTask() {
		            @Override
		            public void run() {
		                try {
							sendFile(_file);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		            }
		        }, 
		        100 
		);
	}
	
	public void sendFile(File _file) throws IOException{
		if(_file == null)
			return;

		// Connect to the server.
		if(!open()){
			window.log("Failed to connect to gateway.\n", Color.red);
			return;
		}

		// Headers.
		out.println("MODE:UPLOAD");
		out.println("FILENAME:" + _file.getName());
		out.println("FILESIZE:" + _file.length());

		int byteOffset = 0;
		while(true){
			String input = in.readLine();

			if(input == null){
				window.log("Failed to upload " + _file.getName() + "\n", Color.RED);
				return;
			}

			// The signal to proceed uploading.
			if(input.startsWith("PROCEEDTOUPLOAD:")){
				byteOffset = Integer.parseInt(input.substring(16));
				if(byteOffset != 0)
					window.log("Resuming upload at " + byteOffset + "..." + "\n", Color.BLUE);
				else
					window.log("Beginning upload..." + "\n", Color.BLUE);
				break;
			}
		}

		try{
			out.flush();
			BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
			FileInputStream fis = new FileInputStream(_file);
			BufferedInputStream bis = new BufferedInputStream(fis);

			int n = -1;
			byte[] buffer;

			if(bis.available() - byteOffset < Driver.getClientTransferBlockSize())
				buffer = new byte[bis.available() - byteOffset];
			else
				buffer = new byte[Driver.getClientTransferBlockSize()];

			// Resume from last point.
			bis.skip(byteOffset);

			System.out.println("Starting bar...");
			// Upload...
			while((n = bis.read(buffer)) > -1){
				bos.write(buffer, 0, n);
				byteOffset += n;

				int percent = (int)(((float) byteOffset / _file.length())*100);
				SwingUtilities.invokeLater(new ProgressBarAnimation(window, percent));
			};

			bis.close();
			bos.close();

			window.log("Successfully uploaded file!" + "\n", Color.BLUE);
		} catch(Exception e){
			window.log("Error: " + e.getMessage() + "\n", Color.RED);
			e.printStackTrace();
		}
		SwingUtilities.invokeLater(new ProgressBarAnimation(window, 0));
		close();
	}

	public void downloadFile(String _fileName) throws IOException{
		FileJDBCTemplate dbFile = new FileJDBCTemplate();

		if(_fileName == null)
			return;

		if(!open()){
			window.log("Failed to connect to gateway.\n", Color.red);
			return;
		}

		database.models.File f = dbFile.getFile(_fileName);
		out.println("MODE:DOWNLOAD");
		out.println("FILENAME:" + f.getFile_name());
		out.println("FILESIZE:" + f.getFile_size());

		int downloadPort;
		int fileSize;

		int byteOffset = 0;
		while(true){
			String input = in.readLine();

			if(input == null){
				window.log("No response from server. Failed to download " + f.getFile_name() + "\n", Color.RED);
				return;
			}

			// The signal to proceed downloading.
			if(input.startsWith("PROCEEDTOPORT")){
				String response = input.substring(14);
				downloadPort = Integer.parseInt(response);

				if(byteOffset != 0)
					window.log("Resuming download at " + byteOffset + "..." + "\n", Color.BLUE);
				else
					window.log("Beginning download..." + "\n", Color.BLUE);
				break;
			}
		}

		Socket downloadConnection = null;
		try{
			window.log("Connecting to server at port " + downloadPort + "\n", Color.BLUE);
			downloadConnection = new Socket(Driver.getHost(), downloadPort);

			BufferedReader downloadIn = new BufferedReader(new InputStreamReader(downloadConnection.getInputStream()));
			PrintWriter downloadOut = new PrintWriter(downloadConnection.getOutputStream(), true);

			window.log("Succesfully connected to port " + downloadPort + "\n", Color.BLUE);

			downloadOut.println("FILENAME:" + f.getFile_name());
			downloadOut.flush();

			while(true){
				String input = downloadIn.readLine();

				if(input == null){
					window.log("Failed to download " + f.getFile_name() + "\n", Color.RED);
					return;
				}

				// The signal to proceed downloading.
				if(input.startsWith("PROCEEDTODOWNLOAD:")){
					String response = input.substring(18);
					String[] parts = response.split(",");
					byteOffset = Integer.parseInt(parts[0]);
					fileSize = Integer.parseInt(parts[1]);

					if(byteOffset != 0)
						window.log("Resuming download at " + byteOffset + "..." + "\n", Color.BLUE);
					else
						window.log("Beginning download..." + "\n", Color.BLUE);
					break;
				}
			}

			while(true){
				try{
					DataInputStream dis = new DataInputStream(downloadConnection.getInputStream());
					File directory = new File(".\\_Clients\\" + ClientName);
					directory.mkdir();
					FileOutputStream fos = new FileOutputStream(directory +"\\" + f.getFile_name(),false);
					byte[] buffer = new byte[Driver.getClientTransferBlockSize()];
					int n;

					// Download the chunks.
					while(byteOffset < fileSize){
						n = dis.read(buffer);
						fos.write(buffer, 0, n);
						byteOffset += n;


						window.log("Downloading... " + byteOffset + " out of " + fileSize + "\n", Color.BLACK);
					}

					fos.close();

					window.log("File succesfully saved." + "\n", Color.BLACK);
					break;
				} catch(Exception e){
					window.log("Error: " + e.getMessage() + "\n", Color.RED);
					window.log("Failed to download file from server." + "\n", Color.RED);
					e.printStackTrace();
				}
				break;
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally{
			if(downloadConnection != null)
				downloadConnection.close();
		}

		close();
	}
	
	private class ProgressBarAnimation implements Runnable {
		private int percent;
		private ClientUI window;
		
		public ProgressBarAnimation(ClientUI window, int percent) {
			this.window = window;
			this.percent = percent;
			run();
		}
		
		@Override
		public void run() {
			window.updateProgressBar(percent);
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
	}
}

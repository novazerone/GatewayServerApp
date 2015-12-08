package server;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.SwingUtilities;

import app.Driver;
import app.ProgressBar;

public class DuplicateHandler extends Thread {
	private String name;
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;

	private boolean isDownloading = false;
	private boolean isFinished = false;

	private String fileName;
	private String fileSize;

	public DuplicateHandler(Socket _socket) {
		socket = _socket;
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Server.window.log("Connection established at port " + Server.ListenerPort + "\n", Color.BLUE);
	}

	@Override
	public void run(){
		// Process all messages from the gateway, according to the protocol.
		try{
			while (true) {
				if(isFinished)
					break;

				Server.window.log("Reading input\n", Color.BLUE);
				boolean readBytesSuccess = false;
				boolean readTextSuccess = false;
				if(isDownloading)
					readInputBytes();
				else
					readInputText();

				if(readBytesSuccess && readTextSuccess){
					//window.log("Lost Connection \n", Color.RED);
					break;
				}
			}
		} catch(Exception e){
			//window.log(e.getMessage() + "\n", Color.RED);
			//window.log("Lost connection to server." + "\n", Color.RED);
			close();
		}
	}

	private boolean readInputText() throws IOException{
		Server.window.log("Reading line...\n", Color.BLACK);
		String line = in.readLine();

		Server.window.log(line + "\n", Color.BLACK);
		// No message from the server.
		if(line == null){
			//window.log("Lost connection to server." + "\n", Color.RED);
			//close();
			//break;
			return false;
		}

		if(line.startsWith("REQUEST")){
			String requestContent = line.substring(8);
			String[] contentArray = requestContent.split(",");
			String requestType = contentArray[0];
			String srcServerName = contentArray[3];
			Server.window.log("Server " + srcServerName + " requests " + requestContent + "\n", Color.BLACK);

			if(requestType.equals("UPLOAD")){
				fileName = contentArray[1];
				fileSize = contentArray[2];

				//window.log("Downloading file from server..." + "\n", Color.BLUE);
				isDownloading = true;
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
			File directory = new File(".\\_Servers\\" + Server.ServerName);
			directory.mkdir();
			FileOutputStream fos = new FileOutputStream(directory +"\\" + fileName);
			fos.flush();

			byte[] buffer = new byte[Driver.getServerTransferBlockSize()];
			int n;

			ProgressBar progress = new ProgressBar("Downloading duplicate " + fileName + " from port " + socket.getPort());
			Server.window.getPnlProgressStack().add(progress);
			Server.window.addGap();
			Server.window.validatePanelUpdate();
			// Download the chunks.
			while(byteOffset < Integer.parseInt(fileSize)){
				n = dis.read(buffer);
				fos.write(buffer, 0, n);
				byteOffset += n;

				int percent = (int)(((float) byteOffset / Integer.parseInt(fileSize))*100);
				SwingUtilities.invokeLater(Server.window.new ProgressBarAnimation(progress, percent));
				
				//Server.window.log("Downloading... " + byteOffset + " out of " + fileSize + "\n", Color.BLACK);
			}

			fos.close();

			// Finalize this cache. 

			Server.window.log("File succesfully saved." + "\n", Color.BLUE);
		} catch(Exception e){
			Server.window.log("Error: " + e.getMessage() + "\n", Color.RED);
			Server.window.log("Failed to download file from client." + "\n", Color.RED);
			e.printStackTrace();
			return false;
		}

		isDownloading = false;
		isFinished = true;
		return true;
	}

	public void close(){
		if(socket != null){
			try {
				socket.close();
			} catch (IOException e) {

			}
		}
	}
}
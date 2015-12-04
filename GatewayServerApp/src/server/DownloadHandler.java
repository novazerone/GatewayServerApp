package server;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import app.Driver;

public class DownloadHandler extends Thread {
	private String name;
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;

	private boolean isDownloading = false;
	private boolean isFinished = false;

	private String fileName;
	private String fileSize;

	public DownloadHandler(Socket _socket) {
		socket = _socket;
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Server.window.log("Connection established at port " + Server.DownloadPort + "\n", Color.BLUE);
	}

	@Override
	public void run(){
		// Process all messages from the gateway, according to the protocol.
		try{
			while(true){
				String input = in.readLine();

				if(input == null){
					Server.window.log("Client lost connection. \n", Color.BLACK);
					throw new Exception();
				}

				if(input.startsWith("FILENAME:")){
					fileName = input.substring(9);
					break;
				}
			}

			int byteOffset = 0;
			File file = new File(".\\_Servers\\" + Server.ServerName + "\\" + fileName);
			out.println("PROCEEDTODOWNLOAD:" + 0 + "," + file.length());
			out.flush();

			while (true) {
				DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
				FileInputStream fis = new FileInputStream(file);
				BufferedInputStream bis = new BufferedInputStream(fis);

				int n = -1;
				byte[] buffer = new byte[Driver.getClientTransferBlockSize()];

				// Upload...
				while((n = bis.read(buffer)) > -1){
					dos.write(buffer, 0, n);
					byteOffset += n;

					Server.window.log("Uploading to client... " + byteOffset + " out of " + file.length() + "\n", Color.BLACK);
				};

				Server.window.log("Successfully uploaded file!" + "\n", Color.BLUE);
				break;
			}
		} catch(Exception e){
			Server.window.log(e.getMessage() + "\n", Color.RED);
			Server.window.log("Lost connection to server." + "\n", Color.RED);
			close();
		}
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
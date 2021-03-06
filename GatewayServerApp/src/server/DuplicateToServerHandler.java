package server;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.SwingUtilities;

import app.ByteCache;
import app.Driver;
import app.ProgressBar;
import database.ServerJDBCTemplate;

public class DuplicateToServerHandler{

	private Socket socket;
	private  BufferedReader in;
	private PrintWriter out;

	private ByteCache file;
	private int fileId;

	public String input = "";

	public DuplicateToServerHandler(Socket _socket, ByteCache _byteCache, int _fileId){
		socket = _socket;
		file = _byteCache;
		Server.window.log("Initialized Handler\n", Color.BLACK);
		if(_byteCache == null)
			Server.window.log("Cache is null\n", Color.BLACK);

		fileId = _fileId;
	}

	public void run(){
		if(file == null)
			return;

		try{
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			// Headers.
			out.println("REQUEST:UPLOAD," + file.getFileName() + "," + file.getCurrentSize() + "," + Server.ServerName);

			int byteOffset = 0;
			while(true){
				input = in.readLine();

				if(input == null){
					Server.window.log("Nothing read.\n", Color.BLACK);
					return;
				}

				if(input.startsWith("REQUESTDENIED")){
					Server.window.log("Failed to upload " + file.getFileName() + ". Request denied. \n", Color.BLACK);
					return;
				}

				// The signal to proceed uploading.
				if(input.startsWith("PROCEEDTOUPLOAD:")){
					byteOffset = Integer.parseInt(input.substring(16));
					if(byteOffset != 0)
						Server.window.log("Resuming upload at " + byteOffset + "..." + "\n", Color.BLUE);
					else
						Server.window.log("Beginning upload..." + "\n", Color.BLUE);

					break;
				}
			}

			out.flush();
			DataOutputStream bos = new DataOutputStream(socket.getOutputStream());
			ByteArrayInputStream bais = new ByteArrayInputStream(file.getBytes());
			BufferedInputStream bis = new BufferedInputStream(bais);

			int n = -1;
			byte[] buffer;

			if(bis.available() - byteOffset < Driver.getServerTransferBlockSize())
				buffer = new byte[bis.available() - byteOffset];
			else
				buffer = new byte[Driver.getServerTransferBlockSize()];

			// Resume from last point.
			//bis.skip(byteOffset);

			ProgressBar progress = new ProgressBar("Duplicating " + file.getFileName() + " to port " + socket.getPort());
			Server.window.getPnlProgressStack().add(progress);
			Server.window.addGap();
			Server.window.validatePanelUpdate();
			// Upload...
			while(byteOffset < file.getTargetSize()){
				n = bis.read(buffer);
				bos.write(buffer, 0, n);
				byteOffset += n;
				
				int percent = (int)(((float) byteOffset / file.getTargetSize())*100);
				SwingUtilities.invokeLater(Server.window.new ProgressBarAnimation(progress, percent));
				
				//Server.window.log("Uploading... " + byteOffset + " out of " + file.getTargetSize() + "\n", Color.BLACK);
			};

			Server.window.log("Successfully uploaded file!" + "\n", Color.BLUE);			
			
			ServerJDBCTemplate db = new ServerJDBCTemplate();
			Server.window.log("Attempting to save... FID: " + fileId + " Port: " + db.getServer(socket.getPort()).getId() + "\n", Color.RED);

			db.updateUploadFinish(fileId, db.getServer(socket.getPort()).getId(), 1);
			
			Server.window.log("Saving to database... Socket: " + socket.getPort() + " FID: " + fileId + "\n", Color.BLACK);
			
		} catch(Exception e){
			Server.window.log("Error: " + e.getMessage() + "\n", Color.RED);
			e.printStackTrace();
		}

		// Release references.
		socket = null;
		file = null;
	}
}

package gatewayServer;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.swing.SwingUtilities;

import app.ByteCache;
import app.CacheManager;
import app.Driver;
import app.ProgressBar;
import controllers.UploadController;
import database.FileJDBCTemplate;
import database.ServerJDBCTemplate;
import database.models.File;
import database.models.Server;

public class ClientHandler extends Thread {
	private String name;
	private Socket connection;
	private BufferedReader in;
	private PrintWriter out;
	private FileOutputStream fos;

	public ClientHandler(Socket _socket) {
		connection = _socket;
	}

	@Override
	public void run() {
		try {
			// Initialize streams.
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			out = new PrintWriter(connection.getOutputStream(), true);

			Gateway.log("A client connected." + "\n", Color.BLACK);
			name = UUID.randomUUID().toString();

			// Get mode and file name.
			String mode = null;
			String fileName = null;
			long fileSize = 0;
			while(true){
				String input = in.readLine();

				if(input == null){
					Gateway.log("Client lost connection. \n", Color.BLACK);
					throw new Exception();
				}

				if(input.startsWith("MODE:")){
					mode = input.substring(5);
				} else if(input.startsWith("FILENAME:")){
					fileName = input.substring(9);
				} else if(input.startsWith("FILESIZE:")){
					fileSize = Long.parseLong(input.substring(9));
				}

				if(mode != null && fileName != null && fileSize != 0)
					break;
			}

			CacheManager cm = Gateway.getInstance().getCacheManager();
			int byteOffset = 0;
			if(mode.equals("UPLOAD")){
				if(cm.contains(fileName)){
					if(!cm.getByteCache(fileName).getIsFinal()){
						byteOffset = cm.getByteCache(fileName).getCurrentSize();
						out.println("PROCEEDTOUPLOAD:" + byteOffset);

						Gateway.log("Resuming download at " + byteOffset + "..." + "\n", Color.BLUE);

					} else{
						out.println("PROCEEDTOUPLOAD:0"); // Overwrite file.
						Gateway.log("Beginning download..." + "\n", Color.BLUE);
					}

				} else{
					out.println("PROCEEDTOUPLOAD:0");
					Gateway.log("Beginning download..." + "\n", Color.BLUE);
				}

				out.flush();
				BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
				
				while(true){
					try{
						fos = new FileOutputStream(".\\_Gateway\\"+fileName, byteOffset > 0);

						byte[] buffer = new byte[Driver.getClientTransferBlockSize()];
						int n;

						ByteCache cache = null;
						if(byteOffset > 0){	// If the download was resumed.
							cache = cm.getByteCache(fileName);
						} else{
							cache = new ByteCache(fileName, (int)fileSize);
							cm.add(cache);
						}
						
						ProgressBar progress = new ProgressBar("Client uploading '"+fileName+"'.");
						Gateway.getInstance().getWindow().getPnlProgressStack().add(progress);
						Gateway.getInstance().getWindow().addGap();
						Gateway.getInstance().getWindow().validatePanelUpdate();
						
						// Download the chunks.
						while((n = bis.read(buffer)) > 0){
							fos.write(buffer, 0, n);
							byteOffset += n;
							cache.write(buffer, 0, n);

							int percent = (int)(((float) byteOffset / fileSize)*100);
							SwingUtilities.invokeLater(Gateway.getInstance().getWindow().new ProgressBarAnimation(progress, percent));
						}

						fos.close();

						// Finalize this cache. 
						cache.setIsFinal(true);

						Gateway.log("File succesfully saved." + "\n", Color.BLACK);

						// Save to database.
						UploadController uploadController = new UploadController();
						database.models.Server_File dbFile = uploadController.uploadFile(fileName, (int)fileSize);

						// Distribute
						Gateway.getInstance().distribute(cache, dbFile.getDestinationServers(), dbFile.getFile_id());
					} catch(Exception e){
						Gateway.log("Error: " + e.getMessage() + "\n", Color.RED);
						Gateway.log("Failed to download file from client." + "\n", Color.RED);
						e.printStackTrace();
					}
					break;
				}
			} else{
				// Supply the port to the server.
				Gateway.log("Download requested. Returning port... \n", Color.BLUE);
				FileJDBCTemplate dbFile = new FileJDBCTemplate();
				File f = dbFile.getFile(fileName);
								
				ServerJDBCTemplate dbServer = new ServerJDBCTemplate();
				List<Server> servers = dbServer.getServerWithFiles(f.getId());
				System.out.println("# of SERVERS with FID:"+f.getId()+" >> "+servers.size());
				for (Server s : servers) {
					System.out.println(" >> "+s.getDownloadPort());
				}
				Random r = new Random();
				int index = r.nextInt(servers.size());
				int randomDownloadPort = servers.get(index).getDownloadPort();
				System.out.println("DL PORT: "+randomDownloadPort);
				out.println("PROCEEDTOPORT:" + randomDownloadPort);
				out.flush();
			}

		} catch (IOException e) {
			System.out.println(e);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}

	public void close(){
		try {
			connection.close();
			if(out != null)
				out.println("CLOSE");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

		Gateway.getInstance().getClientListener().removeClient(this);
		Gateway.log("Client " + name + " closed connection." + "\n", Color.BLACK);
	}

	public void setClientName(String _name){
		name = _name;
	}
}
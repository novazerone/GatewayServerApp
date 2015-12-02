package app;

import controllers.UploadController;
import database.ServerJDBCTemplate;
import database.models.Server;
import database.models.Server_File;
import java.util.List;

public class Driver2 {


	public static void main(String[] args) {

		UploadController uploadController = new UploadController();
		//Server_File server_file = uploadController.uploadFile("file_new12334", 123);
		//System.out.print(server.toArray().toString());

		ServerJDBCTemplate server = new ServerJDBCTemplate();
		List<Server_File> server_files = server.checkFile(7);

		for (Server_File server_file : server_files){

			System.out.println(server_file.getFile_id());
			server_file.getServers();
			for (Server serverF : server_file.getServers()){
				serverF.getName();
				System.out.println(serverF.getId() + " " + serverF.getName() + " " + serverF.getPort() + " ");
			}

		}
	}
	
}

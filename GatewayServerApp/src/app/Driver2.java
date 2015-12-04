package app;

import controllers.UploadController;
import database.ServerJDBCTemplate;
import database.models.Server_File;

public class Driver2 {

	@SuppressWarnings("unused")
	public static void main(String[] args) {

		UploadController uploadController = new UploadController();
		Server_File server_file = uploadController.uploadFile("file_new12334", 12333);
		System.out.print(server_file.getFile_id());
		System.out.print(server_file.getDestinationServers().toArray().toString());

		ServerJDBCTemplate server = new ServerJDBCTemplate();
//
//		List<Server_File> server_files = server.checkFile(7);
//
//		for (Server_File server_file : server_files){
//
//			System.out.println(server_file.getFile_id());
//			server_file.getDestinationServers();
//			for (Server serverF : server_file.getDestinationServers()){
//				serverF.getName();
//				System.out.println(serverF.getId() + " " + serverF.getName() + " " + serverF.getPort() + " ");
//			}
//
//		}
	}
	
}

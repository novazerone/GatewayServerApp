package app;

import controllers.UploadController;
import database.models.Server;
import database.models.Server_File;

import java.util.List;

public class Driver2 {


	public static void main(String[] args) {

		UploadController uploadController = new UploadController();
		Server_File server_file = uploadController.uploadFile("file_new12334", 123);
		//System.out.print(server.toArray().toString());
	}
	
}

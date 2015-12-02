package app;

import controllers.UploadController;
import database.models.Server;

import java.util.List;

public class Driver2 {


	public static void main(String[] args) {

		UploadController uploadController = new UploadController();
		List<Server> server = uploadController.uploadFile("file_new12334", 123);
		System.out.print(server.toArray().toString());
	}
	
}

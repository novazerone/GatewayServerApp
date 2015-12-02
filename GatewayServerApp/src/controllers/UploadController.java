package controllers;

import database.FileJDBCTemplate;
import database.ServerJDBCTemplate;
import database.models.Server;
import database.models.Server_File;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 12/2/2015.
 */
public class UploadController {

    public UploadController() {
    }

    public Server_File uploadFile(String file_name, Integer file_size){

        FileJDBCTemplate fileDB = new FileJDBCTemplate();
        ServerJDBCTemplate serverDB = new ServerJDBCTemplate();
        List<Server> servers = new ArrayList<Server>();

        int file_id = fileDB.create(file_name, file_size, 0);

        if(file_id != 0){
           servers = serverDB.listAvailableServers(file_id);
        }
        Server_File server_file = new Server_File();
        server_file.setFile_id(file_id);
        server_file.setDestinationServers(servers);


        return server_file;
    }

}

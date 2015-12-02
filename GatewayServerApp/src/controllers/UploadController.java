package controllers;

import database.FileJDBCTemplate;
import database.ServerJDBCTemplate;
import database.models.Server;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 12/2/2015.
 */
public class UploadController {

    public UploadController() {
    }

    public List<Server> uploadFile(String file_name, Integer file_size){

        FileJDBCTemplate fileDB = new FileJDBCTemplate();
        ServerJDBCTemplate serverDB = new ServerJDBCTemplate();
        List<Server> server = new ArrayList<Server>();

        int file_id = fileDB.create(file_name, file_size, 0);

        if(file_id != 0){
           server = serverDB.listAvailableServers(file_id);
        }



        return server;
    }

}

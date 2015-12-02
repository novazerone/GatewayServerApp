package database.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 11/30/2015.
 */
public class Server_File {

    private Integer file_id;
    private List<Server> servers = new ArrayList<Server>();

    /**
     * Sets new servers.
     *
     * @param servers New value of servers.
     */
    public void setServers(List<Server> servers) {
        this.servers = servers;
    }

    /**
     * Sets new file_id.
     *
     * @param file_id New value of file_id.
     */
    public void setFile_id(Integer file_id) {
        this.file_id = file_id;
    }

    /**
     * Gets servers.
     *
     * @return Value of servers.
     */
    public List<Server> getServers() {
        return servers;
    }

    /**
     * Gets file_id.
     *
     * @return Value of file_id.
     */
    public Integer getFile_id() {
        return file_id;
    }
}

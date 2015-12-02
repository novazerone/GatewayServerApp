package database.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 11/30/2015.
 */
public class Server_File {

    private Integer file_id;
    private List<Server> destinationServers = new ArrayList<Server>();
    private List<Server> sourceServers = new ArrayList<Server>();

    /**
     * Sets new sourceServers.
     *
     * @param sourceServers New value of sourceServers.
     */
    public void setSourceServers(List<Server> sourceServers) {
        this.sourceServers = sourceServers;
    }

    /**
     * Gets destinationServers.
     *
     * @return Value of destinationServers.
     */
    public List<Server> getDestinationServers() {
        return destinationServers;
    }

    /**
     * Gets file_id.
     *
     * @return Value of file_id.
     */
    public Integer getFile_id() {
        return file_id;
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
     * Sets new destinationServers.
     *
     * @param destinationServers New value of destinationServers.
     */
    public void setDestinationServers(List<Server> destinationServers) {
        this.destinationServers = destinationServers;
    }

    /**
     * Gets sourceServers.
     *
     * @return Value of sourceServers.
     */
    public List<Server> getSourceServers() {
        return sourceServers;
    }
}

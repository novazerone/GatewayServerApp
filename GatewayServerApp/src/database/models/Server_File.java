package database.models;

/**
 * Created by user on 11/30/2015.
 */
public class Server_File {

    private Integer id;
    private Integer file_id;
    private Integer server_id;

    /**
     * Sets new server_id.
     *
     * @param server_id New value of server_id.
     */
    public void setServer_id(Integer server_id) {
        this.server_id = server_id;
    }

    /**
     * Gets id.
     *
     * @return Value of id.
     */
    public Integer getId() {
        return id;
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
     * Gets file_id.
     *
     * @return Value of file_id.
     */
    public Integer getFile_id() {
        return file_id;
    }

    /**
     * Sets new id.
     *
     * @param id New value of id.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets server_id.
     *
     * @return Value of server_id.
     */
    public Integer getServer_id() {
        return server_id;
    }
}

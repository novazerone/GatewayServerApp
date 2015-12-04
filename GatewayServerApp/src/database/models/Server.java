package database.models;

/**
 * Created by user on 11/30/2015.
 */
public class Server {

    private Integer id;
    private String name;
    private boolean status;
    private Integer total_fize_size;
    private Integer uploadPort;
    private Integer downloadPort;

    /**
     * Sets new name.
     *
     * @param name New value of name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets new status.
     *
     * @param status New value of status.
     */
    public void setStatus(boolean status) {
        this.status = status;
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
     * Gets name.
     *
     * @return Value of name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets status.
     *
     * @return Value of status.
     */
    public boolean isStatus() {
        return status;
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
     * Sets new total_fize_size.
     *
     * @param total_fize_size New value of total_fize_size.
     */
    public void setTotal_fize_size(Integer total_fize_size) {
        this.total_fize_size = total_fize_size;
    }

    /**
     * Gets total_fize_size.
     *
     * @return Value of total_fize_size.
     */
    public Integer getTotal_fize_size() {
        return total_fize_size;
    }



    /**
     * Gets uploadPort.
     *
     * @return Value of uploadPort.
     */
    public Integer getUploadPort() {
        return uploadPort;
    }

    /**
     * Gets downloadPort.
     *
     * @return Value of downloadPort.
     */
    public Integer getDownloadPort() {
        return downloadPort;
    }

    /**
     * Sets new uploadPort.
     *
     * @param uploadPort New value of uploadPort.
     */
    public void setUploadPort(Integer uploadPort) {
        this.uploadPort = uploadPort;
    }

    /**
     * Sets new downloadPort.
     *
     * @param downloadPort New value of downloadPort.
     */
    public void setDownloadPort(Integer downloadPort) {
        this.downloadPort = downloadPort;
    }
}

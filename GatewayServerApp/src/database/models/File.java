package database.models;

/**
 * Created by user on 11/30/2015.
 */
public class File{

    private Integer id;
    private String file_name;
    private Integer file_size;
    private Integer status;

    /**
     * Gets file_size.
     *
     * @return Value of file_size.
     */
    public Integer getFile_size() {
        return file_size;
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
     * Sets new file_name.
     *
     * @param file_name New value of file_name.
     */
    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    /**
     * Gets file_name.
     *
     * @return Value of file_name.
     */
    public String getFile_name() {
        return file_name;
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
     * Sets new file_size.
     *
     * @param file_size New value of file_size.
     */
    public void setFile_size(Integer file_size) {
        this.file_size = file_size;
    }

    /**
     * Gets status.
     *
     * @return Value of status.
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * Sets new status.
     *
     * @param status New value of status.
     */
    public void setStatus(Integer status) {
        this.status = status;
    }
}

package database.models;

/**
 * Created by user on 11/30/2015.
 */
public class Server {

    private Integer id;
    private String name;
    private boolean status;

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
}

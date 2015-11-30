package database.daos;

import database.models.Server;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by user on 11/30/2015.
 */
public interface ServerDAO {

    /**
     * This is the method to be used to initialize
     * database resources ie. connection.
     */
    public void setDataSource(DataSource ds);
    /**
     * This is the method to be used to create
     * a record in the Student table.
     */
    public void create(String name, Boolean status);
    /**
     * This is the method to be used to list down
     * a record from the Student table corresponding
     * to a passed student id.
     */
    public Server getServer(Integer id);
    /**
     * This is the method to be used to list down
     * all the records from the Student table.
     */
    public List<Server> listServers();
    /**
     * This is the method to be used to delete
     * a record from the Student table corresponding
     * to a passed student id.
     */
    public void delete(Integer id);
    /**
     * This is the method to be used to update
     * a record into the Student table.
     */
    public void update(Integer id, Boolean status);

}

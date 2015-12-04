package database.daos;

import java.util.List;

import database.models.Server;
import database.models.Server_File;

/**
 * Created by user on 11/30/2015.
 */
public interface ServerDAO {

	/**
	 * This is the method to be used to initialize
	 * database resources ie. connection.
	 */
	// public void setDataSource(DataSource ds);
	/**
	 * This is the method to be used to create
	 * a record in the Student table.
	 */
	public int create(String name, Integer uploadPort, Integer downloadPort);
	/**
	 * This is the method to be used to list down
	 * a record from the Student table corresponding
	 * to a passed student id.
	 */
	public Server getServer(String server_name);

	public Server getServer(Integer port);
	/**
	 * This is the method to be used to list down
	 * all the records from the Student table.
	 */
	public List<Server> listServers();

	public List<Server> getServerWithFiles(Integer file_id);

	public List<Server> listAvailableServers(Integer file_id);


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

	public void downAllServers();

	public void updateUploadFinish(Integer file_id, Integer server_id, Integer status);

	public List<Server_File> checkFile(Integer port);

}

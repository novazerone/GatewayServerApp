package database.daos;

import java.util.List;

import database.models.File;

/**
 * Created by user on 11/30/2015.
 */
public interface FileDAO {

	/**
	 * This is the method to be used to initialize
	 * database resources ie. connection.
	 */
//    public void setDataSource(DataSource ds);
	/**
	 * This is the method to be used to create
	 * a record in the Student table.
	 */
	public int create(String file_name, Integer file_size, Integer status);
	/**
	 * This is the method to be used to list down
	 * a record from the Student table corresponding
	 * to a passed student id.
	 */
	public File getFile(String file_name);
	
	public File getFile(Integer file_id);
	/**
	 * This is the method to be used to list down
	 * all the records from the Student table.
	 */
	public List<File> listFiles();


	public List<File> listServerFiles(Integer server_id);

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

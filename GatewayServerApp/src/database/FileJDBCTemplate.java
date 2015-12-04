package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import database.daos.FileDAO;
import database.daos.FileMapper;
import database.models.File;
import database.models.Server;

/**
 * Created by user on 11/30/2015.
 */
public class FileJDBCTemplate implements FileDAO {

	private PreparedStatement preparedStatement = null;
	private PreparedStatement preparedStatement2 = null;
	private PreparedStatement preparedStatement3 = null;
	private PreparedStatement preparedStatement4 = null;
	private Connection connection;


//	connection = ConnectionFactory.getConnection();
//	try {
//		statement = connection.createStatement();
//		//R = statement.executeQuery("SELECT * FROM files");
//
//
//
//		// Result set get the result of the SQL query
//		R = statement
//				.executeQuery("select * from files");
//		while(R.next()){
//			System.out.println(R.getString("file_name"));
//		}
//
//	} catch (SQLException e) {
//		e.printStackTrace();
//	}

	@Override
	public int create(String file_name, Integer file_size, Integer status) {

		File fileOLD = getFile(file_name);

		String query = "insert into files (file_name, file_size, status) values (?, ?, ?) ON DUPLICATE KEY UPDATE file_size = ?,"
				+ "id = LAST_INSERT_ID(id)";

		connection = ConnectionFactory.getConnection();
		ServerJDBCTemplate serverDB = new ServerJDBCTemplate();
		List<Server> servers = new ArrayList<Server>();

		int last_inserted_id = 0;
		try {
			preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setString(1, file_name);
			preparedStatement.setInt(2, file_size);
			preparedStatement.setInt(3, status);
			preparedStatement.setInt(4, file_size);
			int i = preparedStatement.executeUpdate();
			System.out.println(i);
			String query2 = "SELECT LAST_INSERT_ID() AS n";
			preparedStatement2 = connection.prepareStatement(query2);
			ResultSet rs = preparedStatement2.executeQuery();
			rs.next();
			last_inserted_id = rs.getInt(1);
			System.out.println(last_inserted_id);

			//update
			if(i == 2){
				servers = serverDB.getServerWithFiles(last_inserted_id);
				String whereUpdateServer = "";
				for (Server server: servers){
					whereUpdateServer = whereUpdateServer +  " id = " + server.getId() + " AND";
				}
				whereUpdateServer = whereUpdateServer.substring(0, whereUpdateServer.length()-3);

				String query3 = "update servers set total_file_size = total_file_size - ? where " + whereUpdateServer;
				preparedStatement3 = connection.prepareStatement(query3);
				preparedStatement3.setInt(1, fileOLD.getFile_size());
				preparedStatement3.executeUpdate();


				String query4 = "UPDATE server_file set status = 3 WHERE file_id = ?";
				preparedStatement4 = connection.prepareStatement(query4);
				preparedStatement4.setInt(1, last_inserted_id);
				preparedStatement4.executeUpdate();

			}


			//            ResultSet rs = preparedStatement.getGeneratedKeys();
			//
			//            if(rs.next()){
			//                 last_inserted_id = rs.getInt(1);
			//            }


			SQLWarning warning = preparedStatement.getWarnings();
			if(warning != null){
				throw new SQLException(warning.getMessage());
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(preparedStatement);
			DbUtil.close(preparedStatement2);
			DbUtil.close(connection);
		}

		return last_inserted_id;

	}

	@Override
	public File getFile(String file_name) {
		String query = "SELECT * from files WHERE file_name = ?";
		ResultSet rs;
		File file = null;
		Connection connection = ConnectionFactory.getConnection();

		try {

			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1,file_name);
			rs = preparedStatement.executeQuery();
			int i = 0;
			while (rs.next()) {
				System.out.println(rs.toString());
				FileMapper fileMapper = new FileMapper();
				file = fileMapper.mapRow(rs, i);
				i++;
			}



			SQLWarning warning = preparedStatement.getWarnings();

			if (warning != null) {
				throw new SQLException(warning.getMessage());
			}


		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(preparedStatement);
			DbUtil.close(connection);
		}

		return file;

	}

	@Override
	public List<File> listFiles() {
		String query = "SELECT * from files";
		ResultSet rs;
		List<File> files = new ArrayList<File>();

		Connection connection = ConnectionFactory.getConnection();

		try {

			preparedStatement = connection.prepareStatement(query);
			rs = preparedStatement.executeQuery();
			int i = 0;
			while (rs.next()) {
				System.out.println(rs.toString());
				FileMapper fileMapper = new FileMapper();
				File file = fileMapper.mapRow(rs, i);
				i++;
				files.add(file);
			}

			SQLWarning warning = preparedStatement.getWarnings();

			if (warning != null) {
				throw new SQLException(warning.getMessage());
			}


		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(preparedStatement);
			DbUtil.close(connection);
		}

		return files;
	}

	@Override
	public List<File> listServerFiles(Integer server_id) {
		String query = "SELECT file_id as id, file_name, file_size, files.status from server_file JOIN files " +
				"ON server_file.file_id = files.id WHERE server_id = ? AND server_file.status = 1";
		ResultSet rs;
		List<File> files = new ArrayList<File>();

		Connection connection = ConnectionFactory.getConnection();

		try {

			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1,server_id);
			rs = preparedStatement.executeQuery();
			int i = 0;
			while (rs.next()) {
				System.out.println(rs.toString());
				FileMapper fileMapper = new FileMapper();
				File file = fileMapper.mapRow(rs, i);
				i++;
				files.add(file);
			}

			SQLWarning warning = preparedStatement.getWarnings();

			if (warning != null) {
				throw new SQLException(warning.getMessage());
			}


		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(preparedStatement);
			DbUtil.close(connection);
		}

		return files;
	}

	@Override
	public void delete(Integer id) {

	}

	@Override
	public void update(Integer id, Boolean status) {

	}


	//        private DataSource dataSource;
	//        private JdbcTemplate jdbcTemplateObject;

	//        public void setDataSource(DataSource dataSource) {
	//            this.dataSource = dataSource;
	//            this.jdbcTemplateObject = new JdbcTemplate(dataSource);
	//
	//        }

	//        public void create(String name, Integer age) {
	//            String SQL = "insert into Student (name, age) values (?, ?)";
	//
	//            jdbcTemplateObject.update( SQL, name, age);
	//            System.out.println("Created Record Name = " + name + " Age = " + age);
	//            return;
	//        }
	//
	//        public Student getStudent(Integer id) {
	//            String SQL = "select * from Student where id = ?";
	//            Student student = jdbcTemplateObject.queryForObject(SQL,
	//                    new Object[]{id}, new StudentMapper());
	//            return student;
	//        }
	//
	//        public List<Student> listStudents() {
	//            String SQL = "select * from Student";
	//            List <Student> students = jdbcTemplateObject.query(SQL,
	//                    new StudentMapper());
	//            return students;
	//        }
	//
	//        public void delete(Integer id){
	//            String SQL = "delete from Student where id = ?";
	//            jdbcTemplateObject.update(SQL, id);
	//            System.out.println("Deleted Record with ID = " + id );
	//            return;
	//        }
	//
	//        public void update(Integer id, Integer age){
	//            String SQL = "update Student set age = ? where id = ?";
	//            jdbcTemplateObject.update(SQL, age, id);
	//            System.out.println("Updated Record with ID = " + id );
	//            return;
	//        }



}

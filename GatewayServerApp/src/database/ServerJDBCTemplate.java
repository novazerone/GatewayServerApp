package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import database.daos.ServerDAO;
import database.daos.ServerMapper;
import database.models.Server;
import database.models.Server_File;

/**
 * Created by user on 12/2/2015.
 */
public class ServerJDBCTemplate implements ServerDAO {

	private PreparedStatement preparedStatement = null;
	private PreparedStatement preparedStatement2 = null;
	private PreparedStatement preparedStatement3 = null;
	private PreparedStatement preparedStatement4 = null;
	private PreparedStatement preparedStatement5 = null;
	private Connection connection;


	@Override
	public int create(String server_name, Integer uploadPort, Integer downloadPort) {

		String query = "insert into servers (name, status, total_file_size, port, download_port) values (?, ?, ?, ?, ?)";

		connection = ConnectionFactory.getConnection();
		int last_inserted_id = 0;
		try {
			preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setString(1, server_name);
			preparedStatement.setBoolean(2, true);
			preparedStatement.setInt(3, 0);
			preparedStatement.setInt(4, uploadPort);
			preparedStatement.setInt(5, downloadPort);
			preparedStatement.execute();

			ResultSet rs = preparedStatement.getGeneratedKeys();

			if (rs.next()) {
				last_inserted_id = rs.getInt(1);
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

		return last_inserted_id;

	}

	@Override
	public Server getServer(String server_name) {
		String query = "SELECT * FROM servers WHERE name = ?";
		ResultSet rs;
		Server server = null;

		Connection connection = ConnectionFactory.getConnection();

		try {

			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, server_name);
			rs = preparedStatement.executeQuery();
			int i = 0;
			while (rs.next()) {
				System.out.println(rs.toString());
				ServerMapper serverMapper = new ServerMapper();
				server = serverMapper.mapRow(rs, i);
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

		return server;
	}

	@Override
	public Server getServer(Integer port) {

		String query = "SELECT * FROM servers WHERE port = ?";
		ResultSet rs;
		Server server = null;

		Connection connection = ConnectionFactory.getConnection();

		try {

			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, port);
			rs = preparedStatement.executeQuery();
			int i = 0;
			while (rs.next()) {
				System.out.println(rs.toString());
				ServerMapper serverMapper = new ServerMapper();
				server = serverMapper.mapRow(rs, i);
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

		return server;
	}

	@Override
	public List<Server> listServers() {
		String query = "SELECT * from servers";
		ResultSet rs;
		List<Server> servers = new ArrayList<Server>();

		Connection connection = ConnectionFactory.getConnection();

		try {

			preparedStatement = connection.prepareStatement(query);
			rs = preparedStatement.executeQuery();
			int i = 0;
			while (rs.next()) {
				System.out.println(rs.toString());
				ServerMapper serverMapper = new ServerMapper();
				Server server = serverMapper.mapRow(rs, i);
				i++;
				servers.add(server);
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

		return servers;
	}

	@Override
	public List<Server> getServerWithFiles(Integer file_id) {
		String query = "SELECT * FROM server_file JOIN servers ON server_file.server_id = servers.id WHERE file_id = ? AND server_file.status = 1";
		ResultSet rs;
		List<Server> servers = new ArrayList<Server>();

		Connection connection = ConnectionFactory.getConnection();

		try {

			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, file_id);
			rs = preparedStatement.executeQuery();
			int i = 0;
			while (rs.next()) {
				System.out.println(rs.toString());
				ServerMapper serverMapper = new ServerMapper();
				Server server = serverMapper.mapRow(rs, i);
				i++;
				servers.add(server);
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

		return servers;
	}

	@Override
	public List<Server> listAvailableServers(Integer file_id) {

		String query = "select floor(count(*) * 2/3) AS server_available from servers";
		ResultSet rs;
		Integer available_server = 0;
		List<Server> servers = new ArrayList<Server>();
		Connection connection = ConnectionFactory.getConnection();

		try {
			preparedStatement = connection.prepareStatement(query);
			rs = preparedStatement.executeQuery();
			while (rs.next()) {
				available_server = rs.getInt("server_available");
			}
			SQLWarning warning = preparedStatement.getWarnings();

			if (warning != null) {
				throw new SQLException(warning.getMessage());
			}

			String query2 = "SELECT * from servers WHERE status = 1 ORDER BY total_file_size asc LIMIT " + available_server;
			System.out.println(query2);
			preparedStatement2 = connection.prepareStatement(query2);
			rs = preparedStatement2.executeQuery();

			int i = 0;
			while (rs.next()) {
				System.out.println(rs.toString());
				ServerMapper serverMapper = new ServerMapper();
				Server server = serverMapper.mapRow(rs, i);
				i++;
				servers.add(server);
				String query3 = "insert into server_file (file_id, server_id, status) values (?, ?, 0)  ON DUPLICATE KEY UPDATE status = 3";
				preparedStatement3 = connection.prepareStatement(query3);
				preparedStatement3.setInt(1, file_id);
				preparedStatement3.setInt(2, server.getId());
				preparedStatement3.executeUpdate();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(preparedStatement);
			DbUtil.close(preparedStatement2);
			DbUtil.close(preparedStatement3);
			DbUtil.close(connection);
		}

		return servers;

	}

	@Override
	public void delete(Integer id) {

	}

	@Override
	public void update(Integer id, Boolean status) {

		String query = "update servers set status = ? where id = ?";
		Connection connection = ConnectionFactory.getConnection();

		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setBoolean(1, status);
			preparedStatement.setInt(2, id);
			preparedStatement.execute();

			String query2 = "UPDATE server_file set status = ? WHERE server_id = ?";
			preparedStatement2 = connection.prepareStatement(query2);
			if (!status) {
				preparedStatement2.setInt(1, 2);
				preparedStatement2.setInt(2, id);
			} else {
				preparedStatement2.setInt(1, 1);
				preparedStatement2.setInt(2, id);
			}

			preparedStatement2.execute();

			SQLWarning warning = preparedStatement.getWarnings();

			if (warning != null) {
				throw new SQLException(warning.getMessage());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(preparedStatement);
			DbUtil.close(preparedStatement2);
			DbUtil.close(connection);
		}

	}

	@Override
	public void downAllServers() {

		String query = "update servers set status = 0";
		Connection connection = ConnectionFactory.getConnection();

		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.execute();
			SQLWarning warning = preparedStatement.getWarnings();

			if (warning != null) {
				throw new SQLException(warning.getMessage());
			}

			query = "update server_file set status = 2";

			try {
				preparedStatement2 = connection.prepareStatement(query);
				preparedStatement2.execute();

				warning = preparedStatement2.getWarnings();

				if (warning != null) {
					throw new SQLException(warning.getMessage());
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(preparedStatement);
			DbUtil.close(preparedStatement2);
			DbUtil.close(connection);
		}

	}

	@Override
	public void updateUploadFinish(Integer file_id, Integer server_id, Integer status) {

		String query = "update server_file set status = ? where file_id = ? AND server_id = ?";
		Connection connection = ConnectionFactory.getConnection();

		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, status);
			preparedStatement.setInt(2, file_id);
			preparedStatement.setInt(3, server_id);
			preparedStatement.execute();

			String query2 = "update servers set total_file_size = total_file_size + "
					+ "(SELECT file_size from files where id = ?) where id = ?";
			try {
				preparedStatement2 = connection.prepareStatement(query2);
				preparedStatement2.setInt(1, file_id);
				preparedStatement2.setInt(2, server_id);
				preparedStatement2.execute();

				SQLWarning warning = preparedStatement.getWarnings();

				if (warning != null) {
					throw new SQLException(warning.getMessage());
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			SQLWarning warning = preparedStatement.getWarnings();

			if (warning != null) {
				throw new SQLException(warning.getMessage());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(preparedStatement);
			DbUtil.close(preparedStatement2);
			DbUtil.close(connection);
		}

	}

	public List<Server_File> checkFile(Integer port) {

		String query = "select floor(count(*) * 2/3) AS server_available from servers";
		ResultSet rs;
		Integer available_server = 0;
		List<Server> servers = new ArrayList<Server>();
		List<Server> servers2 = new ArrayList<Server>();
		List<Server_File> server_files = new ArrayList<Server_File>();
		Connection connection = ConnectionFactory.getConnection();

		try {
			preparedStatement = connection.prepareStatement(query);
			rs = preparedStatement.executeQuery();
			while (rs.next()) {
				available_server = rs.getInt("server_available");
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

		Connection connection2 = ConnectionFactory.getConnection();
		String query2 = "SELECT servers.id, servers.name, servers.status AS server_status, server_file.file_id, "
				+ "server_file.status AS file_status FROM servers JOIN server_file ON servers.id = server_file.server_id "
				+ "WHERE servers.port = ?";

		ResultSet rsFiles;
		try {
			preparedStatement2 = connection2.prepareStatement(query2);
			preparedStatement2.setInt(1, port);
			rsFiles = preparedStatement2.executeQuery();

			while (rsFiles.next()) {
				int file_id = rsFiles.getInt("file_id");

				String query3 = "SELECT count(*) as server_available FROM server_file WHERE file_id = ? AND status = true";
				preparedStatement3 = connection2.prepareStatement(query3);
				preparedStatement3.setInt(1, file_id);
				rs = preparedStatement3.executeQuery();
				int server_with_file = 0;
				int i = 0;
				while (rs.next()) {
					server_with_file = rs.getInt("server_available");

					String where = "";
					if (available_server > server_with_file) {

						int neededServer = available_server - server_with_file;

						String query4 = "SELECT * FROM servers LEFT JOIN server_file ON server_file.server_id = servers.id"
								+ " WHERE file_id = ? AND server_file.status = 1 AND servers.status = 1";

						preparedStatement4 = connection2.prepareStatement(query4);
						preparedStatement4.setInt(1, file_id);
						ResultSet rsWithFile = preparedStatement4.executeQuery();
						int k = 0;
						while (rsWithFile.next()) {
							where = where + " id <> " + rsWithFile.getInt("server_id") + " AND";
							ServerMapper serverMapper = new ServerMapper();
							Server server = serverMapper.mapRow(rsWithFile, k);
							servers.add(server);
							k++;

						}
						Server_File server_file = new Server_File();
						server_file.setSourceServers(servers);

						where = where.substring(0, where.length() - 3);

						String query5 = "SELECT * FROM servers WHERE " + where + " AND status = 1 LIMIT "
								+ neededServer;
						System.out.println(query5);
						preparedStatement5 = connection2.prepareStatement(query5);
						ResultSet rsServers = preparedStatement5.executeQuery();

						int j = 0;
						while (rsServers.next()) {
							System.out.println(rs.toString());
							ServerMapper serverMapper = new ServerMapper();
							Server server2 = serverMapper.mapRow(rsServers, j);
							servers2.add(server2);
							j++;
						}

						server_file.setDestinationServers(servers2);
						server_file.setFile_id(file_id);

						servers = new ArrayList<Server>();
						servers2 = new ArrayList<Server>();
						server_files.add(server_file);

					}

				}

			}

			SQLWarning warning = preparedStatement2.getWarnings();

			if (warning != null) {
				throw new SQLException(warning.getMessage());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(connection);
			DbUtil.close(connection2);
			DbUtil.close(preparedStatement2);
			DbUtil.close(preparedStatement3);
			DbUtil.close(preparedStatement4);
			DbUtil.close(preparedStatement5);
		}

		return server_files;

	}

}

package database.daos;

import java.sql.ResultSet;
import java.sql.SQLException;

import database.models.Server;

/**
 * Created by user on 12/2/2015.
 */
public class ServerMapper {

	public Server mapRow(ResultSet rs, Integer rowNum) throws SQLException {
		Server server = new Server();
		server.setId(rs.getInt("id"));
		server.setName(rs.getString("name"));
		server.setTotal_fize_size(rs.getInt("total_file_size"));
		server.setStatus(rs.getBoolean("status"));
		server.setUploadPort(rs.getInt("port"));
		server.setDownloadPort(rs.getInt("download_port"));

		return server;
	}


}

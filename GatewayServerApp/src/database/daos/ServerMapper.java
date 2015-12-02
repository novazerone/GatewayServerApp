package database.daos;

import database.models.File;
import database.models.Server;

import java.sql.ResultSet;
import java.sql.SQLException;

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
        server.setPort(rs.getInt("port"));

        return server;
    }


}

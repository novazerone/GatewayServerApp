package database;

import database.daos.ServerDAO;
import database.daos.ServerMapper;
import database.models.Server;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 12/2/2015.
 */
public class ServerJDBCTemplate implements ServerDAO{

    private PreparedStatement preparedStatement = null;
    private Connection connection;

    @Override
    public int create(String server_name, Integer port) {

        String query = "insert into servers (name, status, total_file_size, port) values (?, ?, ?, ?)";

        connection = ConnectionFactory.getConnection();
        int last_inserted_id = 0;
        try {
            preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, server_name);
            preparedStatement.setBoolean(2, true);
            preparedStatement.setInt(3, 0);
            preparedStatement.setInt(4, port);
            preparedStatement.execute();

            ResultSet rs = preparedStatement.getGeneratedKeys();

            if(rs.next()){
                last_inserted_id = rs.getInt(1);
            }


            SQLWarning warning = preparedStatement.getWarnings();
            if(warning != null){
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
    public Server getServer(Integer id) {
        return null;
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
            while(rs.next()){
                System.out.println(rs.toString());
                ServerMapper serverMapper = new ServerMapper();
                Server server = serverMapper.mapRow(rs, i);
            }
            SQLWarning warning = preparedStatement.getWarnings();

            if(warning != null){
                throw new SQLException(warning.getMessage());
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
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
            while(rs.next()){
            	available_server = rs.getInt("server_available");
            }
            SQLWarning warning = preparedStatement.getWarnings();

            if(warning != null){
                throw new SQLException(warning.getMessage());
            }

            String query2 = "SELECT * from servers ORDER BY total_file_size asc LIMIT " + available_server;
            System.out.println(query2);
            preparedStatement = connection.prepareStatement(query2);
            rs = preparedStatement.executeQuery();
            int i = 0;
            while(rs.next()){
            	System.out.println(rs.toString());
                ServerMapper serverMapper = new ServerMapper();
                Server server = serverMapper.mapRow(rs, i);
                i++;
                String query3 = "insert into server_file (file_id, server_id, status) values (?, ?, 0)";
                preparedStatement = connection.prepareStatement(query3);
                preparedStatement.setInt(1, file_id);
                preparedStatement.setInt(2, server.getId());
                preparedStatement.execute();
            }




        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DbUtil.close(preparedStatement);
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

            SQLWarning warning = preparedStatement.getWarnings();

            if(warning != null){
                throw new SQLException(warning.getMessage());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void updateUploadFinish(Integer file_id, Integer server_id, Boolean status) {

        String query = "update server_file set status = ? where file_id = ? AND server_id = ?";
        Connection connection = ConnectionFactory.getConnection();

        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setBoolean(1, status);
            preparedStatement.setInt(2, file_id);
            preparedStatement.setInt(3, server_id);

            SQLWarning warning = preparedStatement.getWarnings();

            if(warning != null){
                throw new SQLException(warning.getMessage());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}

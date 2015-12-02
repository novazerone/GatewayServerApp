package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.List;

import database.daos.FileDAO;
import database.models.File;

/**
 * Created by user on 11/30/2015.
 */
public class FileJDBCTemplate implements FileDAO {

    private PreparedStatement preparedStatement = null;
    private Connection connection;


//    connection = ConnectionFactory.getConnection();
//    try {
//        statement = connection.createStatement();
//        //R = statement.executeQuery("SELECT * FROM files");
//
//
//
//        // Result set get the result of the SQL query
//        R = statement
//                .executeQuery("select * from files");
//        while(R.next()){
//            System.out.println(R.getString("file_name"));
//        }
//
//    } catch (SQLException e) {
//        e.printStackTrace();
//    }

    @Override
    public int create(String file_name, Integer file_size, Integer status) {
        String query = "insert into files (file_name, file_size, status) values (?, ?, ?) ON DUPLICATE KEY UPDATE file_size = ?";

        connection = ConnectionFactory.getConnection();
        int last_inserted_id = 0;
        try {
            preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, file_name);
            preparedStatement.setInt(2, file_size);
            preparedStatement.setInt(3, status);
            preparedStatement.setInt(4, file_size);
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
    public File getFile(Integer id) {
        return null;
    }

    @Override
    public List<File> listFiles(Integer server_id) {
        return null;
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

package database;

import database.ConnectionFactory;
import database.DbUtil;
import database.daos.FileDAO;
import database.models.File;

import java.sql.*;
import java.util.List;
import javax.sql.DataSource;
import javax.swing.*;

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
    public void create(String file_name, Integer file_size, Integer status) {
        String query = "insert into files (file_name, file_size, status) values (?, ?, ?)";

        connection = ConnectionFactory.getConnection();

        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, file_name);
            preparedStatement.setInt(2, file_size);
            preparedStatement.setInt(3, status);
            preparedStatement.execute();

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

    }

    @Override
    public File getFile(Integer id) {
        return null;
    }

    @Override
    public List<File> listFiles() {
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

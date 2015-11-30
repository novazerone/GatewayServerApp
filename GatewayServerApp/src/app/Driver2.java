package app;

import database.ConnectionFactory;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Driver2 {


	public static void main(String[] args) {

		Connection connection;
		Statement statement;
		ResultSet R;

		connection = ConnectionFactory.getConnection();
		try {
			statement = connection.createStatement();
			//R = statement.executeQuery("SELECT * FROM files");


			
			 // Result set get the result of the SQL query
		      R = statement
		          .executeQuery("select * from files");
		      while(R.next()){
		    	  System.out.println(R.getString("file_name"));
		      }
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
}

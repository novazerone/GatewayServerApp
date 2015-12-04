package database.daos;

import java.sql.ResultSet;
import java.sql.SQLException;

import database.models.File;

/**
 * Created by user on 11/30/2015.
 */
public class FileMapper{

	public File mapRow(ResultSet rs, Integer rowNum) throws SQLException{
		File file = new File();
		file.setId(rs.getInt("id"));
		file.setFile_name(rs.getString("file_name"));
		file.setFile_size(rs.getInt("file_size"));
		file.setStatus(rs.getInt("status"));

		return file;
	}


}

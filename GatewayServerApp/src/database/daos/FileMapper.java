package database.daos;

import database.models.File;

import javax.swing.tree.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by user on 11/30/2015.
 */
public class FileMapper{

    public File mapRow(ResultSet rs, Integer rowNum) throws SQLException{
        File file = new File();
        file.setId(rs.getInt("id"));
        file.setFile_name(rs.getString("file_name"));
        file.setFile_size(rs.getString("file_size"));
        file.setStatus(rs.getInt("status"));

        return file;
    }


}

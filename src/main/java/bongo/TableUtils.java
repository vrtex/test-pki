package bongo;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

public class TableUtils {
    public static ArrayList<String> getColumnNames(ResultSet resultSet) throws SQLException {

        ResultSetMetaData metaData = resultSet.getMetaData();

        ArrayList<String> columns = new ArrayList<>();
        for(int i = 0;i<metaData.getColumnCount();i++){
            columns.add(metaData.getColumnName(i+1));
        }
        return columns;
    }
    public static ArrayList<String> getColumnTypes(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();

        ArrayList<String> columns = new ArrayList<>();
        for(int i = 0;i<metaData.getColumnCount();i++){
            columns.add(metaData.getColumnTypeName(i+1));
        }
        return columns;
    }
    public static ArrayList<ArrayList<String>> getRows(ResultSet resultSet) throws SQLException {
        ArrayList<ArrayList<String>> rows = new ArrayList<>();
        while (resultSet.next()){
            rows.add(getRow(resultSet));
        }
        return rows;
    }
    private static ArrayList<String> getRow(ResultSet resultSet) throws SQLException {
        ArrayList<String> row = new ArrayList<>();
        for(int i = 0;i<resultSet.getMetaData().getColumnCount();i++){
            row.add(resultSet.getString(i+1));
        }
        return row;

    }



}

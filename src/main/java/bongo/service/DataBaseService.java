package bongo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import bongo.ConfigDataBase;
import bongo.model.RegisterModel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

@Component
public class DataBaseService
{
    @Autowired
    ConfigDataBase configDataBase;

    public boolean checkUserExist(String email) throws SQLException
    {
        Statement stmt = configDataBase.dataSource();
        final String query = "SELECT COUNT(*) FROM users where users.email= " + "'" + email + "';";
        ResultSet rs = stmt.executeQuery(query);
        rs.next();
        int count = rs.getInt(1);
        return count > 0;
    }

    public boolean checkUserLogin(String email, String password) throws SQLException
    {
        Statement stmt = configDataBase.dataSource();
        final String query = "SELECT COUNT(*) FROM users where users.email= " + "'" + email + "' and users.password='" + password + "';";
        ResultSet rs = stmt.executeQuery(query);
        rs.next();
        int count = rs.getInt(1);
        return count > 0;
    }

    public boolean addUser(RegisterModel registerModel) throws SQLException
    {
        Statement stmt = configDataBase.dataSource();
        final String query = "INSERT INTO users (email,password) values ('" + registerModel.getEmail() + "','" + registerModel.getPassword() + "');";
        stmt.executeUpdate(query);
        return true;
    }

    public ArrayList<String> getAllTableNames() throws SQLException
    {
        Statement stmt = configDataBase.dataSource();
        ResultSet rs = stmt.executeQuery("SELECT table_name" +
                "  FROM information_schema.tables" +
                " WHERE table_schema='public'" +
                "   AND table_type='BASE TABLE'" +
                "   AND table_name not like 'users';" +
                "");
        ArrayList<String> tables = new ArrayList<>();
        while (rs.next()) {
            tables.add(rs.getString(1));
        }
        return tables;
    }

    public ResultSet getResultSet(String query) throws SQLException
    {
        Statement stmt = configDataBase.dataSource();
        return stmt.executeQuery(query);
    }

    public void insertUpdateQuery(String query) throws SQLException
    {
        Statement stmt = configDataBase.dataSource();
        stmt.executeUpdate(query);
    }

    public void createUsersTable() throws SQLException
    {
        Statement stmt = configDataBase.dataSource();
        stmt.executeQuery("CREATE TABLE users (email varchar, password varchar)");
    }
}

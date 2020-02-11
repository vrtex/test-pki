package bongo;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class ConfigDataBase {
    @Autowired
    private Environment env;

    BasicDataSource basicDataSource = null;
    Statement statement;

    public void config() throws SQLException {

        System.out.println("Got uri: " + env.getProperty("spring.datasource.url"));
        System.out.println("Got user: " + env.getProperty("spring.datasource.data-username"));
        System.out.println("Got pass: " + env.getProperty("spring.datasource.data-password"));

        basicDataSource = new BasicDataSource();
        basicDataSource.setUrl(env.getProperty("spring.datasource.url"));
        basicDataSource.setUsername(env.getProperty("spring.datasource.data-username"));
        basicDataSource.setPassword(env.getProperty("spring.datasource.data-password"));
        statement = basicDataSource.getConnection().createStatement(
                ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_UPDATABLE);
    }

    public Statement dataSource() throws SQLException {
        if(basicDataSource == null || basicDataSource.isClosed() || statement == null)
            config();
        return statement;
    }


}
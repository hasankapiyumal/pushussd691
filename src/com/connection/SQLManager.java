package com.connection;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.*;

public class SQLManager {
    private static HikariDataSource dataSource;
    
   static {
        // HikariCP configuration
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql:// " + HardcodedValues.DATABASE_IP_ADDRESS +":"+HardcodedValues.DATABASE_PORT+"/"+ HardcodedValues.DATABASE_NAME); // Database URL
        config.setUsername(HardcodedValues.DATABASE_USERNAME); // Database username
        config.setPassword( HardcodedValues.DATABASE_PASSWORD); // Database password
        config.setMaximumPoolSize(10); // Maximum number of connections in the pool
        config.setMinimumIdle(2); // Minimum number of idle connections
        config.setIdleTimeout(30000); // Idle timeout in milliseconds
        config.setMaxLifetime(1800000); // Maximum lifetime of a connection
        config.setConnectionTimeout(30000); // Connection timeout in milliseconds
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        // Initialize the data source
        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
       
       return dataSource.getConnection();
    }
    
   

}

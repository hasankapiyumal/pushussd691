package com.connection;

import java.sql.*;

public class SQLManager {

    private static Connection connection;
    public SQLManager() {
    }

    public static Connection getConnection() throws SQLException {
       
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://"
                + HardcodedValues.DATABASE_IP_ADDRESS + ":3306/"
                + HardcodedValues.DATABASE_NAME,
                HardcodedValues.DATABASE_USERNAME,
                HardcodedValues.DATABASE_PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return connection;

    }
    
   

}

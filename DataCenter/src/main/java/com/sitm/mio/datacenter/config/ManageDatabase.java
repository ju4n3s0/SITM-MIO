package com.sitm.mio.datacenter.config;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ManageDatabase {

    private static final String URL = 
        "jdbc:postgresql://10.147.20.62:5432/SITM-MIOJM";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    

    public static Connection gConnection() throws SQLException{
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

}

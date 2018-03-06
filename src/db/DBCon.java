package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBCon {
    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static final String URL = "jdbc:mysql://52.57.136.74:3306/";
    private static final String DB_NAME = "nordic_motorhomes";
    private static final String USER = "Nikolay";
    private static final String PASS = "1234";
    private static Connection conn;

    public static Connection getConn()
    {
        if (conn != null)
        {
            return conn;
        }

        try
        {
            Class.forName(DRIVER);
            conn = DriverManager.getConnection(URL + DB_NAME, USER, PASS);
            return conn;

        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return null;
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static void close()
    {
        try
        {
            conn.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}

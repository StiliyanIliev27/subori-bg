package bg.sabori.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL      = "jdbc:mysql://localhost:3306/sabori_bg?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Europe/Sofia";
    private static final String USER     = "root";
    private static final String PASSWORD = "";

    private static Connection instance;

    private DatabaseConnection() {}

    public static Connection getInstance() throws SQLException {
        if (instance == null || instance.isClosed()) {
            instance = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return instance;
    }
}

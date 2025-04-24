package db;

import exceptions.ValidationException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.FileInputStream;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    private Properties properties;

    private DatabaseConnection() {
        properties = loadProperties();
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public static Connection getConnection() throws SQLException {
        return getInstance().createConnection();
    }

    private Connection createConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName(properties.getProperty("db.driver"));
                connection = DriverManager.getConnection(
                        properties.getProperty("db.url"),
                        properties.getProperty("db.user"),
                        properties.getProperty("db.password"));
            } catch (ClassNotFoundException e) {
                throw new SQLException("Database driver not found", e);
            }
        }
        return connection;
    }

    private Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("../../config/db.properties")) {
            if (input == null) {
                try (InputStream fileInput = new FileInputStream("d:\\javaproj\\flexi\\config\\db.properties")) {
                    props.load(fileInput);
                } catch (Exception e) {
                    throw new RuntimeException(
                            "Unable to find db.properties in config directory: " + e.getMessage(), e);
                }
            } else {
                props.load(input);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error loading database properties: " + e.getMessage(), e);
        }
        return props;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
}

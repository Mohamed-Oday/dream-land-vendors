package org.openjfx.dreamlandvendors.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    // Database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/dreamland_vendors";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "admin"; // Update with your MySQL password

    // Connection pool could be implemented here for production applications

    /**
     * Get a connection to the database
     * @return a Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Load the JDBC driver (not necessary for newer JDBC drivers)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Return a new connection
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }

    /**
     * Close the database connection safely
     * @param connection the connection to close
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }

    /**
     * Test the database connection
     * @return true if connection is successful, false otherwise
     */
    public static boolean testConnection() {
        Connection connection = null;
        try {
            connection = getConnection();
            return connection != null;
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        } finally {
            closeConnection(connection);
        }
    }
}

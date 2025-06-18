package org.openjfx.dreamlandvendors.util;

import org.openjfx.dreamlandvendors.dao.UserDAO;
import org.openjfx.dreamlandvendors.dao.impl.UserDAOImpl;
import org.openjfx.dreamlandvendors.model.User;
import org.openjfx.dreamlandvendors.model.User.UserRole;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Utility class to initialize default data in the application
 */
public class DataInitializer {

    private static final UserDAO userDAO = new UserDAOImpl();

    /**
     * Initialize default data in the application
     */
    public static void initializeDefaultData() {
        try {
            initializeDefaultAdmin();
        } catch (SQLException e) {
            System.err.println("Error initializing default data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Initialize default admin user if it doesn't exist
     */
    private static void initializeDefaultAdmin() throws SQLException {
        // Check if the user already exists
        if (userDAO.findByUsername("Oday") == null) {
            // Create the default admin user
            User defaultAdmin = new User();
            defaultAdmin.setUsername("Oday");
            defaultAdmin.setPassword("admin"); // In a real app, we would hash this password
            defaultAdmin.setFullName("Oday Admin");
            defaultAdmin.setRole(UserRole.admin);
            defaultAdmin.setActive(true);

            // Insert the user
            userDAO.insert(defaultAdmin);
            System.out.println("Default admin user 'Oday' created successfully");
        }
    }
}

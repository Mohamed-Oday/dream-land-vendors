package org.openjfx.dreamlandvendors.service;

import org.openjfx.dreamlandvendors.dao.UserDAO;
import org.openjfx.dreamlandvendors.dao.impl.UserDAOImpl;
import org.openjfx.dreamlandvendors.model.User;

import java.sql.SQLException;
import java.util.List;

/**
 * Service class for handling user-related business logic
 */
public class UserService {

    private final UserDAO userDAO;

    // Singleton instance
    private static UserService instance;

    // Currently logged in user
    private User currentUser;

    /**
     * Private constructor for singleton pattern
     */
    private UserService() {
        userDAO = new UserDAOImpl();
    }

    /**
     * Get the singleton instance
     * @return the UserService instance
     */
    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    /**
     * Authenticate a user
     * @param username the username
     * @param password the password
     * @return the authenticated User object if successful, null otherwise
     */
    public User authenticate(String username, String password) {
        try {
            User user = userDAO.findByUsername(username);

            if (user != null) {
                // In a real application, you would use a password hashing library
                // For simplicity, we're comparing plain text here
                if (user.getPassword().equals(password)) {
                    // Update last login time
                    userDAO.updateLastLogin(user.getUserId());

                    // Store the current user
                    currentUser = user;

                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during authentication: " + e.getMessage());
        }

        return null;
    }

    /**
     * Get the currently logged in user
     * @return the current User object
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Log out the current user
     */
    public void logout() {
        currentUser = null;
    }

    /**
     * Check if a user is an admin
     * @param user the user to check
     * @return true if the user is an admin, false otherwise
     */
    public boolean isAdmin(User user) {
        return user != null && user.getRole() == User.UserRole.admin;
    }

    /**
     * Check if a user is a vendor
     * @param user the user to check
     * @return true if the user is a vendor, false otherwise
     */
    public boolean isVendor(User user) {
        return user != null && user.getRole() == User.UserRole.vendor;
    }

    /**
     * Create a new user
     * @param user the user to create
     * @return the ID of the created user
     * @throws SQLException if a database error occurs
     */
    public int createUser(User user) throws SQLException {
        return userDAO.insert(user);
    }

    /**
     * Update an existing user
     * @param user the user to update
     * @return true if the update was successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean updateUser(User user) throws SQLException {
        return userDAO.update(user);
    }

    /**
     * Delete a user
     * @param userId the ID of the user to delete
     * @return true if the deletion was successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean deleteUser(int userId) throws SQLException {
        return userDAO.delete(userId);
    }

    /**
     * Get all users
     * @return a list of all users
     * @throws SQLException if a database error occurs
     */
    public List<User> getAllUsers() throws SQLException {
        return userDAO.findAll();
    }

    /**
     * Get a user by ID
     * @param userId the ID of the user to get
     * @return the User object if found, null otherwise
     * @throws SQLException if a database error occurs
     */
    public User getUserById(int userId) throws SQLException {
        return userDAO.findById(userId);
    }

    /**
     * Check if a username exists
     * @param username the username to check
     * @return true if the username exists, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean usernameExists(String username) throws SQLException {
        return userDAO.usernameExists(username);
    }
}

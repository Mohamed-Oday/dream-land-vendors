package org.openjfx.dreamlandvendors.dao;

import org.openjfx.dreamlandvendors.model.User;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface for User data access operations
 */
public interface UserDAO {

    /**
     * Find a user by username
     * @param username the username to search for
     * @return the User object if found, null otherwise
     * @throws SQLException if a database error occurs
     */
    User findByUsername(String username) throws SQLException;

    /**
     * Find a user by ID
     * @param userId the user ID to search for
     * @return the User object if found, null otherwise
     * @throws SQLException if a database error occurs
     */
    User findById(int userId) throws SQLException;

    /**
     * Get all users
     * @return a list of all users
     * @throws SQLException if a database error occurs
     */
    List<User> findAll() throws SQLException;

    /**
     * Insert a new user
     * @param user the user to insert
     * @return the user ID of the newly inserted user
     * @throws SQLException if a database error occurs
     */
    int insert(User user) throws SQLException;

    /**
     * Update an existing user
     * @param user the user to update
     * @return true if update was successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    boolean update(User user) throws SQLException;

    /**
     * Delete a user
     * @param userId the ID of the user to delete
     * @return true if deletion was successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    boolean delete(int userId) throws SQLException;

    /**
     * Update a user's last login time
     * @param userId the ID of the user
     * @return true if update was successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    boolean updateLastLogin(int userId) throws SQLException;

    /**
     * Check if a username already exists
     * @param username the username to check
     * @return true if the username exists, false otherwise
     * @throws SQLException if a database error occurs
     */
    boolean usernameExists(String username) throws SQLException;
}

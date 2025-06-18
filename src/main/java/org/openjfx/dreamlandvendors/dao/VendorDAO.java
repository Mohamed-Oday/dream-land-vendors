package org.openjfx.dreamlandvendors.dao;

import org.openjfx.dreamlandvendors.model.Vendor;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface for Vendor data access operations
 */
public interface VendorDAO {

    /**
     * Find a vendor by ID
     * @param vendorId the vendor ID to search for
     * @return the Vendor object if found, null otherwise
     * @throws SQLException if a database error occurs
     */
    Vendor findById(int vendorId) throws SQLException;

    /**
     * Get all vendors
     * @return a list of all vendors
     * @throws SQLException if a database error occurs
     */
    List<Vendor> findAll() throws SQLException;

    /**
     * Insert a new vendor
     * @param vendor the vendor to insert
     * @return the vendor ID of the newly inserted vendor
     * @throws SQLException if a database error occurs
     */
    int insert(Vendor vendor) throws SQLException;

    /**
     * Update an existing vendor
     * @param vendor the vendor to update
     * @return true if update was successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    boolean update(Vendor vendor) throws SQLException;

    /**
     * Delete a vendor
     * @param vendorId the ID of the vendor to delete
     * @return true if deletion was successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    boolean delete(int vendorId) throws SQLException;
}

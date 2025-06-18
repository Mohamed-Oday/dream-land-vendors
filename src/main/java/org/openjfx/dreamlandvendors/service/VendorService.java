package org.openjfx.dreamlandvendors.service;

import org.openjfx.dreamlandvendors.dao.VendorDAO;
import org.openjfx.dreamlandvendors.dao.impl.VendorDAOImpl;
import org.openjfx.dreamlandvendors.model.Vendor;

import java.sql.SQLException;
import java.util.List;

/**
 * Service class for handling vendor-related business logic
 */
public class VendorService {

    private final VendorDAO vendorDAO;

    // Singleton instance
    private static VendorService instance;

    /**
     * Private constructor for singleton pattern
     */
    private VendorService() {
        vendorDAO = new VendorDAOImpl();
    }

    /**
     * Get the singleton instance
     * @return the VendorService instance
     */
    public static VendorService getInstance() {
        if (instance == null) {
            instance = new VendorService();
        }
        return instance;
    }

    /**
     * Get all vendors
     * @return a list of all vendors
     * @throws SQLException if a database error occurs
     */
    public List<Vendor> getAllVendors() throws SQLException {
        return vendorDAO.findAll();
    }

    /**
     * Get a vendor by ID
     * @param vendorId the ID of the vendor to get
     * @return the Vendor object if found, null otherwise
     * @throws SQLException if a database error occurs
     */
    public Vendor getVendorById(int vendorId) throws SQLException {
        return vendorDAO.findById(vendorId);
    }

    /**
     * Create a new vendor
     * @param vendor the vendor to create
     * @return the ID of the created vendor
     * @throws SQLException if a database error occurs
     */
    public int createVendor(Vendor vendor) throws SQLException {
        return vendorDAO.insert(vendor);
    }

    /**
     * Update an existing vendor
     * @param vendor the vendor to update
     * @return true if the update was successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean updateVendor(Vendor vendor) throws SQLException {
        return vendorDAO.update(vendor);
    }

    /**
     * Delete a vendor
     * @param vendorId the ID of the vendor to delete
     * @return true if the deletion was successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean deleteVendor(int vendorId) throws SQLException {
        return vendorDAO.delete(vendorId);
    }
}

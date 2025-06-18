package org.openjfx.dreamlandvendors.dao.impl;

import org.openjfx.dreamlandvendors.dao.VendorDAO;
import org.openjfx.dreamlandvendors.model.Vendor;
import org.openjfx.dreamlandvendors.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of VendorDAO interface for MySQL database
 */
public class VendorDAOImpl implements VendorDAO {

    @Override
    public Vendor findById(int vendorId) throws SQLException {
        String sql = "SELECT * FROM vendors WHERE vendor_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, vendorId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractVendorFromResultSet(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<Vendor> findAll() throws SQLException {
        List<Vendor> vendors = new ArrayList<>();
        String sql = "SELECT * FROM vendors ORDER BY name";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                vendors.add(extractVendorFromResultSet(rs));
            }
        }

        return vendors;
    }

    @Override
    public int insert(Vendor vendor) throws SQLException {
        String sql = "INSERT INTO vendors (name, phone_number) VALUES (?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, vendor.getName());
            stmt.setString(2, vendor.getPhoneNumber());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating vendor failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    vendor.setVendorId(generatedKeys.getInt(1));
                    return vendor.getVendorId();
                } else {
                    throw new SQLException("Creating vendor failed, no ID obtained.");
                }
            }
        }
    }

    @Override
    public boolean update(Vendor vendor) throws SQLException {
        String sql = "UPDATE vendors SET name = ?, phone_number = ? WHERE vendor_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, vendor.getName());
            stmt.setString(2, vendor.getPhoneNumber());
            stmt.setInt(3, vendor.getVendorId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean delete(int vendorId) throws SQLException {
        String sql = "DELETE FROM vendors WHERE vendor_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, vendorId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Helper method to extract a Vendor object from a ResultSet
     */
    private Vendor extractVendorFromResultSet(ResultSet rs) throws SQLException {
        Vendor vendor = new Vendor();
        vendor.setVendorId(rs.getInt("vendor_id"));
        vendor.setName(rs.getString("name"));
        vendor.setPhoneNumber(rs.getString("phone_number"));

        // Handle timestamp conversion to LocalDateTime
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            vendor.setCreatedAt(createdAt.toLocalDateTime());
        }

        return vendor;
    }
}

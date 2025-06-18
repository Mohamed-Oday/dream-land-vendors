package org.openjfx.dreamlandvendors.dao.impl;

import org.openjfx.dreamlandvendors.dao.InvoiceDAO;
import org.openjfx.dreamlandvendors.dao.VendorDAO;
import org.openjfx.dreamlandvendors.model.Invoice;
import org.openjfx.dreamlandvendors.model.Vendor;
import org.openjfx.dreamlandvendors.util.DBUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of InvoiceDAO interface for MySQL database
 */
public class InvoiceDAOImpl implements InvoiceDAO {

    private final VendorDAO vendorDAO;

    public InvoiceDAOImpl() {
        this.vendorDAO = new VendorDAOImpl();
    }

    @Override
    public Invoice findById(int invoiceId) throws SQLException {
        String sql = "SELECT * FROM invoices WHERE invoice_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, invoiceId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Invoice invoice = extractInvoiceFromResultSet(rs);

                    // Load the related vendor
                    Vendor vendor = vendorDAO.findById(invoice.getVendorId());
                    invoice.setVendor(vendor);

                    return invoice;
                }
            }
        }

        return null;
    }

    @Override
    public List<Invoice> findAll() throws SQLException {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT * FROM invoices ORDER BY creation_date DESC";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Invoice invoice = extractInvoiceFromResultSet(rs);

                // Load the related vendor
                Vendor vendor = vendorDAO.findById(invoice.getVendorId());
                invoice.setVendor(vendor);

                invoices.add(invoice);
            }
        }

        return invoices;
    }

    @Override
    public List<Invoice> findByVendorId(int vendorId) throws SQLException {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT * FROM invoices WHERE vendor_id = ? ORDER BY creation_date DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, vendorId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Invoice invoice = extractInvoiceFromResultSet(rs);

                    // Load the related vendor
                    Vendor vendor = vendorDAO.findById(invoice.getVendorId());
                    invoice.setVendor(vendor);

                    invoices.add(invoice);
                }
            }
        }

        return invoices;
    }

    @Override
    public List<Invoice> findUnpaidInvoices() throws SQLException {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT * FROM invoices WHERE is_paid = FALSE ORDER BY creation_date";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Invoice invoice = extractInvoiceFromResultSet(rs);

                // Load the related vendor
                Vendor vendor = vendorDAO.findById(invoice.getVendorId());
                invoice.setVendor(vendor);

                invoices.add(invoice);
            }
        }

        return invoices;
    }

    @Override
    public int insert(Invoice invoice) throws SQLException {
        String sql = "INSERT INTO invoices (vendor_id, creation_date, total_amount, amount_due, " +
                     "receipt_image_path, is_paid) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, invoice.getVendorId());
            stmt.setDate(2, Date.valueOf(invoice.getCreationDate()));
            stmt.setBigDecimal(3, invoice.getTotalAmount());
            stmt.setBigDecimal(4, invoice.getAmountDue());
            stmt.setString(5, invoice.getReceiptImagePath());
            stmt.setBoolean(6, invoice.isPaid());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating invoice failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    invoice.setInvoiceId(generatedKeys.getInt(1));
                    return invoice.getInvoiceId();
                } else {
                    throw new SQLException("Creating invoice failed, no ID obtained.");
                }
            }
        }
    }

    @Override
    public boolean update(Invoice invoice) throws SQLException {
        String sql = "UPDATE invoices SET vendor_id = ?, creation_date = ?, total_amount = ?, " +
                     "amount_due = ?, receipt_image_path = ?, is_paid = ? WHERE invoice_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, invoice.getVendorId());
            stmt.setDate(2, Date.valueOf(invoice.getCreationDate()));
            stmt.setBigDecimal(3, invoice.getTotalAmount());
            stmt.setBigDecimal(4, invoice.getAmountDue());
            stmt.setString(5, invoice.getReceiptImagePath());
            stmt.setBoolean(6, invoice.isPaid());
            stmt.setInt(7, invoice.getInvoiceId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean delete(int invoiceId) throws SQLException {
        String sql = "DELETE FROM invoices WHERE invoice_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, invoiceId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Helper method to extract an Invoice object from a ResultSet
     */
    private Invoice extractInvoiceFromResultSet(ResultSet rs) throws SQLException {
        Invoice invoice = new Invoice();
        invoice.setInvoiceId(rs.getInt("invoice_id"));
        invoice.setVendorId(rs.getInt("vendor_id"));

        // Convert java.sql.Date to LocalDate
        Date creationDate = rs.getDate("creation_date");
        if (creationDate != null) {
            invoice.setCreationDate(creationDate.toLocalDate());
        }

        invoice.setTotalAmount(rs.getBigDecimal("total_amount"));
        invoice.setAmountDue(rs.getBigDecimal("amount_due"));
        invoice.setReceiptImagePath(rs.getString("receipt_image_path"));
        invoice.setPaid(rs.getBoolean("is_paid"));

        // Handle timestamp conversion to LocalDateTime
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            invoice.setCreatedAt(createdAt.toLocalDateTime());
        }

        return invoice;
    }
}

package org.openjfx.dreamlandvendors.dao.impl;

import org.openjfx.dreamlandvendors.dao.InvoiceDAO;
import org.openjfx.dreamlandvendors.dao.PaymentDAO;
import org.openjfx.dreamlandvendors.model.Invoice;
import org.openjfx.dreamlandvendors.model.Payment;
import org.openjfx.dreamlandvendors.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of PaymentDAO interface for MySQL database
 */
public class PaymentDAOImpl implements PaymentDAO {

    private final InvoiceDAO invoiceDAO;

    public PaymentDAOImpl() {
        this.invoiceDAO = new InvoiceDAOImpl();
    }

    @Override
    public Payment findById(int paymentId) throws SQLException {
        String sql = "SELECT * FROM payments WHERE payment_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, paymentId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Payment payment = extractPaymentFromResultSet(rs);

                    // Load the related invoice
                    Invoice invoice = invoiceDAO.findById(payment.getInvoiceId());
                    payment.setInvoice(invoice);

                    return payment;
                }
            }
        }

        return null;
    }

    @Override
    public List<Payment> findAll() throws SQLException {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payments ORDER BY date DESC";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Payment payment = extractPaymentFromResultSet(rs);

                // Load the related invoice
                Invoice invoice = invoiceDAO.findById(payment.getInvoiceId());
                payment.setInvoice(invoice);

                payments.add(payment);
            }
        }

        return payments;
    }

    @Override
    public List<Payment> findByInvoiceId(int invoiceId) throws SQLException {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE invoice_id = ? ORDER BY date DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, invoiceId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Payment payment = extractPaymentFromResultSet(rs);

                    // Load the related invoice
                    Invoice invoice = invoiceDAO.findById(payment.getInvoiceId());
                    payment.setInvoice(invoice);

                    payments.add(payment);
                }
            }
        }

        return payments;
    }

    @Override
    public int insert(Payment payment) throws SQLException {
        String sql = "INSERT INTO payments (invoice_id, date, amount) VALUES (?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, payment.getInvoiceId());
            stmt.setDate(2, Date.valueOf(payment.getDate()));
            stmt.setBigDecimal(3, payment.getAmount());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating payment failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    payment.setPaymentId(generatedKeys.getInt(1));
                    return payment.getPaymentId();
                } else {
                    throw new SQLException("Creating payment failed, no ID obtained.");
                }
            }
        }
    }

    @Override
    public boolean update(Payment payment) throws SQLException {
        String sql = "UPDATE payments SET invoice_id = ?, date = ?, amount = ? WHERE payment_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, payment.getInvoiceId());
            stmt.setDate(2, Date.valueOf(payment.getDate()));
            stmt.setBigDecimal(3, payment.getAmount());
            stmt.setInt(4, payment.getPaymentId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean delete(int paymentId) throws SQLException {
        String sql = "DELETE FROM payments WHERE payment_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, paymentId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Helper method to extract a Payment object from a ResultSet
     */
    private Payment extractPaymentFromResultSet(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setPaymentId(rs.getInt("payment_id"));
        payment.setInvoiceId(rs.getInt("invoice_id"));

        // Convert java.sql.Date to LocalDate
        Date date = rs.getDate("date");
        if (date != null) {
            payment.setDate(date.toLocalDate());
        }

        payment.setAmount(rs.getBigDecimal("amount"));

        // Handle timestamp conversion to LocalDateTime
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            payment.setCreatedAt(createdAt.toLocalDateTime());
        }

        return payment;
    }
}

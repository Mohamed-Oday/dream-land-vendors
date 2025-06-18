package org.openjfx.dreamlandvendors.service;

import org.openjfx.dreamlandvendors.dao.PaymentDAO;
import org.openjfx.dreamlandvendors.dao.impl.PaymentDAOImpl;
import org.openjfx.dreamlandvendors.model.Payment;

import java.sql.SQLException;
import java.util.List;

/**
 * Service class for handling payment-related business logic
 */
public class PaymentService {

    private final PaymentDAO paymentDAO;

    // Singleton instance
    private static PaymentService instance;

    /**
     * Private constructor for singleton pattern
     */
    private PaymentService() {
        paymentDAO = new PaymentDAOImpl();
    }

    /**
     * Get the singleton instance
     * @return the PaymentService instance
     */
    public static PaymentService getInstance() {
        if (instance == null) {
            instance = new PaymentService();
        }
        return instance;
    }

    /**
     * Get all payments
     * @return a list of all payments
     * @throws SQLException if a database error occurs
     */
    public List<Payment> getAllPayments() throws SQLException {
        return paymentDAO.findAll();
    }

    /**
     * Get payments for a specific invoice
     * @param invoiceId the ID of the invoice
     * @return a list of payments for the invoice
     * @throws SQLException if a database error occurs
     */
    public List<Payment> getPaymentsByInvoiceId(int invoiceId) throws SQLException {
        return paymentDAO.findByInvoiceId(invoiceId);
    }

    /**
     * Get a payment by ID
     * @param paymentId the ID of the payment to get
     * @return the Payment object if found, null otherwise
     * @throws SQLException if a database error occurs
     */
    public Payment getPaymentById(int paymentId) throws SQLException {
        return paymentDAO.findById(paymentId);
    }

    /**
     * Create a new payment
     * @param payment the payment to create
     * @return the ID of the created payment
     * @throws SQLException if a database error occurs
     */
    public int createPayment(Payment payment) throws SQLException {
        return paymentDAO.insert(payment);
    }

    /**
     * Update an existing payment
     * @param payment the payment to update
     * @return true if the update was successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean updatePayment(Payment payment) throws SQLException {
        return paymentDAO.update(payment);
    }

    /**
     * Delete a payment
     * @param paymentId the ID of the payment to delete
     * @return true if the deletion was successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean deletePayment(int paymentId) throws SQLException {
        return paymentDAO.delete(paymentId);
    }
}

package org.openjfx.dreamlandvendors.dao;

import org.openjfx.dreamlandvendors.model.Payment;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface for Payment data access operations
 */
public interface PaymentDAO {

    /**
     * Find a payment by ID
     * @param paymentId the payment ID to search for
     * @return the Payment object if found, null otherwise
     * @throws SQLException if a database error occurs
     */
    Payment findById(int paymentId) throws SQLException;

    /**
     * Get all payments
     * @return a list of all payments
     * @throws SQLException if a database error occurs
     */
    List<Payment> findAll() throws SQLException;

    /**
     * Get all payments for a specific invoice
     * @param invoiceId the invoice ID to find payments for
     * @return a list of payments for the invoice
     * @throws SQLException if a database error occurs
     */
    List<Payment> findByInvoiceId(int invoiceId) throws SQLException;

    /**
     * Insert a new payment
     * @param payment the payment to insert
     * @return the payment ID of the newly inserted payment
     * @throws SQLException if a database error occurs
     */
    int insert(Payment payment) throws SQLException;

    /**
     * Update an existing payment
     * @param payment the payment to update
     * @return true if update was successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    boolean update(Payment payment) throws SQLException;

    /**
     * Delete a payment
     * @param paymentId the ID of the payment to delete
     * @return true if deletion was successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    boolean delete(int paymentId) throws SQLException;
}

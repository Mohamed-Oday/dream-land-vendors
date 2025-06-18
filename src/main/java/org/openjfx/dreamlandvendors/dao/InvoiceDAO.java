package org.openjfx.dreamlandvendors.dao;

import org.openjfx.dreamlandvendors.model.Invoice;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface for Invoice data access operations
 */
public interface InvoiceDAO {

    /**
     * Find an invoice by ID
     * @param invoiceId the invoice ID to search for
     * @return the Invoice object if found, null otherwise
     * @throws SQLException if a database error occurs
     */
    Invoice findById(int invoiceId) throws SQLException;

    /**
     * Get all invoices
     * @return a list of all invoices
     * @throws SQLException if a database error occurs
     */
    List<Invoice> findAll() throws SQLException;

    /**
     * Get all invoices for a specific vendor
     * @param vendorId the vendor ID to find invoices for
     * @return a list of invoices for the vendor
     * @throws SQLException if a database error occurs
     */
    List<Invoice> findByVendorId(int vendorId) throws SQLException;

    /**
     * Get all unpaid invoices
     * @return a list of unpaid invoices
     * @throws SQLException if a database error occurs
     */
    List<Invoice> findUnpaidInvoices() throws SQLException;

    /**
     * Insert a new invoice
     * @param invoice the invoice to insert
     * @return the invoice ID of the newly inserted invoice
     * @throws SQLException if a database error occurs
     */
    int insert(Invoice invoice) throws SQLException;

    /**
     * Update an existing invoice
     * @param invoice the invoice to update
     * @return true if update was successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    boolean update(Invoice invoice) throws SQLException;

    /**
     * Delete an invoice
     * @param invoiceId the ID of the invoice to delete
     * @return true if deletion was successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    boolean delete(int invoiceId) throws SQLException;
}

package org.openjfx.dreamlandvendors.service;

import org.openjfx.dreamlandvendors.dao.InvoiceDAO;
import org.openjfx.dreamlandvendors.dao.impl.InvoiceDAOImpl;
import org.openjfx.dreamlandvendors.model.Invoice;

import java.sql.SQLException;
import java.util.List;

/**
 * Service class for handling invoice-related business logic
 */
public class InvoiceService {

    private final InvoiceDAO invoiceDAO;

    // Singleton instance
    private static InvoiceService instance;

    /**
     * Private constructor for singleton pattern
     */
    private InvoiceService() {
        invoiceDAO = new InvoiceDAOImpl();
    }

    /**
     * Get the singleton instance
     * @return the InvoiceService instance
     */
    public static InvoiceService getInstance() {
        if (instance == null) {
            instance = new InvoiceService();
        }
        return instance;
    }

    /**
     * Get all invoices
     * @return a list of all invoices
     * @throws SQLException if a database error occurs
     */
    public List<Invoice> getAllInvoices() throws SQLException {
        return invoiceDAO.findAll();
    }

    /**
     * Get invoices for a specific vendor
     * @param vendorId the ID of the vendor
     * @return a list of invoices for the vendor
     * @throws SQLException if a database error occurs
     */
    public List<Invoice> getInvoicesByVendorId(int vendorId) throws SQLException {
        return invoiceDAO.findByVendorId(vendorId);
    }

    /**
     * Get all unpaid invoices
     * @return a list of unpaid invoices
     * @throws SQLException if a database error occurs
     */
    public List<Invoice> getUnpaidInvoices() throws SQLException {
        return invoiceDAO.findUnpaidInvoices();
    }

    /**
     * Get an invoice by ID
     * @param invoiceId the ID of the invoice to get
     * @return the Invoice object if found, null otherwise
     * @throws SQLException if a database error occurs
     */
    public Invoice getInvoiceById(int invoiceId) throws SQLException {
        return invoiceDAO.findById(invoiceId);
    }

    /**
     * Create a new invoice
     * @param invoice the invoice to create
     * @return the ID of the created invoice
     * @throws SQLException if a database error occurs
     */
    public int createInvoice(Invoice invoice) throws SQLException {
        return invoiceDAO.insert(invoice);
    }

    /**
     * Update an existing invoice
     * @param invoice the invoice to update
     * @return true if the update was successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean updateInvoice(Invoice invoice) throws SQLException {
        return invoiceDAO.update(invoice);
    }

    /**
     * Delete an invoice
     * @param invoiceId the ID of the invoice to delete
     * @return true if the deletion was successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean deleteInvoice(int invoiceId) throws SQLException {
        return invoiceDAO.delete(invoiceId);
    }
}

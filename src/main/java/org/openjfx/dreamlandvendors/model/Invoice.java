package org.openjfx.dreamlandvendors.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Invoice {
    private int invoiceId;
    private int vendorId;
    private LocalDate creationDate;
    private BigDecimal totalAmount;
    private BigDecimal amountDue;
    private String receiptImagePath;
    private boolean isPaid;
    private LocalDateTime createdAt;

    // Reference to vendor (not stored in DB, used for display)
    private Vendor vendor;

    // Default constructor
    public Invoice() {
        this.creationDate = LocalDate.now();
    }

    // Constructor with essential fields
    public Invoice(int vendorId, LocalDate creationDate, BigDecimal totalAmount,
                   String receiptImagePath) {
        this.vendorId = vendorId;
        this.creationDate = creationDate;
        this.totalAmount = totalAmount;
        this.amountDue = totalAmount; // Initially, amount due equals total amount
        this.receiptImagePath = receiptImagePath;
        this.isPaid = false;
    }

    // Constructor with all fields
    public Invoice(int invoiceId, int vendorId, LocalDate creationDate, BigDecimal totalAmount,
                  BigDecimal amountDue, String receiptImagePath, boolean isPaid,
                  LocalDateTime createdAt) {
        this.invoiceId = invoiceId;
        this.vendorId = vendorId;
        this.creationDate = creationDate;
        this.totalAmount = totalAmount;
        this.amountDue = amountDue;
        this.receiptImagePath = receiptImagePath;
        this.isPaid = isPaid;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(BigDecimal amountDue) {
        this.amountDue = amountDue;
    }

    public String getReceiptImagePath() {
        return receiptImagePath;
    }

    public void setReceiptImagePath(String receiptImagePath) {
        this.receiptImagePath = receiptImagePath;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    @Override
    public String toString() {
        return "Invoice #" + invoiceId + " - " +
               (vendor != null ? vendor.getName() : "Vendor ID: " + vendorId) +
               " - $" + totalAmount;
    }
}

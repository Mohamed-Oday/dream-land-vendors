package org.openjfx.dreamlandvendors.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Payment {
    private int paymentId;
    private int invoiceId;
    private LocalDate date;
    private BigDecimal amount;
    private LocalDateTime createdAt;

    // Reference to invoice (not stored in DB, used for display)
    private Invoice invoice;

    // Default constructor
    public Payment() {
        this.date = LocalDate.now();
    }

    // Constructor with essential fields
    public Payment(int invoiceId, LocalDate date, BigDecimal amount) {
        this.invoiceId = invoiceId;
        this.date = date;
        this.amount = amount;
    }

    // Constructor with all fields
    public Payment(int paymentId, int invoiceId, LocalDate date, BigDecimal amount,
                  LocalDateTime createdAt) {
        this.paymentId = paymentId;
        this.invoiceId = invoiceId;
        this.date = date;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    @Override
    public String toString() {
        return "Payment #" + paymentId + " - $" + amount + " - " + date;
    }
}

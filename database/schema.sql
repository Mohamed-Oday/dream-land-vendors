-- Dream Land Vendors Payment Management System Database Schema

-- Drop database if it exists and create a new one
DROP DATABASE IF EXISTS dreamland_vendors;
CREATE DATABASE dreamland_vendors;
USE dreamland_vendors;

-- Users table for application login
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,  -- Store hashed passwords
    full_name VARCHAR(100) NOT NULL,
    role ENUM('admin', 'vendor') NOT NULL,
    vendor_id INT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL
);

-- Vendors table
CREATE TABLE vendors (
    vendor_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Invoices table
CREATE TABLE invoices (
    invoice_id INT AUTO_INCREMENT PRIMARY KEY,
    vendor_id INT NOT NULL,
    creation_date DATE NOT NULL,
    total_amount DECIMAL(12, 2) NOT NULL,
    amount_due DECIMAL(12, 2) NOT NULL,
    receipt_image_path VARCHAR(255),
    is_paid BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (vendor_id) REFERENCES vendors(vendor_id)
);

-- Payments table
CREATE TABLE payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    invoice_id INT NOT NULL,
    date DATE NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (invoice_id) REFERENCES invoices(invoice_id) ON DELETE CASCADE
);

-- Create a trigger to update invoice amount_due and is_paid status when a payment is added
DELIMITER //
CREATE TRIGGER after_payment_insert
AFTER INSERT ON payments
FOR EACH ROW
BEGIN
    DECLARE invoice_total DECIMAL(12, 2);
    DECLARE paid_amount DECIMAL(12, 2);
    DECLARE remaining_amount DECIMAL(12, 2);

    -- Get the invoice total
    SELECT total_amount INTO invoice_total
    FROM invoices
    WHERE invoice_id = NEW.invoice_id;

    -- Calculate total paid amount
    SELECT COALESCE(SUM(amount), 0) INTO paid_amount
    FROM payments
    WHERE invoice_id = NEW.invoice_id;

    -- Calculate remaining amount
    SET remaining_amount = invoice_total - paid_amount;

    -- Update the invoice
    UPDATE invoices
    SET amount_due = GREATEST(0, remaining_amount),
        is_paid = (remaining_amount <= 0)
    WHERE invoice_id = NEW.invoice_id;
END //
DELIMITER ;

-- Insert default admin user (password: admin123)
INSERT INTO users (username, password, full_name, role)
VALUES ('admin', '$2a$10$FgOr1tJ1nH7RdpJW9YwPaeiZ2RBtCIimXX1xPYGJU2yH4jmzGqMzW', 'System Administrator', 'admin');

-- Create indexes for better performance
CREATE INDEX idx_invoices_vendor ON invoices(vendor_id);
CREATE INDEX idx_payments_invoice ON payments(invoice_id);
CREATE INDEX idx_users_vendor ON users(vendor_id);

-- End of schema

# 🛒 Supermarket Vendor Payment Tracker – Project Plan

## 📌 Overview

This project is a **standalone JavaFX desktop application** built to help a supermarket owner **manage and track payments to vendors**. The app is designed to be **fully offline**, easy to use, and robust in tracking vendor data, invoices, and payments including receipt uploads.

---

## ✅ Functional Overview

### 🔐 Login Page
- Displayed on app startup.
- Contains:
    - Username and Password fields.
    - Login button.
- Validates against a local `users` table in MySQL.
- Uses hashed passwords (SHA-256 or bcrypt recommended).
- On successful login, navigates to the main dashboard.

---

### 🧾 Vendor Management
- **Add Vendor**:
    - Fields: `Name`, `Phone`, `Email`, `Address`.
- **List Vendors**:
    - Display all vendors with their **total outstanding balance**.
    - Color-coded indicators:
        - 🟥 Red = unpaid
        - 🟨 Yellow = partial
        - 🟩 Green = paid
- **Vendor Detail View**:
    - Shows all **bills** associated with that vendor.
    - Clickable entries to view bill details and payment history.

---

### 📄 Bill/Invoice Entry
- For a given vendor, you can:
    - Add new bills with:
        - `Total Amount`
        - `Bill Date`
    - Track bill status automatically based on payments:
        - `Unpaid`, `Partially Paid`, `Paid`
    - View bill-specific details.

---

### 💸 Payment Tracking (Versements)
- For each bill:
    - Add a **payment**:
        - `Amount`
        - `Payment Date`
        - Upload **receipt** (JPEG, PNG, or PDF).
    - Save receipt locally in a `/receipts` folder.
    - Link receipt path in the database.
    - View **payment history**:
        - Amount paid
        - Payment date
        - Receipt preview or link to open

---

## 🖼️ User Interface (JavaFX)
- **Main Dashboard**:
    - Summary of all vendors and their total balances.
    - Color-coded indicators by payment status.
- **Vendor Detail View**:
    - List of bills per vendor
    - Shows:
        - Total billed amount
        - Paid amount
        - Remaining balance
        - Status
    - Expandable section per bill to show:
        - All payments
        - Preview or link of attached receipt

---

## ⚙️ Technical Requirements

### 🧱 Stack
- **Frontend**: JavaFX
- **Backend**: Local MySQL
- **DB Connection**: JDBC
- **Platform**: Windows (cross-platform if possible)

### 📦 Data Storage
- All receipts stored locally in:  
  `./receipts/`
- Database schema includes links to local receipt files

---

## 📁 Project Structure

VendorTracker/
├── src/ # Java source files
│ ├── login/
│ ├── dashboard/
│ ├── vendor/
│ ├── bills/
│ ├── payments/
├── resources/
│ ├── fxml/
│ ├── images/
├── receipts/ # Local folder for uploaded receipts
├── sql/
│ └── schema.sql # SQL file to set up the DB
├── README.md
└── plan.md # This file
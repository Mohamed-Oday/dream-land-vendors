package org.openjfx.dreamlandvendors.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import org.openjfx.dreamlandvendors.model.Invoice;
import org.openjfx.dreamlandvendors.model.Payment;
import org.openjfx.dreamlandvendors.model.Vendor;
import org.openjfx.dreamlandvendors.service.InvoiceService;
import org.openjfx.dreamlandvendors.service.PaymentService;
import org.openjfx.dreamlandvendors.service.VendorService;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the payment management view
 */
public class PaymentManagementController implements Initializable {

    @FXML
    private ComboBox<Vendor> vendorComboBox;

    @FXML
    private ComboBox<Invoice> invoiceComboBox;

    @FXML
    private Label totalAmountLabel;

    @FXML
    private Label amountDueLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private DatePicker dateField;

    @FXML
    private TextField amountField;

    @FXML
    private Button clearPaymentButton;

    @FXML
    private Button addPaymentButton;

    @FXML
    private TextField searchPaymentsField;

    @FXML
    private TableView<Payment> paymentsTable;

    @FXML
    private TableColumn<Payment, Integer> paymentIdColumn;

    @FXML
    private TableColumn<Payment, LocalDate> paymentDateColumn;

    @FXML
    private TableColumn<Payment, BigDecimal> paymentAmountColumn;

    @FXML
    private TableColumn<Payment, String> paymentMethodColumn;

    @FXML
    private TableColumn<Payment, Void> actionColumn;

    @FXML
    private HBox alertBox;

    @FXML
    private Label alertMessage;

    @FXML
    private Text alertIcon;

    @FXML
    private Text totalPaymentsText;

    @FXML
    private Text totalPaidAmountText;

    @FXML
    private Text recentPaymentsText;

    private ObservableList<Payment> paymentList = FXCollections.observableArrayList();
    private FilteredList<Payment> filteredPayments;

    private final VendorService vendorService = VendorService.getInstance();
    private final InvoiceService invoiceService = InvoiceService.getInstance();
    private final PaymentService paymentService = PaymentService.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize date picker with current date
        dateField.setValue(LocalDate.now());

        // Set up table columns
        paymentIdColumn.setCellValueFactory(new PropertyValueFactory<>("paymentId"));
        paymentDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        paymentAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        paymentMethodColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Cash"));

        // Format date column
        paymentDateColumn.setCellFactory(column -> new TableCell<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(formatter.format(date));
                }
            }
        });

        // Format amount column
        paymentAmountColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(amount.toString() + " DZD");
                }
            }
        });

        // Add delete button to action column
        actionColumn.setCellFactory(column -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.getStyleClass().add("danger-button");
                deleteButton.setOnAction(event -> {
                    Payment payment = getTableView().getItems().get(getIndex());
                    handleDeletePayment(payment);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });

        paymentsTable.setItems(paymentList);

        // Set up vendor combo box
        loadVendors();

        // Set up filtered list for payments
        filteredPayments = new FilteredList<>(paymentList, p -> true);
        paymentsTable.setItems(filteredPayments);

        // Set up search and vendor filter listeners
        searchPaymentsField.textProperty().addListener((observable, oldValue, newValue) -> {
            updatePaymentsFilter();
        });

        // Set up vendor combo box
        vendorComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadInvoicesForVendor(newValue.getVendorId());
            } else {
                invoiceComboBox.getItems().clear();
                clearInvoiceDetails();
            }
        });

        invoiceComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                displayInvoiceDetails(newValue);
                loadPaymentsForInvoice(newValue.getInvoiceId());
                addPaymentButton.setDisable(false);
            } else {
                clearInvoiceDetails();
                paymentList.clear();
                addPaymentButton.setDisable(true);
            }
        });
    }

    /**
     * Load vendors from the database
     */
    private void loadVendors() {
        try {
            ObservableList<Vendor> vendors = FXCollections.observableArrayList(vendorService.getAllVendors());
            vendorComboBox.setItems(vendors);

            // Set up vendor display in combo box
            vendorComboBox.setConverter(new StringConverter<>() {
                @Override
                public String toString(Vendor vendor) {
                    return vendor != null ? vendor.getName() : "";
                }

                @Override
                public Vendor fromString(String string) {
                    return vendorComboBox.getItems().stream()
                            .filter(vendor -> vendor.getName().equals(string))
                            .findFirst()
                            .orElse(null);
                }
            });
        } catch (SQLException e) {
            showAlert("Error loading vendors: " + e.getMessage(), "alert-error");
            e.printStackTrace();
        }
    }

    /**
     * Load invoices for a specific vendor
     */
    private void loadInvoicesForVendor(int vendorId) {
        try {
            List<Invoice> invoices = invoiceService.getInvoicesByVendorId(vendorId);
            ObservableList<Invoice> invoiceItems = FXCollections.observableArrayList(invoices);
            invoiceComboBox.setItems(invoiceItems);

            // Set up invoice display in combo box
            invoiceComboBox.setConverter(new StringConverter<>() {
                @Override
                public String toString(Invoice invoice) {
                    if (invoice == null) return "";
                    return "Invoice #" + invoice.getInvoiceId() + " - " +
                           invoice.getCreationDate() + " - " + invoice.getTotalAmount().toString() + " DZD";
                }

                @Override
                public Invoice fromString(String string) {
                    return null; // Not used for selection
                }
            });
        } catch (SQLException e) {
            showAlert("Error loading invoices: " + e.getMessage(), "alert-error");
            e.printStackTrace();
        }
    }

    /**
     * Display invoice details
     */
    private void displayInvoiceDetails(Invoice invoice) {
        totalAmountLabel.setText(invoice.getTotalAmount().toString() + " DZD");
        amountDueLabel.setText(invoice.getAmountDue().toString() + " DZD");

        if (invoice.isPaid() || invoice.getAmountDue().compareTo(BigDecimal.ZERO) == 0) {
            statusLabel.setText("PAID");
            statusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        } else if (invoice.getAmountDue().compareTo(invoice.getTotalAmount()) < 0) {
            statusLabel.setText("PARTIAL");
            statusLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
        } else {
            statusLabel.setText("UNPAID");
            statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        }
    }

    /**
     * Clear invoice details
     */
    private void clearInvoiceDetails() {
        totalAmountLabel.setText("-");
        amountDueLabel.setText("-");
        statusLabel.setText("-");
        statusLabel.setStyle("");
    }

    /**
     * Load payments for a specific invoice
     */
    private void loadPaymentsForInvoice(int invoiceId) {
        try {
            List<Payment> payments = paymentService.getPaymentsByInvoiceId(invoiceId);
            paymentList.clear();
            paymentList.addAll(payments);
            updateStatistics();
        } catch (SQLException e) {
            showAlert("Error loading payments: " + e.getMessage(), "alert-error");
            e.printStackTrace();
        }
    }

    /**
     * Update payment statistics display
     */
    private void updateStatistics() {
        // Only use the currently displayed paymentList (filtered for the selected invoice)
        if (totalPaymentsText != null) {
            totalPaymentsText.setText(String.valueOf(paymentList.size()));
        }
        BigDecimal totalPaidAmount = BigDecimal.ZERO;
        int recentPayments = 0;
        LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);
        for (Payment payment : paymentList) {
            totalPaidAmount = totalPaidAmount.add(payment.getAmount());
            if (payment.getDate().isAfter(currentMonth.minusDays(1))) {
                recentPayments++;
            }
        }
        if (totalPaidAmountText != null) {
            totalPaidAmountText.setText(String.format("%.2f DZD", totalPaidAmount));
        }
        if (recentPaymentsText != null) {
            recentPaymentsText.setText(String.valueOf(recentPayments));
        }
    }

    /**
     * Handle add payment button action
     */
    @FXML
    void handleAddPayment() {
        if (validatePaymentForm()) {
            try {
                Invoice selectedInvoice = invoiceComboBox.getValue();
                LocalDate paymentDate = dateField.getValue();
                BigDecimal amount = new BigDecimal(amountField.getText().trim());

                // Check if amount is greater than amount due
                if (amount.compareTo(selectedInvoice.getAmountDue()) > 0) {
                    showAlert("Payment amount cannot exceed amount due ($" +
                             selectedInvoice.getAmountDue() + ")", "alert-error");
                    return;
                }

                // Create and save payment
                Payment payment = new Payment();
                payment.setInvoiceId(selectedInvoice.getInvoiceId());
                payment.setDate(paymentDate);
                payment.setAmount(amount);

                int paymentId = paymentService.createPayment(payment);
                payment.setPaymentId(paymentId);

                // Refresh data
                loadPaymentsForInvoice(selectedInvoice.getInvoiceId());

                // Refresh invoice details (amount due will be updated by trigger)
                selectedInvoice = invoiceService.getInvoiceById(selectedInvoice.getInvoiceId());
                invoiceComboBox.setValue(selectedInvoice);
                displayInvoiceDetails(selectedInvoice);

                // Clear payment form
                dateField.setValue(LocalDate.now());
                amountField.clear();

                showAlert("Payment added successfully!", "alert-success");
            } catch (SQLException e) {
                showAlert("Error adding payment: " + e.getMessage(), "alert-error");
                e.printStackTrace();
            }
        }
    }

    /**
     * Handle delete payment action
     */
    private void handleDeletePayment(Payment payment) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Delete");
        confirmDialog.setHeaderText("Delete Payment");
        confirmDialog.setContentText("Are you sure you want to delete this payment? This will increase the amount due on the invoice.");

        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    paymentService.deletePayment(payment.getPaymentId());

                    // Refresh data
                    Invoice selectedInvoice = invoiceComboBox.getValue();
                    loadPaymentsForInvoice(selectedInvoice.getInvoiceId());

                    // Refresh invoice details (amount due will be updated)
                    selectedInvoice = invoiceService.getInvoiceById(selectedInvoice.getInvoiceId());
                    invoiceComboBox.setValue(selectedInvoice);
                    displayInvoiceDetails(selectedInvoice);

                    showAlert("Payment deleted successfully!", "alert-success");
                } catch (SQLException e) {
                    showAlert("Error deleting payment: " + e.getMessage(), "alert-error");
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Handle browse receipt button click
     */
    @FXML
    void handleBrowseReceipt() {
        // Implementation needed
    }

    /**
     * Handle clear payment form
     */
    @FXML
    void handleClearPayment() {
        dateField.setValue(null);
        amountField.clear();
        addPaymentButton.setDisable(true);
    }

    /**
     * Validate payment form
     */
    private boolean validatePaymentForm() {
        if (invoiceComboBox.getValue() == null) {
            showAlert("Please select an invoice", "alert-error");
            return false;
        }

        if (dateField.getValue() == null) {
            showAlert("Please select a payment date", "alert-error");
            dateField.requestFocus();
            return false;
        }

        String amountText = amountField.getText().trim();
        if (amountText.isEmpty()) {
            showAlert("Payment amount is required", "alert-error");
            amountField.requestFocus();
            return false;
        }

        try {
            BigDecimal amount = new BigDecimal(amountText);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                showAlert("Payment amount must be greater than zero", "alert-error");
                amountField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid amount format", "alert-error");
            amountField.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Show an alert message
     */
    private void showAlert(String message, String styleClass) {
        alertMessage.setText(message);
        alertBox.getStyleClass().removeAll("alert-success", "alert-error", "alert-warning");
        alertBox.getStyleClass().add(styleClass);
        
        // Set appropriate icon based on alert type
        if (alertIcon != null) {
            switch (styleClass) {
                case "alert-success":
                    alertIcon.setText("✓");
                    alertIcon.getStyleClass().removeAll("alert-icon-error", "alert-icon-warning");
                    alertIcon.getStyleClass().add("alert-icon-success");
                    break;
                case "alert-error":
                    alertIcon.setText("✕");
                    alertIcon.getStyleClass().removeAll("alert-icon-success", "alert-icon-warning");
                    alertIcon.getStyleClass().add("alert-icon-error");
                    break;
                case "alert-warning":
                    alertIcon.setText("⚠");
                    alertIcon.getStyleClass().removeAll("alert-icon-success", "alert-icon-error");
                    alertIcon.getStyleClass().add("alert-icon-warning");
                    break;
                default:
                    alertIcon.setText("●");
                    break;
            }
        }
        
        alertBox.setVisible(true);
    }

    private void updatePaymentsFilter() {
        String searchText = searchPaymentsField.getText();
        filteredPayments.setPredicate(payment -> {
            boolean matchesSearch = true;
            // Search by payment ID or amount
            if (searchText != null && !searchText.isEmpty()) {
                String lowerCaseFilter = searchText.toLowerCase();
                matchesSearch = String.valueOf(payment.getPaymentId()).contains(lowerCaseFilter)
                        || (payment.getAmount() != null && payment.getAmount().toString().contains(lowerCaseFilter));
            }
            return matchesSearch;
        });
    }
}

package org.openjfx.dreamlandvendors.vendor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.openjfx.dreamlandvendors.model.Invoice;
import org.openjfx.dreamlandvendors.model.Payment;
import org.openjfx.dreamlandvendors.model.User;
import org.openjfx.dreamlandvendors.service.InvoiceService;
import org.openjfx.dreamlandvendors.service.PaymentService;
import org.openjfx.dreamlandvendors.service.UserService;

import java.awt.Desktop;
import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Controller for the vendor invoices view
 */
public class VendorInvoicesController implements Initializable {

    @FXML
    private ComboBox<String> filterComboBox;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Invoice> invoiceTable;

    @FXML
    private TableColumn<Invoice, Integer> idColumn;

    @FXML
    private TableColumn<Invoice, LocalDate> dateColumn;

    @FXML
    private TableColumn<Invoice, BigDecimal> totalAmountColumn;

    @FXML
    private TableColumn<Invoice, BigDecimal> amountDueColumn;

    @FXML
    private TableColumn<Invoice, Boolean> statusColumn;

    @FXML
    private TableColumn<Invoice, Void> viewColumn;

    @FXML
    private VBox invoiceDetailsSection;

    @FXML
    private Label detailsIdLabel;

    @FXML
    private Label detailsDateLabel;

    @FXML
    private Label detailsTotalLabel;

    @FXML
    private Label detailsDueLabel;

    @FXML
    private Label detailsStatusLabel;

    @FXML
    private TableView<Payment> paymentsTable;

    @FXML
    private TableColumn<Payment, Integer> paymentIdColumn;

    @FXML
    private TableColumn<Payment, LocalDate> paymentDateColumn;

    @FXML
    private TableColumn<Payment, BigDecimal> paymentAmountColumn;

    @FXML
    private Button viewReceiptButton;

    private ObservableList<Invoice> invoiceList = FXCollections.observableArrayList();
    private FilteredList<Invoice> filteredInvoices;
    private ObservableList<Payment> paymentList = FXCollections.observableArrayList();

    private Invoice selectedInvoice;

    private final UserService userService = UserService.getInstance();
    private final InvoiceService invoiceService = InvoiceService.getInstance();
    private final PaymentService paymentService = PaymentService.getInstance();

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up filter combo box
        ObservableList<String> filterOptions = FXCollections.observableArrayList(
                "All", "Paid", "Unpaid", "Partially Paid"
        );
        filterComboBox.setItems(filterOptions);
        filterComboBox.setValue("All");

        // Set up table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("invoiceId"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("creationDate"));
        totalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        amountDueColumn.setCellValueFactory(new PropertyValueFactory<>("amountDue"));

        // Format date column
        dateColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(dateFormatter.format(date));
                }
            }
        });

        // Format amount columns
        totalAmountColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText("$" + amount.toString());
                }
            }
        });

        amountDueColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText("$" + amount.toString());
                }
            }
        });

        // Set up status column
        statusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setStyle("");
                } else {
                    Invoice invoice = getTableView().getItems().get(getIndex());
                    if (invoice.isPaid() || invoice.getAmountDue().compareTo(BigDecimal.ZERO) == 0) {
                        setText("PAID");
                        setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    } else if (invoice.getAmountDue().compareTo(invoice.getTotalAmount()) < 0) {
                        setText("PARTIAL");
                        setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                    } else {
                        setText("UNPAID");
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    }
                }
            }
        });

        // Set up view column with view button
        viewColumn.setCellFactory(param -> new TableCell<>() {
            private final Button viewButton = new Button("View");

            {
                viewButton.getStyleClass().add("primary-button");
                viewButton.setOnAction(event -> {
                    Invoice invoice = getTableView().getItems().get(getIndex());
                    showInvoiceDetails(invoice);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(viewButton);
                }
            }
        });

        // Set up payments table
        paymentIdColumn.setCellValueFactory(new PropertyValueFactory<>("paymentId"));
        paymentDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        paymentAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

        // Format payment date
        paymentDateColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(dateFormatter.format(date));
                }
            }
        });

        // Format payment amount
        paymentAmountColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText("$" + amount.toString());
                }
            }
        });

        // Set up search functionality
        filteredInvoices = new FilteredList<>(invoiceList, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> updateFilterPredicate());

        filterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> updateFilterPredicate());

        invoiceTable.setItems(filteredInvoices);
        paymentsTable.setItems(paymentList);

        // Load invoices
        loadInvoices();
    }

    /**
     * Update the filter predicate based on search text and filter selection
     */
    private void updateFilterPredicate() {
        String searchText = searchField.getText().trim().toLowerCase();
        String filterValue = filterComboBox.getValue();

        filteredInvoices.setPredicate(invoice -> {
            // Apply search filter
            boolean matchesSearch = true;
            if (!searchText.isEmpty()) {
                matchesSearch = String.valueOf(invoice.getInvoiceId()).contains(searchText) ||
                                (invoice.getCreationDate() != null &&
                                 dateFormatter.format(invoice.getCreationDate()).toLowerCase().contains(searchText));
            }

            // If doesn't match search, no need to check status
            if (!matchesSearch) {
                return false;
            }

            // Apply status filter
            if ("All".equals(filterValue)) {
                return true;
            } else if ("Paid".equals(filterValue)) {
                return invoice.isPaid() || invoice.getAmountDue().compareTo(BigDecimal.ZERO) == 0;
            } else if ("Unpaid".equals(filterValue)) {
                return !invoice.isPaid() && invoice.getAmountDue().compareTo(invoice.getTotalAmount()) == 0;
            } else if ("Partially Paid".equals(filterValue)) {
                return !invoice.isPaid() && invoice.getAmountDue().compareTo(BigDecimal.ZERO) > 0
                        && invoice.getAmountDue().compareTo(invoice.getTotalAmount()) < 0;
            }

            return true;
        });
    }

    /**
     * Load invoices for the current vendor
     */
    private void loadInvoices() {
        try {
            User currentUser = userService.getCurrentUser();
            Integer vendorId = currentUser.getVendorId();

            if (vendorId != null) {
                invoiceList.clear();
                invoiceList.addAll(invoiceService.getInvoicesByVendorId(vendorId));
            }
        } catch (SQLException e) {
            showErrorAlert("Error Loading Invoices", "Failed to load invoices: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Show invoice details
     */
    private void showInvoiceDetails(Invoice invoice) {
        selectedInvoice = invoice;

        // Set invoice details
        detailsIdLabel.setText(String.valueOf(invoice.getInvoiceId()));
        detailsDateLabel.setText(dateFormatter.format(invoice.getCreationDate()));
        detailsTotalLabel.setText("$" + invoice.getTotalAmount().toString());
        detailsDueLabel.setText("$" + invoice.getAmountDue().toString());

        // Set status with appropriate styling
        if (invoice.isPaid() || invoice.getAmountDue().compareTo(BigDecimal.ZERO) == 0) {
            detailsStatusLabel.setText("PAID");
            detailsStatusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        } else if (invoice.getAmountDue().compareTo(invoice.getTotalAmount()) < 0) {
            detailsStatusLabel.setText("PARTIALLY PAID");
            detailsStatusLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
        } else {
            detailsStatusLabel.setText("UNPAID");
            detailsStatusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        }

        // Load payments for this invoice
        loadPaymentsForInvoice(invoice.getInvoiceId());

        // Check if receipt image exists
        viewReceiptButton.setDisable(invoice.getReceiptImagePath() == null ||
                                     invoice.getReceiptImagePath().isEmpty());

        // Show details section
        invoiceDetailsSection.setVisible(true);
        invoiceDetailsSection.setManaged(true);
    }

    /**
     * Load payments for an invoice
     */
    private void loadPaymentsForInvoice(int invoiceId) {
        try {
            paymentList.clear();
            paymentList.addAll(paymentService.getPaymentsByInvoiceId(invoiceId));
        } catch (SQLException e) {
            showErrorAlert("Error Loading Payments", "Failed to load payments: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle view receipt button action
     */
    @FXML
    private void handleViewReceipt() {
        if (selectedInvoice != null && selectedInvoice.getReceiptImagePath() != null) {
            try {
                File file = new File(selectedInvoice.getReceiptImagePath());
                if (file.exists() && Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                } else {
                    showErrorAlert("Error Opening Receipt", "Receipt file not found or cannot be opened.");
                }
            } catch (Exception e) {
                showErrorAlert("Error Opening Receipt", "Failed to open receipt: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Handle close details button action
     */
    @FXML
    private void handleCloseDetails() {
        invoiceDetailsSection.setVisible(false);
        invoiceDetailsSection.setManaged(false);
        selectedInvoice = null;
    }

    /**
     * Show error alert
     */
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

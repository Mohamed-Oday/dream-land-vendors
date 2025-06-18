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
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import org.openjfx.dreamlandvendors.model.Invoice;
import org.openjfx.dreamlandvendors.model.Vendor;
import org.openjfx.dreamlandvendors.service.InvoiceService;
import org.openjfx.dreamlandvendors.service.VendorService;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.UUID;

/**
 * Controller for the invoice management view
 */
public class InvoiceManagementController implements Initializable {

    @FXML
    private ComboBox<Vendor> vendorComboBox;

    @FXML
    private DatePicker dateField;

    @FXML
    private TextField amountField;

    @FXML
    private TextField imagePathField;

    @FXML
    private Button browseButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button clearButton;

    @FXML
    private Button deleteButton;

    @FXML
    private ComboBox<String> filterComboBox;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Invoice> invoiceTable;

    @FXML
    private TableColumn<Invoice, Integer> idColumn;

    @FXML
    private TableColumn<Invoice, String> vendorColumn;

    @FXML
    private TableColumn<Invoice, LocalDate> dateColumn;

    @FXML
    private TableColumn<Invoice, BigDecimal> totalAmountColumn;

    @FXML
    private TableColumn<Invoice, BigDecimal> amountDueColumn;

    @FXML
    private TableColumn<Invoice, Boolean> statusColumn;    @FXML
    private HBox alertBox;

    @FXML
    private Label alertMessage;

    @FXML
    private Text alertIcon;

    @FXML
    private Text totalInvoicesText;

    @FXML
    private Text totalAmountText;

    @FXML
    private Text pendingAmountText;

    private ObservableList<Invoice> invoiceList = FXCollections.observableArrayList();
    private FilteredList<Invoice> filteredInvoices;
    private Invoice currentInvoice;
    private File selectedFile;

    private final InvoiceService invoiceService = InvoiceService.getInstance();
    private final VendorService vendorService = VendorService.getInstance();

    // Directory to store receipt images
    private final String RECEIPTS_DIR = "receipts";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize date picker with current date
        dateField.setValue(LocalDate.now());

        // Set up table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("invoiceId"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("creationDate"));
        totalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        amountDueColumn.setCellValueFactory(new PropertyValueFactory<>("amountDue"));

        // Custom cell factories for vendor name and status
        vendorColumn.setCellValueFactory(cellData -> {
            Invoice invoice = cellData.getValue();
            if (invoice.getVendor() != null) {
                return javafx.beans.binding.Bindings.createStringBinding(() -> invoice.getVendor().getName());
            }
            return javafx.beans.binding.Bindings.createStringBinding(() -> "Unknown");
        });

        statusColumn.setCellValueFactory(new PropertyValueFactory<>("paid"));
        statusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    if (item) {
                        setText("Paid");
                        setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    } else {
                        Invoice invoice = getTableView().getItems().get(getIndex());
                        if (invoice.getAmountDue().compareTo(BigDecimal.ZERO) == 0) {
                            setText("Paid");
                            setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                        } else if (invoice.getAmountDue().compareTo(invoice.getTotalAmount()) < 0) {
                            setText("Partial");
                            setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                        } else {
                            setText("Unpaid");
                            setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                        }
                    }
                }
            }
        });

        // Format date column
        dateColumn.setCellFactory(column -> new TableCell<>() {
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

        // Set up search functionality
        filteredInvoices = new FilteredList<>(invoiceList, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredInvoices.setPredicate(invoice -> {
                // If filter text is empty, display all invoices
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                // Match by vendor name
                if (invoice.getVendor() != null && invoice.getVendor().getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }

                // Match by invoice ID
                if (String.valueOf(invoice.getInvoiceId()).contains(lowerCaseFilter)) {
                    return true;
                }

                return false;
            });
        });

        // Set up filter combo box
        ObservableList<String> filterOptions = FXCollections.observableArrayList(
                "All", "Paid", "Unpaid", "Partially Paid"
        );
        filterComboBox.setItems(filterOptions);
        filterComboBox.setValue("All");

        filterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            filteredInvoices.setPredicate(invoice -> {
                // Apply text filter
                String searchText = searchField.getText();
                boolean matchesSearch = true;

                if (searchText != null && !searchText.isEmpty()) {
                    String lowerCaseFilter = searchText.toLowerCase();

                    // Match by vendor name
                    if (invoice.getVendor() != null && invoice.getVendor().getName().toLowerCase().contains(lowerCaseFilter)) {
                        // Match
                    } else if (String.valueOf(invoice.getInvoiceId()).contains(lowerCaseFilter)) {
                        // Match
                    } else {
                        matchesSearch = false;
                    }
                }

                // If doesn't match search text, no need to check status
                if (!matchesSearch) {
                    return false;
                }

                // Apply status filter
                if ("All".equals(newValue)) {
                    return true;
                } else if ("Paid".equals(newValue)) {
                    return invoice.isPaid() || invoice.getAmountDue().compareTo(BigDecimal.ZERO) == 0;
                } else if ("Unpaid".equals(newValue)) {
                    return !invoice.isPaid() && invoice.getAmountDue().compareTo(invoice.getTotalAmount()) == 0;
                } else if ("Partially Paid".equals(newValue)) {
                    return !invoice.isPaid() && invoice.getAmountDue().compareTo(BigDecimal.ZERO) > 0
                            && invoice.getAmountDue().compareTo(invoice.getTotalAmount()) < 0;
                }

                return true;
            });
        });

        invoiceTable.setItems(filteredInvoices);

        // Add selection listener to populate form when an invoice is selected
        invoiceTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                currentInvoice = newSelection;
                populateForm(currentInvoice);
                deleteButton.setDisable(false);
            } else {
                clearForm();
                deleteButton.setDisable(true);
            }
        });

        // Load vendors for the combo box
        loadVendors();

        // Load invoices from the database
        loadInvoices();

        // Create receipts directory if it doesn't exist
        createReceiptsDirectory();
    }

    /**
     * Create the receipts directory if it doesn't exist
     */
    private void createReceiptsDirectory() {
        try {
            Path path = Paths.get(RECEIPTS_DIR);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (Exception e) {
            showAlert("Error creating receipts directory: " + e.getMessage(), "alert-error");
        }
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
    }    /**
     * Load invoices from the database
     */
    private void loadInvoices() {
        try {
            invoiceList.clear();
            invoiceList.addAll(invoiceService.getAllInvoices());
            updateStatistics();
        } catch (SQLException e) {
            showAlert("Error loading invoices: " + e.getMessage(), "alert-error");
            e.printStackTrace();
        }
    }

    /**
     * Update invoice statistics display
     */
    private void updateStatistics() {
        if (totalInvoicesText != null) {
            totalInvoicesText.setText(String.valueOf(invoiceList.size()));
        }
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal pendingAmount = BigDecimal.ZERO;
        
        for (Invoice invoice : invoiceList) {
            totalAmount = totalAmount.add(invoice.getTotalAmount());
            pendingAmount = pendingAmount.add(invoice.getAmountDue());
        }
        
        if (totalAmountText != null) {
            totalAmountText.setText(String.format("$%.2f", totalAmount));
        }
        if (pendingAmountText != null) {
            pendingAmountText.setText(String.format("$%.2f", pendingAmount));
        }
    }

    /**
     * Handle browse button action
     */
    @FXML
    private void handleBrowse() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Receipt Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        selectedFile = fileChooser.showOpenDialog(browseButton.getScene().getWindow());
        if (selectedFile != null) {
            imagePathField.setText(selectedFile.getName());
        }
    }

    /**
     * Handle save button action
     */
    @FXML
    private void handleSave() {
        if (validateForm()) {
            try {
                Vendor selectedVendor = vendorComboBox.getValue();
                LocalDate selectedDate = dateField.getValue();
                BigDecimal amount = new BigDecimal(amountField.getText().trim());

                String imagePath = null;
                if (selectedFile != null) {
                    // Generate unique filename for the receipt
                    String uniqueFileName = UUID.randomUUID().toString() + getFileExtension(selectedFile.getName());
                    Path destinationPath = Paths.get(RECEIPTS_DIR, uniqueFileName);

                    // Copy the file to the receipts directory
                    Files.copy(selectedFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

                    imagePath = destinationPath.toString();
                } else if (currentInvoice != null) {
                    // Keep existing image path when updating
                    imagePath = currentInvoice.getReceiptImagePath();
                }

                if (currentInvoice == null) {
                    // Create new invoice
                    Invoice invoice = new Invoice();
                    invoice.setVendorId(selectedVendor.getVendorId());
                    invoice.setCreationDate(selectedDate);
                    invoice.setTotalAmount(amount);
                    invoice.setAmountDue(amount); // Initially, amount due equals total amount
                    invoice.setReceiptImagePath(imagePath);
                    invoice.setPaid(false);

                    int invoiceId = invoiceService.createInvoice(invoice);
                    invoice.setInvoiceId(invoiceId);
                    invoice.setVendor(selectedVendor);                    invoiceList.add(invoice);
                    updateStatistics();

                    showAlert("Invoice created successfully!", "alert-success");
                } else {
                    // Update existing invoice
                    currentInvoice.setVendorId(selectedVendor.getVendorId());
                    currentInvoice.setCreationDate(selectedDate);
                    currentInvoice.setTotalAmount(amount);
                    // Don't update amount due directly, it's managed by the payment system
                    currentInvoice.setReceiptImagePath(imagePath);

                    invoiceService.updateInvoice(currentInvoice);

                    // Update vendor reference
                    currentInvoice.setVendor(selectedVendor);

                    // Refresh the table
                    invoiceTable.refresh();

                    showAlert("Invoice updated successfully!", "alert-success");
                }

                clearForm();
            } catch (Exception e) {
                showAlert("Error saving invoice: " + e.getMessage(), "alert-error");
                e.printStackTrace();
            }
        }
    }

    /**
     * Handle clear button action
     */
    @FXML
    private void handleClear() {
        clearForm();
    }

    /**
     * Handle delete button action
     */
    @FXML
    private void handleDelete() {
        if (currentInvoice != null) {
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Confirm Delete");
            confirmDialog.setHeaderText("Delete Invoice");
            confirmDialog.setContentText("Are you sure you want to delete this invoice?");

            confirmDialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {                    try {
                        invoiceService.deleteInvoice(currentInvoice.getInvoiceId());
                        invoiceList.remove(currentInvoice);
                        updateStatistics();
                        clearForm();
                        showAlert("Invoice deleted successfully!", "alert-success");
                    } catch (SQLException e) {
                        showAlert("Error deleting invoice: " + e.getMessage(), "alert-error");
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * Validate the form inputs
     */
    private boolean validateForm() {
        if (vendorComboBox.getValue() == null) {
            showAlert("Please select a vendor", "alert-error");
            vendorComboBox.requestFocus();
            return false;
        }

        if (dateField.getValue() == null) {
            showAlert("Please select a date", "alert-error");
            dateField.requestFocus();
            return false;
        }

        String amountText = amountField.getText().trim();
        if (amountText.isEmpty()) {
            showAlert("Total amount is required", "alert-error");
            amountField.requestFocus();
            return false;
        }

        try {
            BigDecimal amount = new BigDecimal(amountText);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                showAlert("Total amount must be greater than zero", "alert-error");
                amountField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid amount format", "alert-error");
            amountField.requestFocus();
            return false;
        }

        if (selectedFile == null && currentInvoice == null) {
            showAlert("Please select a receipt image", "alert-error");
            browseButton.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Populate the form with invoice data
     */
    private void populateForm(Invoice invoice) {
        // Find and select the vendor in the combo box
        for (Vendor vendor : vendorComboBox.getItems()) {
            if (vendor.getVendorId() == invoice.getVendorId()) {
                vendorComboBox.setValue(vendor);
                break;
            }
        }

        dateField.setValue(invoice.getCreationDate());
        amountField.setText(invoice.getTotalAmount().toString());
        imagePathField.setText(invoice.getReceiptImagePath() != null ?
                Paths.get(invoice.getReceiptImagePath()).getFileName().toString() : "");

        // Reset the selected file since we're loading existing data
        selectedFile = null;

        saveButton.setText("Update Invoice");
    }

    /**
     * Clear the form and reset selection
     */
    private void clearForm() {
        vendorComboBox.setValue(null);
        dateField.setValue(LocalDate.now());
        amountField.clear();
        imagePathField.clear();
        selectedFile = null;
        currentInvoice = null;
        invoiceTable.getSelectionModel().clearSelection();
        saveButton.setText("Save Invoice");
        deleteButton.setDisable(true);
    }    /**
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

    /**
     * Get the file extension from a filename
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return filename.substring(lastDotIndex);
        }
        return "";
    }
}

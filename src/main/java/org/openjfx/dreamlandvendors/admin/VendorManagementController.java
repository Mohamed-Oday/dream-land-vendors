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
import org.openjfx.dreamlandvendors.model.Vendor;
import org.openjfx.dreamlandvendors.service.VendorService;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Controller for the vendor management view
 */
public class VendorManagementController implements Initializable {

    @FXML
    private TextField nameField;

    @FXML
    private TextField phoneField;

    @FXML
    private Button saveButton;

    @FXML
    private Button clearButton;

    @FXML
    private Button deleteButton;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Vendor> vendorTable;

    @FXML
    private TableColumn<Vendor, Integer> idColumn;

    @FXML
    private TableColumn<Vendor, String> nameColumn;    
    @FXML
    private TableColumn<Vendor, String> phoneColumn;

    @FXML
    private HBox alertBox;    
    @FXML
    private Label alertMessage;

    @FXML
    private Text totalVendorsText;

    @FXML
    private Text alertIcon;

    private ObservableList<Vendor> vendorList = FXCollections.observableArrayList();
    private FilteredList<Vendor> filteredVendors;
    private Vendor currentVendor;

    private final VendorService vendorService = VendorService.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("vendorId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        // Set up search functionality
        filteredVendors = new FilteredList<>(vendorList, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredVendors.setPredicate(vendor -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (vendor.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (vendor.getPhoneNumber() != null && vendor.getPhoneNumber().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });

        vendorTable.setItems(filteredVendors);

        // Add selection listener to populate form when a vendor is selected
        vendorTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                currentVendor = newSelection;
                populateForm(currentVendor);
                deleteButton.setDisable(false);
            } else {
                clearForm();
                deleteButton.setDisable(true);
            }
        });

        // Load vendors from the database
        loadVendors();
    }    /**
     * Load vendors from the database
     */
    private void loadVendors() {
        try {
            vendorList.clear();
            vendorList.addAll(vendorService.getAllVendors());
            updateVendorCount();
        } catch (SQLException e) {
            showAlert("Error loading vendors: " + e.getMessage(), "alert-error");
            e.printStackTrace();
        }
    }

    /**
     * Update the vendor count display
     */
    private void updateVendorCount() {
        if (totalVendorsText != null) {
            totalVendorsText.setText(String.valueOf(vendorList.size()));
        }
    }

    /**
     * Handle save button action
     */
    @FXML
    private void handleSave() {
        if (validateForm()) {
            try {                if (currentVendor == null) {
                    // Create new vendor
                    Vendor vendor = new Vendor();
                    vendor.setName(nameField.getText().trim());
                    vendor.setPhoneNumber(phoneField.getText().trim());

                    int vendorId = vendorService.createVendor(vendor);
                    vendor.setVendorId(vendorId);
                    vendorList.add(vendor);
                    updateVendorCount();

                    showAlert("Vendor created successfully!", "alert-success");
                } else {
                    // Update existing vendor
                    currentVendor.setName(nameField.getText().trim());
                    currentVendor.setPhoneNumber(phoneField.getText().trim());

                    vendorService.updateVendor(currentVendor);

                    // Refresh the table
                    vendorTable.refresh();

                    showAlert("Vendor updated successfully!", "alert-success");
                }

                clearForm();
            } catch (SQLException e) {
                showAlert("Error saving vendor: " + e.getMessage(), "alert-error");
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
        if (currentVendor != null) {
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Confirm Delete");
            confirmDialog.setHeaderText("Delete Vendor");
            confirmDialog.setContentText("Are you sure you want to delete " + currentVendor.getName() + "?");

            confirmDialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {                    try {
                        vendorService.deleteVendor(currentVendor.getVendorId());
                        vendorList.remove(currentVendor);
                        updateVendorCount();
                        clearForm();
                        showAlert("Vendor deleted successfully!", "alert-success");
                    } catch (SQLException e) {
                        showAlert("Error deleting vendor: " + e.getMessage(), "alert-error");
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
        String name = nameField.getText().trim();

        if (name.isEmpty()) {
            showAlert("Vendor name is required", "alert-error");
            nameField.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Populate the form with vendor data
     */
    private void populateForm(Vendor vendor) {
        nameField.setText(vendor.getName());
        phoneField.setText(vendor.getPhoneNumber());
        saveButton.setText("Update Vendor");
    }

    /**
     * Clear the form and reset selection
     */
    private void clearForm() {
        nameField.clear();
        phoneField.clear();
        currentVendor = null;
        vendorTable.getSelectionModel().clearSelection();
        saveButton.setText("Save Vendor");
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
}

package org.openjfx.dreamlandvendors.vendor;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.HBox;
import org.openjfx.dreamlandvendors.model.User;
import org.openjfx.dreamlandvendors.model.Vendor;
import org.openjfx.dreamlandvendors.service.UserService;
import org.openjfx.dreamlandvendors.service.VendorService;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Controller for the vendor profile view
 */
public class VendorProfileController implements Initializable {

    @FXML
    private Label vendorIdLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private Label phoneLabel;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label fullNameLabel;

    @FXML
    private PasswordField currentPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button changePasswordButton;

    @FXML
    private HBox alertBox;

    @FXML
    private Label alertMessage;

    private final UserService userService = UserService.getInstance();
    private final VendorService vendorService = VendorService.getInstance();

    private User currentUser;
    private Vendor vendor;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Get the current user (vendor)
        currentUser = userService.getCurrentUser();

        // Load the vendor data
        loadVendorData();

        // Set the account information
        usernameLabel.setText(currentUser.getUsername());
        fullNameLabel.setText(currentUser.getFullName());
    }

    /**
     * Load vendor data from the database
     */
    private void loadVendorData() {
        try {
            // Get the vendor ID from the current user
            Integer vendorId = currentUser.getVendorId();

            if (vendorId != null) {
                vendor = vendorService.getVendorById(vendorId);

                if (vendor != null) {
                    // Set the vendor information
                    vendorIdLabel.setText(String.valueOf(vendor.getVendorId()));
                    nameLabel.setText(vendor.getName());
                    phoneLabel.setText(vendor.getPhoneNumber() != null ? vendor.getPhoneNumber() : "Not provided");
                }
            }
        } catch (SQLException e) {
            showAlert("Error loading vendor data: " + e.getMessage(), "alert-error");
            e.printStackTrace();
        }
    }

    /**
     * Handle change password button action
     */
    @FXML
    private void handleChangePassword() {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validate inputs
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("All password fields are required", "alert-error");
            return;
        }

        // Check if current password is correct
        if (!currentPassword.equals(currentUser.getPassword())) {
            showAlert("Current password is incorrect", "alert-error");
            currentPasswordField.clear();
            return;
        }

        // Check if new passwords match
        if (!newPassword.equals(confirmPassword)) {
            showAlert("New passwords do not match", "alert-error");
            newPasswordField.clear();
            confirmPasswordField.clear();
            return;
        }

        // Check password strength (minimum 6 characters)
        if (newPassword.length() < 6) {
            showAlert("New password must be at least 6 characters long", "alert-error");
            newPasswordField.clear();
            confirmPasswordField.clear();
            return;
        }

        try {
            // Update the password
            currentUser.setPassword(newPassword);
            userService.updateUser(currentUser);

            // Clear the password fields
            currentPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();

            showAlert("Password changed successfully", "alert-success");
        } catch (SQLException e) {
            showAlert("Error changing password: " + e.getMessage(), "alert-error");
            e.printStackTrace();
        }
    }

    /**
     * Show an alert message
     */
    private void showAlert(String message, String styleClass) {
        alertMessage.setText(message);
        alertBox.getStyleClass().removeAll("alert-success", "alert-error", "alert-warning");
        alertBox.getStyleClass().add(styleClass);
        alertBox.setVisible(true);
    }
}

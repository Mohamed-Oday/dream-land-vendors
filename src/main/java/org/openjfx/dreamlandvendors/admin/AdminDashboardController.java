package org.openjfx.dreamlandvendors.admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.openjfx.dreamlandvendors.service.UserService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the admin dashboard
 */
public class AdminDashboardController implements Initializable {

    @FXML
    private StackPane contentArea;

    @FXML
    private Text userNameText;

    @FXML
    private Button vendorsButton;

    @FXML
    private Button invoicesButton;

    @FXML
    private Button paymentsButton;

    @FXML
    private Button logoutButton;

    // Keep track of the active navigation button
    private Button activeButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set the user name
        userNameText.setText("Welcome, " + UserService.getInstance().getCurrentUser().getFullName());

        // Set vendors as the default view
        activeButton = vendorsButton;
        setActiveButton(vendorsButton);
        try {
            showVendorsView();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Show the vendors management view
     */
    @FXML
    private void showVendorsView() throws IOException {
        setActiveButton(vendorsButton);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("vendor-management.fxml"));
        Parent view = loader.load();
        setContent(view);
    }

    /**
     * Show the invoices management view
     */
    @FXML
    private void showInvoicesView() throws IOException {
        setActiveButton(invoicesButton);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("invoice-management.fxml"));
        Parent view = loader.load();
        setContent(view);
    }

    /**
     * Show the payments management view
     */
    @FXML
    private void showPaymentsView() throws IOException {
        setActiveButton(paymentsButton);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("payment-management.fxml"));
        Parent view = loader.load();
        setContent(view);
    }

    /**
     * Handle logout action
     */
    @FXML
    private void handleLogout() throws IOException {
        // Clear the current user
        UserService.getInstance().logout();

        // Load the login view
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../login/login-view.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        Stage stage = (Stage) logoutButton.getScene().getWindow();
        stage.setTitle("Dream Land Vendors - Login");
        stage.setScene(scene);
    }

    /**
     * Set the content of the main area
     */
    private void setContent(Parent content) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(content);
    }

    /**
     * Set the active navigation button
     */
    private void setActiveButton(Button button) {
        // Remove active class from current active button
        if (activeButton != null) {
            activeButton.getStyleClass().remove("active");
        }

        // Add active class to the selected button
        button.getStyleClass().add("active");
        activeButton = button;
    }
}

package org.openjfx.dreamlandvendors.vendor;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.openjfx.dreamlandvendors.model.User;
import org.openjfx.dreamlandvendors.service.UserService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the vendor dashboard
 */
public class VendorDashboardController implements Initializable {

    @FXML
    private StackPane contentArea;

    @FXML
    private Text userNameText;

    @FXML
    private Button profileButton;

    @FXML
    private Button invoicesButton;

    @FXML
    private Button logoutButton;

    // Keep track of the active navigation button
    private Button activeButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Get the current user (vendor)
        User currentUser = UserService.getInstance().getCurrentUser();

        // Set the user name
        userNameText.setText("Welcome, " + currentUser.getFullName());

        // Set profile as the default view
        activeButton = profileButton;
        setActiveButton(profileButton);
        try {
            showProfileView();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Show the vendor profile view
     */
    @FXML
    private void showProfileView() throws IOException {
        setActiveButton(profileButton);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("vendor-profile.fxml"));
        Parent view = loader.load();
        setContent(view);
    }

    /**
     * Show the vendor invoices view
     */
    @FXML
    private void showInvoicesView() throws IOException {
        setActiveButton(invoicesButton);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("vendor-invoices.fxml"));
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

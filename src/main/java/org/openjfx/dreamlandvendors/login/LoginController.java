package org.openjfx.dreamlandvendors.login;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.openjfx.dreamlandvendors.model.User;
import org.openjfx.dreamlandvendors.service.UserService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private VBox loginFormContainer;

    @FXML
    private Label errorLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Hide error label initially
        errorLabel.setVisible(false);

        // Auto-focus on username field when the form loads
        Platform.runLater(() -> usernameField.requestFocus());

        // Set up keyboard shortcuts for login
        loginFormContainer.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ENTER) {
                handleLogin();
            }
        });
    }    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        // Validate input fields
        if (username.isEmpty() || password.isEmpty()) {
            showError("Username and password are required");
            return;
        }

        // Authenticate user
        User user = UserService.getInstance().authenticate(username, password);

        if (user != null) {
            // Use Platform.runLater to ensure the scene graph is fully initialized
            Platform.runLater(() -> {
                try {
                    // Load appropriate dashboard based on user role
                    if (UserService.getInstance().isAdmin(user)) {
                        loadAdminDashboard();
                    } else if (UserService.getInstance().isVendor(user)) {
                        loadVendorDashboard();
                    } else {
                        showError("Unknown user role");
                    }
                } catch (IOException e) {
                    showError("Error loading dashboard: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } else {
            showError("Invalid username or password");
        }
    }    private void loadAdminDashboard() throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openjfx/dreamlandvendors/admin/admin-dashboard.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            // Get the current stage with better error handling
            Stage currentStage = getCurrentStage();
            Stage targetStage;
            
            if (currentStage != null) {
                // Use the existing stage
                targetStage = currentStage;
                System.out.println("Using existing stage for admin dashboard");
            } else {
                // Create a new stage as fallback
                targetStage = new Stage();
                System.out.println("Created new stage as fallback for admin dashboard");
            }

            // Configure the stage for full window display
            targetStage.setTitle("Dream Land Vendors - Admin Dashboard");
            targetStage.setScene(scene);
            
            // Set minimum size to ensure UI doesn't break on resize (same as login)
            targetStage.setMinWidth(1024);
            targetStage.setMinHeight(768);
            
            // Get screen dimensions and set window to fill the screen
            javafx.stage.Screen screen = javafx.stage.Screen.getPrimary();
            javafx.geometry.Rectangle2D bounds = screen.getVisualBounds();
            
            // Set window position and size to fill the screen
            targetStage.setX(bounds.getMinX());
            targetStage.setY(bounds.getMinY());
            targetStage.setWidth(bounds.getWidth());
            targetStage.setHeight(bounds.getHeight());
            
            // Also try to maximize (as a backup)
            targetStage.setMaximized(true);
            
            // Ensure the stage is visible
            if (!targetStage.isShowing()) {
                targetStage.show();
            }
            
            // Force maximize again after showing and set full screen dimensions
            Platform.runLater(() -> {
                targetStage.setMaximized(true);
                // Force full screen dimensions again in case maximized didn't work
                targetStage.setX(bounds.getMinX());
                targetStage.setY(bounds.getMinY());
                targetStage.setWidth(bounds.getWidth());
                targetStage.setHeight(bounds.getHeight());
            });
            
            System.out.println("Admin dashboard loaded successfully with dimensions: " + bounds.getWidth() + "x" + bounds.getHeight());
        } catch (Exception e) {
            System.err.println("Error in loadAdminDashboard: " + e.getMessage());
            e.printStackTrace();
            showError("Error loading dashboard: " + e.getMessage());
        }
    }private Stage getCurrentStage() {
        try {
            System.out.println("Attempting to get current stage...");
            
            // Try multiple approaches to get the stage
            
            // First try: loginButton scene
            if (loginButton != null) {
                System.out.println("LoginButton is not null");
                Scene scene = loginButton.getScene();
                if (scene != null) {
                    System.out.println("LoginButton scene is not null");
                    Window window = scene.getWindow();
                    if (window != null) {
                        System.out.println("LoginButton window is not null, type: " + window.getClass().getSimpleName());
                        if (window instanceof Stage) {
                            System.out.println("Successfully got stage from loginButton");
                            return (Stage) window;
                        }
                    } else {
                        System.out.println("LoginButton window is null");
                    }
                } else {
                    System.out.println("LoginButton scene is null");
                }
            } else {
                System.out.println("LoginButton is null");
            }

            // Second try: loginFormContainer scene
            if (loginFormContainer != null) {
                System.out.println("LoginFormContainer is not null");
                Scene scene = loginFormContainer.getScene();
                if (scene != null) {
                    System.out.println("LoginFormContainer scene is not null");
                    Window window = scene.getWindow();
                    if (window != null && window instanceof Stage) {
                        System.out.println("Successfully got stage from loginFormContainer");
                        return (Stage) window;
                    }
                }
            }

            // Third try: usernameField scene
            if (usernameField != null) {
                Scene scene = usernameField.getScene();
                if (scene != null) {
                    Window window = scene.getWindow();
                    if (window != null && window instanceof Stage) {
                        System.out.println("Successfully got stage from usernameField");
                        return (Stage) window;
                    }
                }
            }            // Fourth try: passwordField scene
            if (passwordField != null) {
                Scene scene = passwordField.getScene();
                if (scene != null) {
                    Window window = scene.getWindow();
                    if (window != null && window instanceof Stage) {
                        System.out.println("Successfully got stage from passwordField");
                        return (Stage) window;
                    }
                }
            }

            // Fifth try: Use Window.getWindows() to find any open stage
            System.out.println("Trying Window.getWindows() approach...");
            for (Window window : Stage.getWindows()) {
                if (window instanceof Stage && window.isShowing()) {
                    System.out.println("Found open stage using Window.getWindows()");
                    return (Stage) window;
                }
            }

            System.err.println("All attempts to get current stage failed");
            return null;
        } catch (Exception e) {
            System.err.println("Exception while getting current stage: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }    private void loadVendorDashboard() throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openjfx/dreamlandvendors/vendor/vendor-dashboard.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            // Get the current stage with better error handling
            Stage currentStage = getCurrentStage();
            Stage targetStage;
            
            if (currentStage != null) {
                // Use the existing stage
                targetStage = currentStage;
                System.out.println("Using existing stage for vendor dashboard");
            } else {
                // Create a new stage as fallback
                targetStage = new Stage();
                System.out.println("Created new stage as fallback for vendor dashboard");
            }

            // Configure the stage for full window display
            targetStage.setTitle("Dream Land Vendors - Vendor Dashboard");
            targetStage.setScene(scene);
            
            // Set minimum size to ensure UI doesn't break on resize (same as login)
            targetStage.setMinWidth(1024);
            targetStage.setMinHeight(768);
            
            // Get screen dimensions and set window to fill the screen
            javafx.stage.Screen screen = javafx.stage.Screen.getPrimary();
            javafx.geometry.Rectangle2D bounds = screen.getVisualBounds();
            
            // Set window position and size to fill the screen
            targetStage.setX(bounds.getMinX());
            targetStage.setY(bounds.getMinY());
            targetStage.setWidth(bounds.getWidth());
            targetStage.setHeight(bounds.getHeight());
            
            // Also try to maximize (as a backup)
            targetStage.setMaximized(true);
            
            // Ensure the stage is visible
            if (!targetStage.isShowing()) {
                targetStage.show();
            }
            
            // Force maximize again after showing and set full screen dimensions
            Platform.runLater(() -> {
                targetStage.setMaximized(true);
                // Force full screen dimensions again in case maximized didn't work
                targetStage.setX(bounds.getMinX());
                targetStage.setY(bounds.getMinY());
                targetStage.setWidth(bounds.getWidth());
                targetStage.setHeight(bounds.getHeight());
            });
            
            System.out.println("Vendor dashboard loaded successfully with dimensions: " + bounds.getWidth() + "x" + bounds.getHeight());
        } catch (Exception e) {
            System.err.println("Error in loadVendorDashboard: " + e.getMessage());
            e.printStackTrace();
            showError("Error loading dashboard: " + e.getMessage());
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    @FXML
    private void handleExit() {
        Platform.exit();
    }
}

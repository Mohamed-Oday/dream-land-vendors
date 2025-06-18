package org.openjfx.dreamlandvendors;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.openjfx.dreamlandvendors.util.DataInitializer;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Initialize default data with the admin user
        DataInitializer.initializeDefaultData();

        // Load the login view instead of hello-view
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        // Add the stylesheet
        scene.getStylesheets().add(HelloApplication.class.getResource("styles/dashboard.css").toExternalForm());

        // Set application title
        stage.setTitle("Dream Land Vendors - Payment Management System");

        // Set the stage style to UNIFIED to have window controls at the bottom
        stage.initStyle(StageStyle.UNIFIED);

        // Make the window fill the screen (not truly fullscreen, but maximized)
        stage.setMaximized(true);

        // Set the scene to the stage and show it
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
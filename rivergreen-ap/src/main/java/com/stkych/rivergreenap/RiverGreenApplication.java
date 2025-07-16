package com.stkych.rivergreenap;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main application class for the application.
 * Initializes the application and loads the main scene.
 */
public class RiverGreenApplication extends Application {
    // FALLBACK patient number if none is provided
    private static int patientNumber = -1; // Default value

    /**
     * Starts the application.
     * Initializes the SceneSwitcher and loads the main scene.
     * 
     * @param stage The primary stage for this application
     * @throws IOException If the FXML file cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        if (patientNumber != -1) {
            // Initialize the SceneSwitcher with the primary stage
            SceneSwitcher.initialize(stage);
            // Store the patient number in the data cache for the controller to use
            SceneSwitcher.putData("patientNumber", patientNumber);
            // Switch to the main scene (using the new GUI)
            SceneSwitcher.switchScene("main", "RiverGreen Dental Application");
        } else {
            // Display a simple error alert instead of loading an error scene
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Patient Number");
            alert.setContentText("Please provide a valid patient number.");
            alert.showAndWait();
            System.exit(1);
        }

    }

    /**
     * Main method to launch the application.
     * Patient number should be the first argument.
     * 
     * @param args Command line arguments. If provided, the first argument should be the patient number.
     */
    public static void main(String[] args) {
        // Check if a patient number was provided as a command line argument
        if (args.length > 0) {
            try {
                patientNumber = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid patient number format. Using default: " + patientNumber);
            }
        }
        launch(args);
    }
}

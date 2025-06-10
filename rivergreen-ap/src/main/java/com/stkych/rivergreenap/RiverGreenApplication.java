package com.stkych.rivergreenap;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main application class for the RiverGreen Dental Application.
 * Initializes the application and loads the main scene.
 */
public class RiverGreenApplication extends Application {

    /**
     * Starts the application.
     * Initializes the SceneSwitcher and loads the main scene.
     * 
     * @param stage The primary stage for this application
     * @throws IOException If the FXML file cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        // Initialize the SceneSwitcher with the primary stage
        SceneSwitcher.initialize(stage);

        // Switch to the main scene
        SceneSwitcher.switchScene("sceneMain", "RiverGreen Dental Application");
    }

    /**
     * Main method to launch the application.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}

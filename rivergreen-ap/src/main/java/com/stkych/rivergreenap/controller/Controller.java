package com.stkych.rivergreenap.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import com.stkych.rivergreenap.SceneSwitcher;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Base abstract class for all controllers in the application.
 * Provides common functionality and ensures a consistent approach across all controllers.
 * Implements Initializable to provide a standard initialization method.
 */
public abstract class Controller implements Initializable {

    /**
     * Initializes the controller.
     * This method is called automatically after the FXML file has been loaded.
     * 
     * @param location The location used to resolve relative paths for the root object
     * @param resources The resources used to localize the root object
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Default implementation - can be overridden by subclasses
    }

    /**
     * Closes the current window (popup).
     * This is useful for controllers that are displayed in popup windows.
     */
    protected void closePopup() {
        // Get the current stage from any control in the controller
        // This assumes that the controller has at least one @FXML annotated control
        javafx.scene.Node node = null;
        try {
            // Use reflection to get the first @FXML annotated field that is a Node
            for (java.lang.reflect.Field field : getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(FXML.class) && javafx.scene.Node.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    node = (javafx.scene.Node) field.get(this);
                    if (node != null) break;
                }
            }
        } catch (Exception e) {
            handleError(e);
        }

        if (node != null) {
            javafx.stage.Stage stage = (javafx.stage.Stage) node.getScene().getWindow();
            stage.close();
        }
    }

    /**
     * Navigates to the main scene.
     * 
     * @throws IOException If the FXML file cannot be loaded
     */
    protected void navigateToMain() throws IOException {
        SceneSwitcher.switchScene("sceneMain", "RiverGreen Dental Application");
    }

    /**
     * Navigates to the configuration scene.
     * 
     * @throws IOException If the FXML file cannot be loaded
     */
    protected void navigateToConfig() throws IOException {
        SceneSwitcher.switchScene("sceneConfig", "Configuration");
    }

    /**
     * Shows the configuration edit dialog as a popup.
     *
     * @throws IOException If the FXML file cannot be loaded
     */
    protected void popupConfigEdit() throws IOException {
        SceneSwitcher.showPopup("sceneConfigEdit", "Edit Configuration");
    }

    /**
     * Shows the configuration new dialog as a popup.
     *
     * @throws IOException If the FXML file cannot be loaded
     */
    protected void popupConfigNew() throws IOException {
        SceneSwitcher.showPopup("sceneConfigNew", "New Configuration");
    }
    /**
     * Handles errors that occur during controller operations.
     * 
     * @param e The exception that occurred
     */
    protected void handleError(Exception e) {
        // Log the error
        System.err.println("Error in controller: " + e.getMessage());
        e.printStackTrace();

        // In a real application, you might want to show an error dialog to the user
    }
}

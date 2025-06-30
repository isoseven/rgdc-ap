package com.stkych.rivergreenap.controller.archive;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.SelectionMode;
import com.stkych.rivergreenap.SceneSwitcher;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the configuration scene of the application.
 * Handles user interactions and navigation from the configuration screen.
 */
public class ControllerOutConfig extends ControllerOld {

    @FXML
    private Button backButton;

    @FXML
    private Button newButton;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    @FXML
    private TableView<?> configTableView;

    /**
     * Initializes the controller.
     * This method is called automatically after the FXML file has been loaded.
     * 
     * @param location The location used to resolve relative paths for the root object
     * @param resources The resources used to localize the root object
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize the controller
        // This might include setting up table columns, loading configuration data, etc.

        // Set the TableView selection mode to allow multiple selection
        configTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /**
     * Handles the back button click event.
     * Navigates back to the main screen.
     */
    @FXML
    private void handleBackButtonAction() {
        try {
            navigateToMain();
        } catch (IOException e) {
            handleError(e);
        }
    }

    /**
     * Handles the new button click event.
     * Opens a popup window for creating a new configuration.
     */
    @FXML
    private void handleNewButtonAction() {
        try {
            popupConfigNew();
        } catch (IOException e) {
            handleError(e);
        }
    }

    /**
     * Handles the edit button click event.
     * Opens a popup window for editing the selected configuration.
     */
    @FXML
    private void handleEditButtonAction() {
        // Get the selected configuration from the table
        Object selectedConfig = configTableView.getSelectionModel().getSelectedItem();

        if (selectedConfig != null) {
            try {
                // Store the selected configuration for retrieval by the edit screen
                SceneSwitcher.putData("selectedConfig", selectedConfig);
                popupConfigEdit();
            } catch (IOException e) {
                handleError(e);
            }
        } else {
            // Show an error message or alert that no configuration is selected
            System.out.println("No configuration selected");
        }
    }

    /**
     * Handles the delete button click event.
     * Deletes the selected configuration after confirmation.
     */
    @FXML
    private void handleDeleteButtonAction() {
        // Get the selected configuration from the table
        Object selectedConfig = configTableView.getSelectionModel().getSelectedItem();

        if (selectedConfig != null) {
            // In a real application, you would show a confirmation dialog
            // and then delete the configuration if confirmed
            System.out.println("Deleting configuration: " + selectedConfig);

            // Remove the configuration from the table
            // configTableView.getItems().remove(selectedConfig);

            // Save the changes to the data store
        } else {
            // Show an error message or alert that no configuration is selected
            System.out.println("No configuration selected");
        }
    }
}

package com.stkych.rivergreenap.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the main scene of the application.
 * Handles user interactions and navigation from the main screen.
 */
public class MainController extends Controller {
    
    @FXML
    private Button menuButton;
    
    @FXML
    private Button configButton;
    
    @FXML
    private Button okButton;
    
    @FXML
    private Button cancelButton;
    
    @FXML
    private TableView<?> leftTableView;
    
    @FXML
    private TableView<?> rightTableView;
    
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
        // This might include setting up table columns, loading data, etc.
    }
    
    /**
     * Handles the menu button click event.
     * This would typically open a menu or navigate to a menu screen.
     */
    @FXML
    private void handleMenuButtonAction() {
        // Handle menu button click
        System.out.println("Menu button clicked");
        // In a real application, this might open a dropdown menu or navigate to a menu screen
    }
    
    /**
     * Handles the config button click event.
     * Navigates to the configuration screen.
     */
    @FXML
    private void handleConfigButtonAction() {
        try {
            navigateToConfig();
        } catch (IOException e) {
            handleError(e);
        }
    }
    
    /**
     * Handles the OK button click event.
     * This would typically save changes and close the screen or navigate to another screen.
     */
    @FXML
    private void handleOkButtonAction() {
        // Handle OK button click
        System.out.println("OK button clicked");
        // In a real application, this might save changes and close the screen
    }
    
    /**
     * Handles the Cancel button click event.
     * This would typically discard changes and close the screen or navigate to another screen.
     */
    @FXML
    private void handleCancelButtonAction() {
        // Handle Cancel button click
        System.out.println("Cancel button clicked");
        // In a real application, this might discard changes and close the screen
    }
}